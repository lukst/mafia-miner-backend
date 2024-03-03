package com.services.mafia.miner.controller.nft;

import com.services.mafia.miner.dto.nft.NFTCatalogDTO;
import com.services.mafia.miner.services.nft.NFTCatalogService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/api/v1/catalog")
public class NFTCatalogController {
    private final NFTCatalogService nftCatalogService;

    @GetMapping
    public ResponseEntity<List<NFTCatalogDTO>> getAllCatalog() {
        return new ResponseEntity<>(nftCatalogService.findAll(), HttpStatus.OK);
    }
}
