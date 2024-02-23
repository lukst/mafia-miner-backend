package com.services.mafia.miner.entity.transaction;


import com.services.mafia.miner.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "exception_details")
public class ExceptionDetail extends BaseEntity {
    private LocalDateTime timestamp;
    private String exceptionType;
    private String message;
    private String details;
    private Long userId;
    private Long transactionId;
    private boolean isFixed;
}