package com.services.mafia.miner.entity.strongbox;

import com.services.mafia.miner.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "strongbox_game")
public class StrongBoxGame extends BaseEntity {
    @Column(nullable = false)
    @Builder.Default
    private int currentMultiplier = 90;
    @Column(nullable = false, precision = 19, scale = 3)
    @Builder.Default
    private BigDecimal attemptCost = new BigDecimal("0.002").setScale(3, RoundingMode.HALF_UP);
    @Column(nullable = false)
    @Builder.Default
    private int attemptsCount = 0;
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime lastResetTime = LocalDateTime.now();
    @Column(nullable = false)
    private int winningCombination;
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Combination> combinations = new ArrayList<>();
    @Version
    private int version;

    public void initializeCombinations() {
        this.combinations.clear();
        for (int i = 0; i < 100; i++) {
            Combination combination = Combination.builder()
                    .number(i)
                    .isAttempted(false)
                    .game(this)
                    .build();
            this.combinations.add(combination);
        }
    }

    public void resetGame() {
        this.attemptsCount = 0;
        this.currentMultiplier = 95;
        this.lastResetTime = LocalDateTime.now();
        this.winningCombination = generateWinningCombination();
        for (Combination combination : this.combinations) {
            combination.setAttempted(false);
        }
    }

    private int generateWinningCombination() {
        return new Random().nextInt(100);
    }
}
