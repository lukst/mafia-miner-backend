package com.services.mafia.miner.controller.jackpot;

import com.services.mafia.miner.dto.jackpot.JackpotDTO;
import com.services.mafia.miner.dto.jackpot.JackpotHistoryDTO;
import com.services.mafia.miner.dto.jackpot.JackpotResultDTO;
import com.services.mafia.miner.dto.jackpot.WinningCategoryDTO;
import com.services.mafia.miner.services.jackpot.JackpotService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
@RequestMapping("/api/v1/jackpot")
public class JackpotController {
    private final JackpotService jackpotService;

    @PostMapping
    @Transactional
    public ResponseEntity<JackpotResultDTO> play(HttpServletRequest request) {
        return ResponseEntity.ok(jackpotService.playJackpot(request));
    }

    @GetMapping
    public ResponseEntity<JackpotDTO> getJackpot(@RequestParam Optional<Long> id) {
        return ResponseEntity.ok(jackpotService.findJackpotByIdOrLastOngoing(id));
    }

    @GetMapping("/history")
    public ResponseEntity<List<JackpotHistoryDTO>> getLast30RouletteHistories() {
        return ResponseEntity.ok(jackpotService.getLast30JackpotHistories());
    }

    @GetMapping("/winning-categories")
    public ResponseEntity<List<WinningCategoryDTO>> getAllWinningCategories() {
        return ResponseEntity.ok(jackpotService.getAllWinningCategories());
    }
}
