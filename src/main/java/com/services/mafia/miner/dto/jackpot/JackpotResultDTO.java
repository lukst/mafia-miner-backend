package com.services.mafia.miner.dto.jackpot;

import com.services.mafia.miner.dto.nft.NFTDTO;
import com.services.mafia.miner.entity.jackpot.JackpotRewardType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JackpotResultDTO {
    private JackpotRewardType result;
    private BigDecimal winAmount;
    private int rollNumber;
    private BigDecimal mcoin;
    private NFTDTO nftSoldato;
}