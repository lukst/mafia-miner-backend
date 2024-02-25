package com.services.mafia.miner.services.transaction;

import com.services.mafia.miner.dto.game.TotalStatsDTO;
import com.services.mafia.miner.dto.transaction.TransactionDTO;
import com.services.mafia.miner.entity.transaction.TransactionType;
import com.services.mafia.miner.entity.user.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public interface TransactionService {
    Page<TransactionDTO> filterTransactionsForUser(HttpServletRequest request, int page, int size);
    void saveTransactionRecordBNB(TransactionType transactionType, User userFound, BigDecimal bnb, String operation);
    void saveTransactionRecordMCOIN(TransactionType transactionType, User userFound, BigDecimal mcoin, String operation);
    TotalStatsDTO getTotalStats();
    Page<TransactionDTO> getAllTransactionsForUser(String wallet, int page, int size);
}
