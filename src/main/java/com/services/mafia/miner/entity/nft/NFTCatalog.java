package com.services.mafia.miner.entity.nft;

import com.services.mafia.miner.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "nfts_catalog")
public class NFTCatalog extends BaseEntity {
    @Column
    private String name;
    @Column
    private String image;
    @Enumerated(EnumType.STRING)
    @Column
    private NFTType type;
    @OneToMany(mappedBy="nftCatalog", fetch = FetchType.LAZY)
    private List<NFT> nfts;
    @Column(nullable = false, precision = 19, scale = 5)
    private BigDecimal bnbCost;
    @Column(nullable = false, precision = 19, scale = 5)
    private BigDecimal mcoinCost;
    @Column
    @Builder.Default
    private int minFarmDays = 25;
    @Column
    @Builder.Default
    private int maxFarmDays = 30;
    @Column(nullable = false, precision = 19, scale = 5)
    private BigDecimal dailyFarm;
}
