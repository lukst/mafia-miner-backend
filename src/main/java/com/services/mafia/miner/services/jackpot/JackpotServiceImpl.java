package com.services.mafia.miner.services.jackpot;

import com.services.mafia.miner.dto.jackpot.JackpotDTO;
import com.services.mafia.miner.dto.jackpot.JackpotHistoryDTO;
import com.services.mafia.miner.dto.jackpot.JackpotResultDTO;
import com.services.mafia.miner.dto.jackpot.WinningCategoryDTO;
import com.services.mafia.miner.dto.nft.NFTDTO;
import com.services.mafia.miner.entity.jackpot.*;
import com.services.mafia.miner.entity.nft.FarmType;
import com.services.mafia.miner.entity.nft.NFT;
import com.services.mafia.miner.entity.nft.NFTCatalog;
import com.services.mafia.miner.entity.transaction.TransactionType;
import com.services.mafia.miner.entity.user.User;
import com.services.mafia.miner.exception.InvalidInputException;
import com.services.mafia.miner.repository.jackpot.JackpotHistoryRepository;
import com.services.mafia.miner.repository.jackpot.JackpotRepository;
import com.services.mafia.miner.repository.jackpot.WinningCategoryRepository;
import com.services.mafia.miner.repository.nft.NFTCatalogRepository;
import com.services.mafia.miner.repository.nft.NFTRepository;
import com.services.mafia.miner.services.RateLimitingCacheService;
import com.services.mafia.miner.services.transaction.TransactionService;
import com.services.mafia.miner.services.user.UserService;
import com.services.mafia.miner.util.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Log4j2
public class JackpotServiceImpl implements JackpotService {
    private final JackpotRepository jackpotRepository;
    private final WinningCategoryRepository winningCategoryRepository;
    private final UserService userService;
    private final TransactionService transactionService;
    private final JackpotHistoryRepository jackpotHistoryRepository;
    private final NFTRepository nftRepository;
    private final ModelMapper modelMapper;
    private final NFTCatalogRepository nftCatalogRepository;
    private final Random random = new Random();
    private final RateLimitingCacheService rateLimitingCacheService;

    @Override
    @Transactional
    public JackpotResultDTO playJackpot(HttpServletRequest request) {
        User user = userService.findUserByToken(userService.extractTokenFromRequest(request));
        Jackpot jackpot = findFirstWithLock();
        BigDecimal jackpotEntryPrice = jackpot.getBaseEntryPrice();
        NFT soldatoNFT = null;
        BigDecimal winAmount = BigDecimal.ZERO;
        rateLimitingCacheService.checkRateLimitForJackpotPlay(user.getId());
        int generatedNumber = random.nextInt(1000) + 1;
        WinningCategory winningCategory = winningCategoryRepository.findByRangeStartLessThanEqualAndRangeEndGreaterThanEqual(
                        generatedNumber, generatedNumber)
                .orElseThrow(() -> new InvalidInputException("No winning category result"));
        userService.subtractUserMCOIN(user, jackpotEntryPrice);
        transactionService.saveTransactionRecordMCOIN(TransactionType.JACKPOT_ENTRY, user, jackpotEntryPrice.negate(), Constants.PLAY_JACKPOT_OPERATION);
        JackpotRewardType result = null;
        log.info("User " + user.getWalletAddress() + " " + winningCategory.getRewardType() + " number " + generatedNumber);
        switch (winningCategory.getRewardType()) {
            case NOTHING -> {
                Jackpot updatedJackpot = findByIdWithLock(jackpot.getId());
                updatedJackpot.setCurrentAmount(updatedJackpot.getCurrentAmount().add(jackpotEntryPrice.multiply(new BigDecimal("0.5"))));
                updatedJackpot.setLastUpdated(LocalDateTime.now());
                jackpotRepository.save(updatedJackpot);
                transactionService.saveTransactionRecordMCOIN(TransactionType.JACKPOT_NOTHING, user, BigDecimal.ZERO, Constants.JACKPOT_NO_REWARD_OPERATION);
                result = JackpotRewardType.NOTHING;
            }
            case X1point5 -> {
                Jackpot updatedJackpot = findByIdWithLock(jackpot.getId());
                updatedJackpot.setLastUpdated(LocalDateTime.now());
                jackpotRepository.save(updatedJackpot);
                BigDecimal amountWon = jackpotEntryPrice.multiply(new BigDecimal("1.5"));
                user.setMcoinBalance(user.getMcoinBalance().add(amountWon));
                transactionService.saveTransactionRecordMCOIN(TransactionType.JACKPOT_X1_POINT_5, user, amountWon, Constants.WIN_1_OPERATION);
                result = JackpotRewardType.X1point5;
                winAmount = amountWon;
                jackpotHistoryRepository.save(JackpotHistory.builder()
                        .user(user)
                        .amountWon(winAmount)
                        .timestamp(LocalDateTime.now())
                        .winningCategory(winningCategory)
                        .rollNumber(generatedNumber)
                        .build());
            }
            case X2 -> {
                Jackpot updatedJackpot = findByIdWithLock(jackpot.getId());
                updatedJackpot.setLastUpdated(LocalDateTime.now());
                jackpotRepository.save(updatedJackpot);
                BigDecimal amountWon = jackpotEntryPrice.multiply(new BigDecimal("2"));
                user.setMcoinBalance(user.getMcoinBalance().add(amountWon));
                transactionService.saveTransactionRecordMCOIN(TransactionType.JACKPOT_X2, user, amountWon, Constants.WIN_2_OPERATION);
                result = JackpotRewardType.X2;
                winAmount = amountWon;
                jackpotHistoryRepository.save(JackpotHistory.builder()
                        .user(user)
                        .amountWon(winAmount)
                        .timestamp(LocalDateTime.now())
                        .winningCategory(winningCategory)
                        .rollNumber(generatedNumber)
                        .build());
            }
            case X3 -> {
                Jackpot updatedJackpot = findByIdWithLock(jackpot.getId());
                updatedJackpot.setLastUpdated(LocalDateTime.now());
                jackpotRepository.save(updatedJackpot);
                BigDecimal amountWon = jackpotEntryPrice.multiply(new BigDecimal("3"));
                user.setMcoinBalance(user.getMcoinBalance().add(amountWon));
                transactionService.saveTransactionRecordMCOIN(TransactionType.JACKPOT_X3, user, amountWon, Constants.WIN_3_OPERATION);
                result = JackpotRewardType.X3;
                winAmount = amountWon;
                jackpotHistoryRepository.save(JackpotHistory.builder()
                        .user(user)
                        .amountWon(winAmount)
                        .timestamp(LocalDateTime.now())
                        .winningCategory(winningCategory)
                        .rollNumber(generatedNumber)
                        .build());
            }
            case X5 -> {
                Jackpot updatedJackpot = findByIdWithLock(jackpot.getId());
                updatedJackpot.setLastUpdated(LocalDateTime.now());
                jackpotRepository.save(updatedJackpot);
                BigDecimal amountWon = jackpotEntryPrice.multiply(new BigDecimal("5"));
                user.setMcoinBalance(user.getMcoinBalance().add(amountWon));
                transactionService.saveTransactionRecordMCOIN(TransactionType.JACKPOT_X5, user, amountWon, Constants.WIN_5_OPERATION);
                result = JackpotRewardType.X5;
                winAmount = amountWon;
                jackpotHistoryRepository.save(JackpotHistory.builder()
                        .user(user)
                        .amountWon(winAmount)
                        .timestamp(LocalDateTime.now())
                        .winningCategory(winningCategory)
                        .rollNumber(generatedNumber)
                        .build());
            }
            case X10 -> {
                Jackpot updatedJackpot = findByIdWithLock(jackpot.getId());
                updatedJackpot.setLastUpdated(LocalDateTime.now());
                jackpotRepository.save(updatedJackpot);
                BigDecimal amountWon = jackpotEntryPrice.multiply(new BigDecimal("10"));
                user.setMcoinBalance(user.getMcoinBalance().add(amountWon));
                transactionService.saveTransactionRecordMCOIN(TransactionType.JACKPOT_X10, user, amountWon, Constants.WIN_10_OPERATION);
                result = JackpotRewardType.X10;
                winAmount = amountWon;
                jackpotHistoryRepository.save(JackpotHistory.builder()
                        .user(user)
                        .amountWon(winAmount)
                        .timestamp(LocalDateTime.now())
                        .winningCategory(winningCategory)
                        .rollNumber(generatedNumber)
                        .build());
            }
            case SOLDATO -> {
                Jackpot updatedJackpot = findByIdWithLock(jackpot.getId());
                updatedJackpot.setLastUpdated(LocalDateTime.now());
                jackpotRepository.save(updatedJackpot);
                winAmount = new BigDecimal("1");
                transactionService.saveTransactionRecordMCOIN(TransactionType.JACKPOT_SOLDATO, user, winAmount, Constants.WIN_SOLDATO_OPERATION);
                result = JackpotRewardType.SOLDATO;
                NFTCatalog nftCatalogSoldato = nftCatalogRepository.findNftCatalogByName("Soldato").orElseThrow(() ->
                        new InvalidInputException("No soldato available"));
                int minDays = nftCatalogSoldato.getMinFarmDays();
                int maxDays = nftCatalogSoldato.getMaxFarmDays();
                int availableMiningDays = random.nextInt(maxDays - minDays + 1) + minDays;
                soldatoNFT = nftRepository.save(NFT.builder()
                        .user(user)
                        .nftCatalog(nftCatalogSoldato)
                        .farmType(FarmType.MCOIN)
                        .availableMiningDays(availableMiningDays)
                        .maxMiningDays(availableMiningDays)
                        .build());
                jackpotHistoryRepository.save(JackpotHistory.builder()
                        .user(user)
                        .amountWon(winAmount)
                        .soldatoNFT(soldatoNFT.getId())
                        .timestamp(LocalDateTime.now())
                        .winningCategory(winningCategory)
                        .rollNumber(generatedNumber)
                        .build());
            }
            case JACKPOT -> {
                Jackpot updatedJackpot = findByIdWithLock(jackpot.getId());
                updatedJackpot.setLastUpdated(LocalDateTime.now());
                updatedJackpot.setStatus(JackpotStatus.CLOSED);
                jackpotRepository.save(updatedJackpot);
                BigDecimal jackpotWinAmount = updatedJackpot.getCurrentAmount();
                user.setMcoinBalance(user.getMcoinBalance().add(jackpotWinAmount));
                transactionService.saveTransactionRecordMCOIN(TransactionType.JACKPOT_WIN, user, jackpotWinAmount, Constants.WIN_JACKPOT_OPERATION);
                Jackpot nextJackpot = Jackpot.builder().build();
                jackpotRepository.save(nextJackpot);
                result = JackpotRewardType.JACKPOT;
                winAmount = jackpotWinAmount;
                jackpotHistoryRepository.save(JackpotHistory.builder()
                        .user(user)
                        .amountWon(winAmount)
                        .timestamp(LocalDateTime.now())
                        .winningCategory(winningCategory)
                        .rollNumber(generatedNumber)
                        .build());
            }
        }
        return JackpotResultDTO.builder()
                .mcoin(user.getMcoinBalance())
                .nftSoldato(soldatoNFT != null ? modelMapper.map(soldatoNFT, NFTDTO.class) : null)
                .result(result)
                .winAmount(winAmount)
                .rollNumber(generatedNumber)
                .build();
    }

    @Override
    public List<JackpotHistoryDTO> getLast30JackpotHistories() {
        List<JackpotHistory> jackpotHistories = jackpotHistoryRepository.findTop30ByOrderByTimestampDesc();
        List<JackpotHistoryDTO> rouletteHistoryDTOS = new ArrayList<>();
        for (JackpotHistory jackpotHistory : jackpotHistories) {
            NFT soldatoNFT = null;
            if (jackpotHistory.getSoldatoNFT() != null) {
                soldatoNFT = nftRepository.findById(jackpotHistory.getSoldatoNFT()).orElse(null);
            }
            rouletteHistoryDTOS.add(
                    JackpotHistoryDTO.builder()
                            .nftSoldato(soldatoNFT != null ? modelMapper.map(soldatoNFT, NFTDTO.class) : null)
                            .amountWon(jackpotHistory.getAmountWon())
                            .username(jackpotHistory.getUser().getWalletAddress())
                            .timestamp(jackpotHistory.getTimestamp())
                            .rollNumber(jackpotHistory.getRollNumber())
                            .winningCategory(modelMapper.map(jackpotHistory.getWinningCategory(), WinningCategoryDTO.class))
                            .build()
            );
        }
        return rouletteHistoryDTOS;
    }

    @Override
    public List<WinningCategoryDTO> getAllWinningCategories() {
        List<WinningCategory> winningCategories = winningCategoryRepository.findAll();
        return winningCategories.stream().map(winningCategory -> modelMapper.map(winningCategory, WinningCategoryDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Jackpot findFirstWithLock() {
        return jackpotRepository.findFirstByOrderByIdDesc()
                .orElseThrow(() -> new InvalidInputException("No Jackpot available"));
    }

    @Override
    public Jackpot findByIdWithLock(Long id) {
        return jackpotRepository.findById(id)
                .orElseThrow(() -> new InvalidInputException("No Jackpot available"));
    }

    @Override
    public JackpotDTO findJackpotByIdOrLastOngoing(Optional<Long> jackpotId) {
        Jackpot jackpot;
        if (jackpotId.isPresent()) {
            jackpot = jackpotRepository.findById(jackpotId.get())
                    .orElseThrow(() -> new InvalidInputException("Jackpot does not exist"));
        } else {
            jackpot = jackpotRepository.findFirstByStatusOrderByIdDesc(JackpotStatus.ONGOING)
                    .orElseThrow(() -> new InvalidInputException("No jackpot open yet"));
        }
        return modelMapper.map(jackpot, JackpotDTO.class);
    }
}
