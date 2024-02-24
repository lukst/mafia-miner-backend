package com.services.mafia.miner.services.nft;

import com.services.mafia.miner.dto.nft.NFTCatalogDTO;
import org.springframework.core.io.Resource;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;

public interface NFTCatalogService {
    List<NFTCatalogDTO> findAll();
    Resource load(String imageName) throws MalformedURLException;
    Path getPath(String imageName);
}
