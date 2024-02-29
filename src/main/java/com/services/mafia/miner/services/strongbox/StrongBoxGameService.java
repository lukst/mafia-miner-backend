package com.services.mafia.miner.services.strongbox;

import com.services.mafia.miner.dto.strongbox.StrongboxGameDTO;
import com.services.mafia.miner.dto.strongbox.StrongboxHistoryDTO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface StrongBoxGameService {
    StrongboxGameDTO findStrongboxCurrentGame();
    StrongboxGameDTO play(HttpServletRequest request, Long strongboxId, Long combinationId);
    List<StrongboxHistoryDTO> getLast5StrongboxHistory();
}
