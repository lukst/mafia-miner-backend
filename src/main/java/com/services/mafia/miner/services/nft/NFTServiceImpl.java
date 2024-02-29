package com.services.mafia.miner.services.nft;

import com.services.mafia.miner.dto.nft.NFTDTO;
import com.services.mafia.miner.entity.nft.FarmType;
import com.services.mafia.miner.entity.nft.NFT;
import com.services.mafia.miner.entity.nft.NFTCatalog;
import com.services.mafia.miner.entity.transaction.TransactionType;
import com.services.mafia.miner.entity.user.User;
import com.services.mafia.miner.exception.InvalidInputException;
import com.services.mafia.miner.repository.nft.NFTCatalogRepository;
import com.services.mafia.miner.repository.nft.NFTRepository;
import com.services.mafia.miner.services.RateLimitingCacheService;
import com.services.mafia.miner.services.transaction.TransactionService;
import com.services.mafia.miner.services.user.UserService;
import com.services.mafia.miner.util.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@AllArgsConstructor
public class NFTServiceImpl implements NFTService {
    private final TransactionService transactionService;
    private final UserService userService;
    private final NFTRepository nftRepository;
    private final NFTCatalogRepository nftCatalogRepository;
    private final ModelMapper modelMapper;
    private final RateLimitingCacheService rateLimitingCacheService;
    private final Random random = new Random();

    @Override
    @Transactional
    public NFTDTO mintNFT(HttpServletRequest request, Long catalogID, boolean bnbMint) {
        User userFound = userService.findUserByToken(userService.extractTokenFromRequest(request));
        boolean isHotWallet = userFound.getWalletAddress().equalsIgnoreCase(Constants.HOT_WALLET);
        boolean isDeveloperWallet = userFound.getWalletAddress().equalsIgnoreCase(Constants.DEVELOPER_WALLET);
        User referralUser = userFound.getReferrer();
        NFTCatalog nftCatalog = nftCatalogRepository.findById(catalogID).orElseThrow(() -> new InvalidInputException("Catalog does not exist"));
        BigDecimal mintPrice;
        if (bnbMint) {
            mintPrice = nftCatalog.getBnbCost();
        } else {
            mintPrice = nftCatalog.getMcoinCost();
        }
        rateLimitingCacheService.checkRateLimit(userFound.getId(), 1);
        BigDecimal developerFee = nftCatalog.getBnbCost().multiply(Constants.NFT_FEE);
        handleReferrer(bnbMint, referralUser, mintPrice);
        if (!isHotWallet && !isDeveloperWallet) {
            if (bnbMint) {
                userService.subtractUserBNB(userFound, mintPrice);
                transactionService.saveTransactionRecordBNB(
                        TransactionType.MINT_NFT_BNB,
                        userFound,
                        mintPrice.negate(),
                        Constants.MINT_NFT_OPERATION + " " + nftCatalog.getName());
                User developerUser = userService.findUserByWallet(Constants.DEVELOPER_WALLET);
                developerUser.setMcoinBalance(developerUser.getMcoinBalance().add(developerFee));
                userService.save(developerUser);
                transactionService.saveTransactionRecordMCOIN(
                        TransactionType.MINT_NFT_BNB_FEE,
                        developerUser,
                        developerFee,
                        Constants.MINT_NFT_OPERATION + " " + nftCatalog.getName());
            } else {
                userService.subtractUserMCOIN(userFound, mintPrice);
                transactionService.saveTransactionRecordMCOIN(
                        TransactionType.MINT_NFT_MCOIN,
                        userFound,
                        mintPrice.negate(),
                        Constants.MINT_NFT_OPERATION + " " + nftCatalog.getName());
            }
        } else {
            userService.save(userFound);
        }
        int minDays = nftCatalog.getMinFarmDays();
        int maxDays = nftCatalog.getMaxFarmDays();
        int availableMiningDays = random.nextInt(maxDays - minDays + 1) + minDays;
        NFT nftMinted = nftRepository.save(NFT.builder()
                .user(userFound)
                .nftCatalog(nftCatalog)
                .farmType(FarmType.MCOIN)
                .availableMiningDays(availableMiningDays)
                .maxMiningDays(availableMiningDays)
                .build());
        return modelMapper.map(nftMinted, NFTDTO.class);
    }

    @Override
    public Page<NFTDTO> filterNftsForUser(HttpServletRequest request, int page, int size) {
        User user = userService.findUserByToken(userService.extractTokenFromRequest(request));
        Pageable pageable = PageRequest.of(page, size);
        Page<NFT> nfts = nftRepository.findAllByUser(user, pageable);
        return nfts.map(nft -> modelMapper.map(nft, NFTDTO.class));
    }

    @Override
    @Transactional
    public NFTDTO play(HttpServletRequest request, Long nftId) {
        User user = userService.findUserByToken(userService.extractTokenFromRequest(request));
        rateLimitingCacheService.checkRateLimit(user.getId(), 1);
        NFT nft = nftRepository.findById(nftId).orElseThrow(() -> new InvalidInputException("NFT does not exist"));
        LocalDateTime now = LocalDateTime.now();
        if (nft.getAvailableMiningDays() <= 0) {
            throw new InvalidInputException("No mining days left");
        }
        if (!nft.getUser().getId().equals(user.getId())) {
            throw new InvalidInputException("This NFT is not yours");
        }
        if (nft.getLastRewardAt() != null && Duration.between(nft.getLastRewardAt(), now).toHours() < 24) {
            throw new InvalidInputException("You can only mine once every 24 hours");
        }
        BigDecimal amountMined = nft.getNftCatalog().getDailyFarm();
        if (nft.getFarmType() == FarmType.BNB) {
            user.setBnbBalance(user.getBnbBalance().add(amountMined));
            transactionService.saveTransactionRecordBNB(
                    TransactionType.NFT_REWARD,
                    user,
                    amountMined,
                    Constants.NFT_REWARD + " " + amountMined + " for " + nft.getNftCatalog().getName());
        } else {
            user.setMcoinBalance(user.getMcoinBalance().add(amountMined));
            transactionService.saveTransactionRecordMCOIN(
                    TransactionType.NFT_REWARD,
                    user,
                    amountMined,
                    Constants.NFT_REWARD + " " + amountMined + " for " + nft.getNftCatalog().getName());
        }
        nft.setLastRewardAt(now);
        nft.setNextRewardAt(now.plusDays(1));
        nft.setAvailableMiningDays(nft.getAvailableMiningDays() - 1);
        nftRepository.save(nft);
        userService.save(user);
        return modelMapper.map(nft, NFTDTO.class);
    }

    private void handleReferrer(boolean bnbMint, User referralUser, BigDecimal mintPrice) {
        if (referralUser != null) {
            BigDecimal referralFee = mintPrice.multiply(Constants.REFERRAL_FEE_PERCENT);
            if (bnbMint) {
                transactionService.saveTransactionRecordBNB(
                        TransactionType.MINT_NFT_BNB,
                        referralUser,
                        referralFee,
                        Constants.MINT_NFT_OPERATION + " BNB referral");
            } else {
                transactionService.saveTransactionRecordBNB(
                        TransactionType.MINT_NFT_MCOIN,
                        referralUser,
                        referralFee,
                        Constants.MINT_NFT_OPERATION + " BNB referral");
            }
            referralUser.setBnbBalance(referralUser.getBnbBalance().add(referralFee));
            userService.save(referralUser);
        }
    }
}
