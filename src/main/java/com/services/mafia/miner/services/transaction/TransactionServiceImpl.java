package com.services.mafia.miner.services.transaction;

import com.services.mafia.miner.dto.transaction.TransactionDTO;
import com.services.mafia.miner.entity.transaction.Transaction;
import com.services.mafia.miner.entity.transaction.TransactionType;
import com.services.mafia.miner.entity.user.User;
import com.services.mafia.miner.repository.transaction.TransactionRepository;
import com.services.mafia.miner.repository.user.UserRepository;
import com.services.mafia.miner.services.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    public Page<TransactionDTO> filterTransactionsForUser(HttpServletRequest request, int page, int size) {
        User user = userService.findUserByToken(userService.extractTokenFromRequest(request));
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionRepository.findAllByUser(user, pageable);
        return transactions.map(transaction -> modelMapper.map(transaction, TransactionDTO.class));
    }

    @Override
    public void saveTransactionRecord(TransactionType transactionType, User userFound, BigDecimal amount) {
        Transaction transactionRecord = new Transaction();
        transactionRecord.setTransactionType(transactionType);
        transactionRecord.setTransactionDate(LocalDateTime.now());
        transactionRecord.setUser(userFound);
        transactionRecord.setBlockchainTransaction(false);
        transactionRecord.setPendingValidation(false);
        transactionRecord.setBnb(BigDecimal.ZERO.setScale(5, RoundingMode.HALF_UP));
        transactionRecord.setMcoin(amount.setScale(5, RoundingMode.HALF_UP));
        transactionRepository.save(transactionRecord);
    }
}
