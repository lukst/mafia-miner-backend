package com.services.mafia.miner.controller.strongbox;

import com.services.mafia.miner.dto.nft.NFTDTO;
import com.services.mafia.miner.dto.strongbox.StrongboxGameDTO;
import com.services.mafia.miner.dto.user.UserDTO;
import com.services.mafia.miner.services.strongbox.StrongBoxGameService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
@RequestMapping("/api/v1/strongbox")
public class StrongboxController {

    private final StrongBoxGameService strongBoxGameService;

    @PostMapping("/play/{strongboxId}/{combinationId}")
    public ResponseEntity<StrongboxGameDTO> play(
            HttpServletRequest request,
            @PathVariable Long strongboxId,
            @PathVariable Long combinationId
    ) {
        return new ResponseEntity<>(strongBoxGameService.play(request, strongboxId, combinationId), HttpStatus.OK);
    }

    @GetMapping("/game")
    public ResponseEntity<StrongboxGameDTO> getCurrentGame() {
        return new ResponseEntity<>(strongBoxGameService.findStrongboxCurrentGame(), HttpStatus.OK);
    }
}
