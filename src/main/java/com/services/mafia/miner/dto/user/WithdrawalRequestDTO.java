package com.services.mafia.miner.dto.user;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawalRequestDTO {
    private String walletAddress;
    private BigDecimal amount;
}

