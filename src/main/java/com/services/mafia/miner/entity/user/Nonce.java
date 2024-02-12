package com.services.mafia.miner.entity.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "nonce")
public class Nonce {
    @Id
    private String userAddress;
    private String nonce;
    private LocalDateTime createdAt;
}
