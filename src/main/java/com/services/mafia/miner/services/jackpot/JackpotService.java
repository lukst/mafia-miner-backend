package com.services.mafia.miner.services.jackpot;

import com.services.mafia.miner.dto.jackpot.JackpotDTO;
import com.services.mafia.miner.dto.jackpot.JackpotHistoryDTO;
import com.services.mafia.miner.dto.jackpot.JackpotResultDTO;
import com.services.mafia.miner.dto.jackpot.WinningCategoryDTO;
import com.services.mafia.miner.entity.jackpot.Jackpot;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Optional;

public interface JackpotService {
    JackpotResultDTO playJackpot(HttpServletRequest request);
    List<JackpotHistoryDTO> getLast30JackpotHistories();
    List<WinningCategoryDTO> getAllWinningCategories();
    Jackpot findFirstWithLock();
    Jackpot findByIdWithLock(Long id);
    JackpotDTO findJackpotByIdOrLastOngoing(Optional<Long> jackpotId);
}
