package com.services.mafia.miner.services.strongbox;

import com.services.mafia.miner.dto.strongbox.StrongboxGameDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface StrongBoxGameService {
    StrongboxGameDTO findStrongboxCurrentGame();
    StrongboxGameDTO play(HttpServletRequest request, Long strongboxId, Long combinationId);
}
