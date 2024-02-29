package com.services.mafia.miner.services.user;

import com.google.common.util.concurrent.RateLimiter;
import com.services.mafia.miner.dto.user.AddBNB;
import com.services.mafia.miner.dto.user.AddBalanceRequest;
import com.services.mafia.miner.dto.user.UserDTO;
import com.services.mafia.miner.entity.transaction.ExceptionDetail;
import com.services.mafia.miner.entity.transaction.ObjectType;
import com.services.mafia.miner.entity.user.Token;
import com.services.mafia.miner.entity.user.User;
import com.services.mafia.miner.exception.*;
import com.services.mafia.miner.repository.transaction.ExceptionDetailRepository;
import com.services.mafia.miner.repository.transaction.TransactionRepository;
import com.services.mafia.miner.repository.user.TokenRepository;
import com.services.mafia.miner.repository.user.UserRepository;
import com.services.mafia.miner.entity.transaction.Transaction;
import com.services.mafia.miner.entity.transaction.TransactionType;
import com.services.mafia.miner.util.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final TransactionRepository transactionRepository;
    private final RateLimiter rateLimiter;
    private final ExceptionDetailRepository exceptionDetailRepository;

    @Override
    public UserDTO getUser(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        User userFound = findUserByToken(token);
        return modelMapper.map(userFound, UserDTO.class);
    }

    @Override
    @Transactional
    public String extractTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidTokenException("Token is not valid");
        }
        return authHeader.substring(7);
    }

    @Override
    @Transactional
    public User findUserByToken(String token) {
        Token tokenFound = tokenRepository.findByTokenAndExpiredIsFalseAndRevokedIsFalse(token)
                .orElseThrow(() -> new InvalidTokenException("Token is not valid or expired"));
        return userRepository.findById(tokenFound.getUser().getId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    @Transactional
    public User findUserByWallet(String wallet) {
        return userRepository.findByWalletAddress(wallet).orElseThrow(() -> new InvalidInputException("User with wallet " + wallet + " not found"));
    }

    @Override
    public UserDTO addPendingTransaction(HttpServletRequest request, AddBalanceRequest addBalanceRequest) {
        User userFound = findUserByToken(extractTokenFromRequest(request));
        if (addBalanceRequest.getBnbBalance().compareTo(Constants.MINIMUM_DEPOSIT) < 0) {
            throw new IllegalArgumentException("The balance is less than the minimum required deposit.");
        }
        if (transactionRepository.findByTxId(addBalanceRequest.getTxId()).isPresent()) {
            throw new BlockchainTransactionException("The txId is already registered");
        }
        Transaction transaction = Transaction.builder()
                .user(userFound)
                .isBlockchainTransaction(true)
                .isPendingValidation(true)
                .transactionDate(LocalDateTime.now())
                .transactionType(TransactionType.DEPOSIT)
                .bnb(addBalanceRequest.getBnbBalance())
                .objectType(ObjectType.USER)
                .objectId(userFound.getId())
                .operation(Constants.DEPOSIT_OPERATION)
                .txId(addBalanceRequest.getTxId())
                .build();
        transactionRepository.save(transaction);
        return modelMapper.map(userFound, UserDTO.class);
    }

    @Override
    public void addUserBalance(Transaction transaction) {
        try {
            if (!validateTransaction(transaction.getTxId(), transaction.getUser(), transaction.getBnb())) {
                throw new BlockchainTransactionException("The transaction was not successful.");
            }
            BigDecimal newUserBalance = transaction.getUser().getBnbBalance().add(transaction.getBnb());
            transaction.getUser().setBnbBalance(newUserBalance);
            transaction.getUser().setTotalDeposit(transaction.getUser().getTotalDeposit().add(transaction.getBnb()));
            transaction.setTransactionDate(LocalDateTime.now());
            userRepository.save(transaction.getUser());
            transaction.setOperation(Constants.DEPOSIT_OPERATION_SUCCESS);
        } catch (Exception ex) {
            // Create an instance of the exception entity
            ExceptionDetail exceptionDetails = ExceptionDetail.builder()
                    .timestamp(LocalDateTime.now())
                    .exceptionType(ex.getClass().getName())
                    .message(ex.getMessage())
                    .details(ex.getLocalizedMessage())
                    .userId(transaction.getUser().getId())
                    .transactionId(transaction.getId())
                    .build();
            transaction.setOperation(Constants.DEPOSIT_OPERATION_FAILED + " " + ex.getMessage());
            // Save the exception details to the database
            exceptionDetailRepository.save(exceptionDetails);
        } finally {
            transaction.setPendingValidation(false);
            transactionRepository.save(transaction);
        }
    }

    @Override
    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void subtractUserBNB(User userFound, BigDecimal bnbToSubtract) {
        if (userFound.getBnbBalance().compareTo(bnbToSubtract) < 0) {
            throw new UserWithNoBalanceException("Not enough BNB");
        }
        userFound.setBnbBalance(userFound.getBnbBalance().subtract(bnbToSubtract));
        userRepository.save(userFound);
    }

    @Override
    @Transactional
    public void subtractUserMCOIN(User userFound, BigDecimal mcoinToSubtract) {
        if (userFound.getMcoinBalance().compareTo(mcoinToSubtract) < 0) {
            throw new UserWithNoBalanceException("Not enough MCOIN");
        }
        userFound.setMcoinBalance(userFound.getMcoinBalance().subtract(mcoinToSubtract));
        userRepository.save(userFound);
    }

    @Override
    public List<User> findAllByReferrer(User referrer) {
        return userRepository.findAllByReferrer(referrer);
    }

    @Override
    @Transactional
    public UserDTO addGiftBNB(HttpServletRequest request, AddBNB addBNB) {
        User userFound = findUserByToken(extractTokenFromRequest(request));
        if (!userFound.getWalletAddress().equalsIgnoreCase(Constants.HOT_WALLET)) {
            throw new InvalidInputException("The user " +
                    userFound.getWalletAddress() +
                    " is not valid for this operation");
        }
        if (addBNB.getBnbBalance().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInputException("The amount must be greater than 0");
        }
        User userToGift = findUserByWallet(addBNB.getWalletAddress());
        userToGift.setBnbBalance(userToGift.getBnbBalance().add(addBNB.getBnbBalance()));
        Transaction transactionRecord = Transaction.builder()
                .user(userToGift)
                .transactionType(TransactionType.GIFT_BNB)
                .objectType(ObjectType.USER)
                .objectId(userToGift.getId())
                .bnb(addBNB.getBnbBalance())
                .mcoin(BigDecimal.ZERO)
                .operation("BNB gift")
                .transactionDate(LocalDateTime.now())
                .isPendingValidation(false)
                .isBlockchainTransaction(false)
                .build();
        transactionRepository.save(transactionRecord);
        save(userToGift);
        return modelMapper.map(userToGift, UserDTO.class);
    }

    private boolean validateTransaction(String txId, User user, BigDecimal amount) throws IOException, InterruptedException {
        final int MAX_RETRIES = 3;
        final long WAIT_TIME_MS = 5000; // 5 seconds
        Web3j web3j = Web3j.build(new HttpService("https://bsc-dataseed.binance.org/"));

        Exception lastException = null;

        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
                rateLimiter.acquire();

                EthGetTransactionReceipt transactionReceipt = web3j.ethGetTransactionReceipt(txId).send();

                if (transactionReceipt.getTransactionReceipt().isPresent()) {
                    TransactionReceipt receipt = transactionReceipt.getTransactionReceipt().get();

                    // Ensure the transaction was successful
                    if (!receipt.isStatusOK()) {
                        throw new BlockchainTransactionException("The transaction was not successful. " + txId);
                    }

                    // Check that the transaction is from the user's wallet
                    if (!receipt.getFrom().equalsIgnoreCase(user.getWalletAddress())) {
                        throw new BlockchainTransactionException("The transaction was not made from the correct wallet " + txId);
                    }

                    EthTransaction ethTransaction = getEthTransaction(web3j, txId);

                    if (ethTransaction.getTransaction().isPresent()) {
                        org.web3j.protocol.core.methods.response.Transaction transaction = ethTransaction.getTransaction().get();

                        // For BNB, check the 'to' field and 'value'
                        if (!transaction.getTo().equalsIgnoreCase(Constants.GAME_WALLET)) {
                            throw new BlockchainTransactionException("The transaction was not made to the game's wallet " + transaction.getTo());
                        }

                        // Convert wei to BNB (or the unit you are comparing with)
                        BigDecimal transferredAmount = Convert.fromWei(new BigDecimal(transaction.getValue()), Convert.Unit.ETHER);

                        if (transferredAmount.compareTo(amount) != 0) {
                            throw new BlockchainTransactionException("The transferred amount does not match the expected amount " + txId);
                        } else {
                            return true;
                        }
                    } else {
                        throw new BlockchainTransactionException("Could not find a transaction with the given ID " + txId);
                    }
                } else {
                    throw new BlockchainTransactionException("Could not find a transaction with the given ID " + txId);
                }
            } catch (BlockchainTransactionException e) {
                lastException = e;
                Thread.sleep(WAIT_TIME_MS);
            }
        }

        // If reached here, all retries have failed.
        throw new BlockchainTransactionException("After " + MAX_RETRIES + " retries, validation failed: " + lastException.getMessage(), lastException);
    }

    @NotNull
    private static TransactionReceipt getTransactionReceipt(String txId, EthGetTransactionReceipt transactionReceipt) {
        if (transactionReceipt.getTransactionReceipt().isEmpty()) {
            throw new BlockchainTransactionException("No transaction receipt found");
        }
        TransactionReceipt receipt = transactionReceipt.getTransactionReceipt().get();

        if (!receipt.isStatusOK()) {
            throw new BlockchainTransactionException("The transaction was not successful. " + txId);
        }

        if (!receipt.getTo().equalsIgnoreCase(Constants.BNB_MAINNET)) {
            throw new BlockchainTransactionException("The transaction was not made to the correct contract " + txId);
        }
        return receipt;
    }

    private EthTransaction getEthTransaction(Web3j web3j, String txId) throws IOException, InterruptedException {
        EthTransaction ethTransaction = null;
        int MAX_RETRIES = 5;
        for (int i = 0; i < MAX_RETRIES; i++) {
            ethTransaction = web3j.ethGetTransactionByHash(txId).send();
            if (ethTransaction.getTransaction().isPresent()) {
                break;
            }
            int WAITING_TIME = 8000;
            Thread.sleep(WAITING_TIME);
        }
        if (ethTransaction.getTransaction().isEmpty()) {
            throw new BlockchainTransactionException("Could not find a transaction with the given ID.");
        }
        return ethTransaction;
    }
}
