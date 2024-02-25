package com.services.mafia.miner.dto.user;

import lombok.Data;

@Data
public class WithdrawalValidationDTO {
    private Long transactionId;
    private String txId;
}