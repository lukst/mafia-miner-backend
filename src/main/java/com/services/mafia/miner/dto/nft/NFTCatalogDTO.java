package com.services.mafia.miner.dto.nft;

import com.services.mafia.miner.entity.nft.NFTFamily;
import com.services.mafia.miner.entity.nft.NFTType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NFTCatalogDTO {
    private Long id;
    private String name;
    private String image;
    private String roi;
    private NFTType type;
    private NFTFamily family;
    private BigDecimal bnbCost;
    private BigDecimal mcoinCost;
    private int minFarmDays;
    private int maxFarmDays;
    private BigDecimal dailyFarm;
}
