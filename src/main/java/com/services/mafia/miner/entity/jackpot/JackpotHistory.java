package com.services.mafia.miner.entity.jackpot;

import com.services.mafia.miner.entity.BaseEntity;
import com.services.mafia.miner.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "jackpots_history")
public class JackpotHistory extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winning_category_id", nullable = false)
    private WinningCategory winningCategory;
    @Column(nullable = false, precision = 19, scale = 5)
    private BigDecimal amountWon;
    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
    @Column
    private Long soldatoNFT;
    @Column
    private int rollNumber;
}
