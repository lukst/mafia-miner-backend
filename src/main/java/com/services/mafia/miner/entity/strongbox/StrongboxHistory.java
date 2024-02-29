package com.services.mafia.miner.entity.strongbox;

import com.services.mafia.miner.entity.BaseEntity;
import com.services.mafia.miner.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "strongbox_history")
public class StrongboxHistory extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false) // Reference to StrongBoxGame
    private StrongBoxGame game;
    @Column(nullable = false)
    private int winningCombination;
    @Column(nullable = false)
    private int winningMultiplier;
    @Column(nullable = false, precision = 19, scale = 5)
    private BigDecimal winAmount;
    @Column(nullable = false)
    private LocalDateTime winTime = LocalDateTime.now();
}
