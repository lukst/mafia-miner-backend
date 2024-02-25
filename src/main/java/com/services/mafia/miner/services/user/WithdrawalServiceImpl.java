package com.services.mafia.miner.services.user;

import com.services.mafia.miner.dto.transaction.TransactionDTO;
import com.services.mafia.miner.dto.user.UserDTO;
import com.services.mafia.miner.dto.user.WithdrawalRequestDTO;
import com.services.mafia.miner.dto.user.WithdrawalValidationDTO;
import com.services.mafia.miner.entity.transaction.Transaction;
import com.services.mafia.miner.entity.transaction.TransactionType;
import com.services.mafia.miner.entity.user.User;
import com.services.mafia.miner.exception.InvalidInputException;
import com.services.mafia.miner.exception.UserWithNoBalanceException;
import com.services.mafia.miner.repository.transaction.TransactionRepository;
import com.services.mafia.miner.repository.user.UserRepository;
import com.services.mafia.miner.services.transaction.TransactionService;
import com.services.mafia.miner.util.Constants;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Log4j2
public class WithdrawalServiceImpl implements WithdrawalService {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;
    private final ModelMapper modelMapper;
    private final UserService userService;

    @Override
    @Transactional
    public UserDTO requestWithdrawal(HttpServletRequest request, WithdrawalRequestDTO withdrawalRequestDTO) {
        String token = userService.extractTokenFromRequest(request);
        User userFound = userService.findUserByToken(token);
        BigDecimal minimumBalanceRequired = Constants.MINIMUM_WITHDRAW;
        if (userFound.getMcoinBalance().compareTo(withdrawalRequestDTO.getAmount()) < 0
                || userFound.getMcoinBalance().compareTo(minimumBalanceRequired) < 0) {
            throw new UserWithNoBalanceException("Insufficient balance for withdrawal for user: " +
                    userFound.getWalletAddress());
        }
        if (!userFound.getWalletAddress().equalsIgnoreCase(withdrawalRequestDTO.getWalletAddress())) {
            throw new InvalidInputException("User requesting withdraw: " +
                    withdrawalRequestDTO.getWalletAddress() +
                    " user who requested withdraw: " +
                    userFound.getWalletAddress());
        }
        if (transactionRepository
                .findByIsPendingValidationAndTransactionTypeAndUser(
                        true,
                        TransactionType.WITHDRAW,
                        userFound).isPresent()) {
            throw new InvalidInputException("User already has a pending withdraw");
        }
        userService.subtractUserMCOIN(userFound, withdrawalRequestDTO.getAmount());
        // Create a new transaction with PENDING status for full withdraw amount
        Transaction transaction = Transaction.builder()
                .user(userFound)
                .transactionType(TransactionType.WITHDRAW)
                .mcoin(withdrawalRequestDTO.getAmount().negate())
                .operation("User requesting withdraw")
                .isPendingValidation(true)
                .isBlockchainTransaction(true)
                .transactionDate(LocalDateTime.now()).build();

        transactionRepository.save(transaction);
        return modelMapper.map(userRepository.save(userFound), UserDTO.class);
    }

    @Override
    public void validateWithdrawal(HttpServletRequest request, WithdrawalValidationDTO withdrawalValidationDTO) {
        User userFound = userService.findUserByToken(userService.extractTokenFromRequest(request));
        Transaction transaction = validateUser(withdrawalValidationDTO, userFound);
        BigDecimal developerFee = transaction.getMcoin().abs().multiply(Constants.WITHDRAW_FEE);
        transaction.setPendingValidation(false);
        transaction.setTxId(withdrawalValidationDTO.getTxId());
        User userThatIsWithdrawing = transaction.getUser();
        if (userThatIsWithdrawing.getWalletAddress().equalsIgnoreCase(Constants.DEVELOPER_WALLET)) {
            userThatIsWithdrawing.setTotalWithdraw(userThatIsWithdrawing.getTotalWithdraw().add(transaction.getMcoin().abs()));
        } else {
            userThatIsWithdrawing.setTotalWithdraw(
                    userThatIsWithdrawing.getTotalWithdraw().add(
                            transaction.getMcoin().abs()
                                    .subtract(developerFee.abs()))
            );
            User developer = userRepository.findByWalletAddress(Constants.DEVELOPER_WALLET).orElse(null);
            if (developer != null) {
                if (!userFound.equals(developer)) {
                    developer.setMcoinBalance(developer.getMcoinBalance().add(developerFee));
                    userRepository.save(developer);
                    transactionService.saveTransactionRecordMCOIN(TransactionType.DEVELOPER_FEE_WITHDRAW, developer, developerFee, "Withdraw fee " + developerFee);
                }
            }
        }
        transactionRepository.save(transaction);
        userRepository.save(userThatIsWithdrawing);
    }

    private Transaction validateUser(WithdrawalValidationDTO withdrawalValidationDTO, User userFound) {
        if (!userFound.getWalletAddress().equalsIgnoreCase(Constants.HOT_WALLET)) {
            throw new InvalidInputException("The user " +
                    userFound.getWalletAddress() +
                    " is not valid for this operation");
        }
        Transaction transaction = transactionRepository.findById(withdrawalValidationDTO.getTransactionId())
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));
        if (transaction.getTransactionType() != TransactionType.WITHDRAW || !transaction.isPendingValidation()) {
            throw new IllegalStateException("Transaction cannot be validated." +
                    "Transaction type: " +
                    transaction.getTransactionType() +
                    "Transaction isPendingValidation: " +
                    transaction.isPendingValidation());
        }
        return transaction;
    }

    @Override
    public void rejectWithdrawal(HttpServletRequest request, WithdrawalValidationDTO withdrawalValidationDTO) {
        String token = userService.extractTokenFromRequest(request);
        User userFound = userService.findUserByToken(token);
        Transaction transaction = validateUser(withdrawalValidationDTO, userFound);
        transaction.setPendingValidation(false);
        transaction.setTxId(withdrawalValidationDTO.getTxId());
        transactionRepository.save(transaction);
    }

    @Override
    public List<TransactionDTO> findPendingWithdrawTransactions(HttpServletRequest request) {
        String token = userService.extractTokenFromRequest(request);
        User userFound = userService.findUserByToken(token);
        if (!userFound.getWalletAddress().equalsIgnoreCase(Constants.HOT_WALLET)) {
            throw new InvalidInputException("The user " +
                    userFound.getWalletAddress() +
                    " is not valid for this operation");
        }
        List<Transaction> pendingWithdrawTransactions = transactionRepository.findByIsPendingValidationAndTransactionType(true, TransactionType.WITHDRAW);
        return pendingWithdrawTransactions.stream()
                .map(transaction -> modelMapper.map(transaction, TransactionDTO.class))
                .collect(Collectors.toList());
    }
}

