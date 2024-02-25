package com.services.mafia.miner.controller.nft;

import com.services.mafia.miner.dto.nft.NFTDTO;
import com.services.mafia.miner.dto.transaction.TransactionDTO;
import com.services.mafia.miner.services.nft.NFTService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/play/{nftId}")
    public ResponseEntity<NFTDTO> play(
            HttpServletRequest request,
            @PathVariable Long nftId
    ) {
        return new ResponseEntity<>(nftService.play(request, nftId), HttpStatus.OK);
    }

    @GetMapping
    @ResponseBody
    public Page<NFTDTO> getPagedTransactionForUser(@RequestParam("page") Integer page,
                                                   @RequestParam("size") Integer size,
                                                   HttpServletRequest request) {
        return nftService.filterNftsForUser(request, page, size);
    }
}
