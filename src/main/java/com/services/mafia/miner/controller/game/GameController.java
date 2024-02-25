package com.services.mafia.miner.controller.game;

import com.services.mafia.miner.dto.game.TotalStatsDTO;
import com.services.mafia.miner.services.transaction.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/api/v1/game")
public class GameController {
    private final TransactionService transactionService;

    @GetMapping("/total-stats")
    public ResponseEntity<TotalStatsDTO> getTotalStats() {
        return new ResponseEntity<>(transactionService.getTotalStats(), HttpStatus.OK);
    }
}
