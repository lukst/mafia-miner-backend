package com.services.mafia.miner.dto.strongbox;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CombinationDTO {
    private Long id;
    private int number;
    private boolean isAttempted;
}
