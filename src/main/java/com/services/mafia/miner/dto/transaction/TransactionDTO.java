package com.services.mafia.miner.dto.transaction;

import com.services.mafia.miner.dto.user.UserDTO;
import com.services.mafia.miner.entity.transaction.ObjectType;
import com.services.mafia.miner.entity.transaction.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Long id;
    private UserDTO user;
    private TransactionType transactionType;
    private ObjectType objectType;
    private Long objectId;
    private BigDecimal bnb;
    private BigDecimal mcoin;
    private String operation;
    private String txId;
    private LocalDateTime transactionDate;
    private boolean isPendingValidation;
    private boolean isBlockchainTransaction;
}
