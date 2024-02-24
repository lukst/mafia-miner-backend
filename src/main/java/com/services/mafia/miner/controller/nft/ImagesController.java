package com.services.mafia.miner.controller.nft;

import com.services.mafia.miner.services.nft.NFTCatalogService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.MalformedURLException;

@Controller
@AllArgsConstructor
@RequestMapping("/api/v1/images")
public class ImagesController {
    private final NFTCatalogService nftCatalogService;

    @GetMapping("/{imageName:.+}")
    public ResponseEntity<Resource> seeImage(@PathVariable String imageName){
        Resource resource = null;
        try {
            resource = nftCatalogService.load(imageName);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");
        int sixtyDaysInSeconds = 60 * 60 * 24 * 60;
        headers.add(HttpHeaders.CACHE_CONTROL, "public, max-age=" + sixtyDaysInSeconds);
        return new ResponseEntity<Resource>(resource, headers, HttpStatus.OK);
    }
}
