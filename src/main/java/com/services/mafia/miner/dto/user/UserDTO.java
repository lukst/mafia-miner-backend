package com.services.mafia.miner.dto.user;

import com.services.mafia.miner.entity.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String walletAddress;
    private String referralCode;
    private Role role;
    private BigDecimal bnbBalance;
    private BigDecimal mcoinBalance;
    private BigDecimal totalDeposit;
    private BigDecimal totalWithdraw;
    private boolean isAccountNonLocked;
}
