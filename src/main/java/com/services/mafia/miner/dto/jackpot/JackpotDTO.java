package com.services.mafia.miner.dto.jackpot;

import com.services.mafia.miner.entity.jackpot.JackpotStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JackpotDTO {
    private Long id;
    private JackpotStatus status;
    private BigDecimal currentAmount;
    private LocalDateTime lastUpdated;
    private BigDecimal baseEntryPrice;
}