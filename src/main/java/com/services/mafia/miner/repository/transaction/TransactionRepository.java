package com.services.mafia.miner.repository.transaction;

import com.services.mafia.miner.entity.user.User;
import com.services.mafia.miner.entity.transaction.Transaction;
import com.services.mafia.miner.entity.transaction.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findAllByUser(User user, Pageable pageable);
    Optional<Transaction> findByTxId(String txId);
    List<Transaction> findByIsPendingValidationAndTransactionType(boolean pendingValidation, TransactionType transactionType);
    @Query("SELECT t FROM Transaction t WHERE t.isPendingValidation = true AND t.transactionType <> :excludedTransactionType")
    List<Transaction> findByIsPendingValidationAndNotType(@Param("excludedTransactionType") TransactionType excludedTransactionType);
    Optional<Transaction> findByIsPendingValidationAndTransactionTypeAndUser(boolean pendingValidation, TransactionType transactionType, User user);
    List<Transaction> findByTransactionTypeAndUser(TransactionType transactionType, User user);
}
