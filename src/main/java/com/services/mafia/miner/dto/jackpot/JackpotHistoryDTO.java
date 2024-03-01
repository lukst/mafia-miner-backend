package com.services.mafia.miner.dto.jackpot;

import com.services.mafia.miner.dto.nft.NFTDTO;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JackpotHistoryDTO {
    private String username;
    private WinningCategoryDTO winningCategory;
    private BigDecimal amountWon;
    private LocalDateTime timestamp;
    private int rollNumber;
    private NFTDTO nftSoldato;
}
