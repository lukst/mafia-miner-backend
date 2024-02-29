package com.services.mafia.miner.dto.strongbox;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StrongboxHistoryDTO {
    private String winningWallet;
    private StrongboxGameDTO game;
    private int winningCombination;
    private int winningMultiplier;
    private BigDecimal winAmount;
    private LocalDateTime winTime;
}
