package com.services.mafia.miner.entity.strongbox;

import com.services.mafia.miner.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "strongbox_combinations")
public class Combination extends BaseEntity {
    @Column(nullable = false)
    private int number;
    @Column(nullable = false)
    @Builder.Default
    private boolean isAttempted = false;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private StrongBoxGame game;
}
