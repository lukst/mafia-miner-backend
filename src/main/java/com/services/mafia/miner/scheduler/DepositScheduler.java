package com.services.mafia.miner.scheduler;

import com.services.mafia.miner.entity.transaction.Transaction;
import com.services.mafia.miner.entity.transaction.TransactionType;
import com.services.mafia.miner.repository.transaction.TransactionRepository;
import com.services.mafia.miner.services.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class DepositScheduler {
    private final UserService userService;
    private final TransactionRepository transactionRepository;

    @Scheduled(fixedDelay = 30000)  // 30000 ms = 30 seconds
    public void validatePendingTransactions() {
        List<Transaction> pendingTransactions = transactionRepository.findByIsPendingValidationAndNotType(TransactionType.WITHDRAW);
        for (Transaction pendingTransaction : pendingTransactions) {
            userService.addUserBalance(pendingTransaction);
        }
    }
}