package com.services.mafia.miner.controller.nft;

import com.services.mafia.miner.dto.nft.NFTDTO;
import com.services.mafia.miner.services.nft.NFTService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/api/v1/nft")
public class NFTController {
    private final NFTService nftService;

    @PostMapping("/mint/{catalogId}/{bnbMint}")
    public ResponseEntity<NFTDTO> mintNFT(
            HttpServletRequest request,
            @PathVariable Long catalogId,
            @PathVariable boolean bnbMint
    ) {
        return new ResponseEntity<>(nftService.mintNFT(request, catalogId, bnbMint), HttpStatus.OK);
    }
}
