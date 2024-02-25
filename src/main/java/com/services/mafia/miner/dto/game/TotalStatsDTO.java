package com.services.mafia.miner.dto.game;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TotalStatsDTO {
    private BigDecimal totalDeposit;
    private int totalUsers;
    private int totalNfts;
}