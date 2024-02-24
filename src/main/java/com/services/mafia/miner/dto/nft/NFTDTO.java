package com.services.mafia.miner.dto.nft;

import com.services.mafia.miner.entity.nft.FarmType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NFTDTO {
    private Long id;
    private NFTCatalogDTO nftCatalog;
    private FarmType farmType;
    private int availableMiningDays;
    private LocalDateTime lastRewardAt;
    private LocalDateTime nextRewardAt;
}
