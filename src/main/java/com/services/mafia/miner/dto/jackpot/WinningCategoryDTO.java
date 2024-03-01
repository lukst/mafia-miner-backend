package com.services.mafia.miner.dto.jackpot;

import com.services.mafia.miner.entity.jackpot.JackpotRewardType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WinningCategoryDTO {
    private JackpotRewardType rewardType;
    private String winningChance;
    private int rangeStart;
    private int rangeEnd;
}
