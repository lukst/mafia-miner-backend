package com.services.mafia.miner.entity.nft;

import com.services.mafia.miner.entity.BaseEntity;
import com.services.mafia.miner.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "nfts")
public class NFT extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "nft_catalog_id", nullable = false)
    private NFTCatalog nftCatalog;
    @Enumerated(EnumType.STRING)
    @Column
    private FarmType farmType;
    @Column
    private int availableMiningDays;
    @Column
    private LocalDateTime lastRewardAt;
    @Column
    private LocalDateTime nextRewardAt;
}
