package com.services.mafia.miner.services.nft;

import com.services.mafia.miner.dto.nft.NFTDTO;
import com.services.mafia.miner.dto.transaction.TransactionDTO;
import com.services.mafia.miner.entity.nft.FarmType;
import com.services.mafia.miner.entity.nft.NFT;
import com.services.mafia.miner.entity.nft.NFTCatalog;
import com.services.mafia.miner.entity.transaction.Transaction;
import com.services.mafia.miner.entity.transaction.TransactionType;
import com.services.mafia.miner.entity.user.User;
import com.services.mafia.miner.exception.InvalidInputException;
import com.services.mafia.miner.repository.nft.NFTCatalogRepository;
import com.services.mafia.miner.repository.nft.NFTRepository;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
@AllArgsConstructor
public class NFTServiceImpl implements NFTService {
    private final TransactionService transactionService;
    private final UserService userService;
    private final NFTRepository nftRepository;
    private final NFTCatalogRepository nftCatalogRepository;
    private final ModelMapper modelMapper;
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
            } else {
                userService.subtractUserMCOIN(userFound, mintPrice);
                transactionService.saveTransactionRecordMCOIN(
                        TransactionType.MINT_NFT_MCOIN,
                        userFound,
                        mintPrice.negate(),
                        Constants.MINT_NFT_OPERATION + " " + nftCatalog.getName());
            }
            User developerUser = userService.findUserByWallet(Constants.DEVELOPER_WALLET);
            developerUser.setMcoinBalance(developerUser.getMcoinBalance().add(developerFee));
            userService.save(developerUser);
            transactionService.saveTransactionRecordMCOIN(
                    TransactionType.MINT_NFT_BNB_FEE,
                    developerUser,
                    developerFee,
                    Constants.MINT_NFT_OPERATION + " " + nftCatalog.getName());
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
