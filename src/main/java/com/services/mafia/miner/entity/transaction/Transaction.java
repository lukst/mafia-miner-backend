package com.services.mafia.miner.entity.transaction;


import com.services.mafia.miner.entity.BaseEntity;
import com.services.mafia.miner.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction extends BaseEntity implements Serializable {
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    @Enumerated(EnumType.STRING)
    private ObjectType objectType;
    @Column
    private Long objectId;
    @Column(nullable = false, precision = 19, scale = 5)
    @Builder.Default
    private BigDecimal bnb = BigDecimal.ZERO.setScale(5, RoundingMode.HALF_UP);
    @Column(nullable = false, precision = 19, scale = 5)
    @Builder.Default
    private BigDecimal mcoin = BigDecimal.ZERO.setScale(5, RoundingMode.HALF_UP);
    @Column
    private String operation;
    @Column(name = "tx_id", unique = true)
    private String txId;
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;
    @Column
    private boolean isPendingValidation;
    @Column
    private boolean isBlockchainTransaction;
}