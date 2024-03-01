package com.services.mafia.miner.entity.jackpot;

import com.services.mafia.miner.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "winning_categories")
public class WinningCategory extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column
    private JackpotRewardType rewardType;
    @Column(nullable = false)
    private String winningChance;
    @Column(nullable = false)
    private int rangeStart;
    @Column(nullable = false)
    private int rangeEnd;
}
