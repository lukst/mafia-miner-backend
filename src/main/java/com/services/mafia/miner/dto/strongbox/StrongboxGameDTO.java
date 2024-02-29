package com.services.mafia.miner.dto.strongbox;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StrongboxGameDTO {
    private Long id;
    private int currentMultiplier;
    private BigDecimal attemptCost;
    private int attemptsCount;
    private LocalDateTime lastResetTime;
    private List<CombinationDTO> combinations;
    private boolean won;
    private BigDecimal mcoinWon;
}
