package com.services.mafia.miner.services.transaction;

import com.services.mafia.miner.dto.game.TotalStatsDTO;
import com.services.mafia.miner.dto.transaction.ReferralHistoryDTO;
import com.services.mafia.miner.dto.transaction.TransactionDTO;
import com.services.mafia.miner.entity.transaction.Transaction;
import com.services.mafia.miner.entity.transaction.TransactionType;
import com.services.mafia.miner.entity.user.User;
import com.services.mafia.miner.repository.nft.NFTRepository;
import com.services.mafia.miner.repository.transaction.TransactionRepository;
import com.services.mafia.miner.repository.user.UserRepository;
import com.services.mafia.miner.services.user.UserService;
import com.services.mafia.miner.util.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final NFTRepository nftRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    public Page<TransactionDTO> filterTransactionsForUser(HttpServletRequest request, int page, int size) {
        User user = userService.findUserByToken(userService.extractTokenFromRequest(request));
        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        Page<Transaction> transactions = transactionRepository.findAllByUser(user, pageable);
        return transactions.map(transaction -> modelMapper.map(transaction, TransactionDTO.class));
    }

    @Override
    @Transactional
    public void saveTransactionRecordBNB(TransactionType transactionType, User userFound, BigDecimal bnb, String operation) {
        Transaction transactionRecord = new Transaction();
        transactionRecord.setTransactionType(transactionType);
        transactionRecord.setTransactionDate(LocalDateTime.now());
        transactionRecord.setUser(userFound);
        transactionRecord.setBlockchainTransaction(false);
        transactionRecord.setPendingValidation(false);
        transactionRecord.setBnb(bnb.setScale(5, RoundingMode.HALF_UP));
        transactionRecord.setMcoin(BigDecimal.ZERO);
        transactionRecord.setOperation(operation);
        transactionRepository.save(transactionRecord);
    }

    @Override
    @Transactional
    public void saveTransactionRecordMCOIN(TransactionType transactionType, User userFound, BigDecimal mcoin, String operation) {
        Transaction transactionRecord = new Transaction();
        transactionRecord.setTransactionType(transactionType);
        transactionRecord.setTransactionDate(LocalDateTime.now());
        transactionRecord.setUser(userFound);
        transactionRecord.setBlockchainTransaction(false);
        transactionRecord.setPendingValidation(false);
        transactionRecord.setBnb(BigDecimal.ZERO);
        transactionRecord.setMcoin(mcoin.setScale(5, RoundingMode.HALF_UP));
        transactionRecord.setOperation(operation);
        transactionRepository.save(transactionRecord);
    }

    @Override
    public TotalStatsDTO getTotalStats() {
        return TotalStatsDTO.builder()
                .totalDeposit(userRepository.sumTotalDeposit())
                .totalUsers(userRepository.findAll().size())
                .totalNfts(nftRepository.findAll().size())
                .build();
    }

    @Override
    public Page<TransactionDTO> getAllTransactionsForUser(String wallet, int page, int size) {
        User user = userService.findUserByWallet(wallet);
        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        Page<Transaction> allTransactionsForUser = transactionRepository.findAllByUser(user, pageable);
        return allTransactionsForUser.map(transaction -> modelMapper.map(transaction, TransactionDTO.class));
    }

    @Override
    public List<ReferralHistoryDTO> getAllReferalHistoryForUser(HttpServletRequest request) {
        User userFound = userService.findUserByToken(userService.extractTokenFromRequest(request));
        List<ReferralHistoryDTO> referralHistoryDTOS = new ArrayList<>();
        List<User> referees = userService.findAllByReferrer(userFound);
        for (User referrer : referees) {
            List<Transaction> mintedNFTsBNB = transactionRepository.findByTransactionTypeAndUser(TransactionType.MINT_NFT_BNB, referrer);
            BigDecimal referralAmountBNB = mintedNFTsBNB.stream()
                    .map(Transaction::getBnb)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .abs()
                    .multiply(Constants.REFERRAL_FEE_PERCENT);
            List<Transaction> mintedNFTsMCOIN = transactionRepository.findByTransactionTypeAndUser(TransactionType.MINT_NFT_MCOIN, referrer);
            BigDecimal referralAmountMCOIN = mintedNFTsMCOIN.stream()
                    .map(Transaction::getMcoin)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .abs()
                    .multiply(Constants.REFERRAL_FEE_PERCENT);
            BigDecimal totalReferralAmount = referralAmountBNB.add(referralAmountMCOIN).setScale(5, RoundingMode.HALF_UP);
            referralHistoryDTOS.add(ReferralHistoryDTO.builder()
                    .walletAddress(referrer.getWalletAddress())
                    .referralAmount(totalReferralAmount)
                    .build());
        }
        return referralHistoryDTOS;
    }
}
