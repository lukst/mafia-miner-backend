package com.services.mafia.miner.entity.jackpot;

import com.services.mafia.miner.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "jackpots")
public class Jackpot extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column
    @Builder.Default
    private JackpotStatus status = JackpotStatus.ONGOING;
    @Column(nullable = false, precision = 19, scale = 5)
    @Builder.Default
    private BigDecimal currentAmount = new BigDecimal("0.1").setScale(5, RoundingMode.HALF_UP);
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime lastUpdated = LocalDateTime.now();
    @Column(nullable = false, precision = 19, scale = 5)
    @Builder.Default
    private BigDecimal baseEntryPrice = new BigDecimal("0.001").setScale(5, RoundingMode.HALF_UP);
    @Version
    private int version;
}
