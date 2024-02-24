package com.services.mafia.miner.services.nft;

import com.services.mafia.miner.dto.nft.NFTCatalogDTO;
import com.services.mafia.miner.entity.nft.NFTType;
import com.services.mafia.miner.repository.nft.NFTCatalogRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NFTCatalogServiceImpl implements NFTCatalogService {
    private final NFTCatalogRepository nftCatalogRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<NFTCatalogDTO> findAll() {
        return nftCatalogRepository.findAll()
                .stream()
                .filter(nftCatalog -> nftCatalog.getType() == NFTType.PAID)
                .map(nftCatalog -> modelMapper.map(nftCatalog, NFTCatalogDTO.class))
                .collect(Collectors.toList());
    }


    @Override
    public Resource load(String imageName) throws MalformedURLException {
        Path filePath = getPath(imageName);

        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() && !resource.isReadable()) {
            filePath = Paths.get("images").resolve("no.png").toAbsolutePath();
            resource = new UrlResource(filePath.toUri());
        }
        return resource;
    }

    @Override
    public Path getPath(String imageName) {
        String IMAGES_DIRECTORY = "images";
        return Paths.get(IMAGES_DIRECTORY).resolve(imageName).toAbsolutePath();
    }
}
