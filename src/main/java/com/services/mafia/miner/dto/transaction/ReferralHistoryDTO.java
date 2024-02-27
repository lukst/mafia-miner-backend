package com.services.mafia.miner.dto.transaction;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class ReferralHistoryDTO {
    private String walletAddress;
    private BigDecimal referralAmount;
}
