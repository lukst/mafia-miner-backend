package com.services.mafia.miner.entity.user;

import com.services.mafia.miner.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "login_attempts")
public class LoginAttempt extends BaseEntity {
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String ip;
    @Column(nullable = false)
    private LocalDateTime lastAttempt;
    @Column(nullable = false)
    private int attemptCount;
}
