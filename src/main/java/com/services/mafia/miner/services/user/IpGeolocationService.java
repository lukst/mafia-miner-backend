package com.services.mafia.miner.services.user;

import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
public class IpGeolocationService {
    private final RestTemplate restTemplate = new RestTemplate();

    public CompletableFuture<String> getCountryByIp(String ip) {
        return CompletableFuture.supplyAsync(() -> {
            String apiUrl = "https://ipinfo.io/" + ip + "/json";
            GeolocationResponse response = restTemplate.getForObject(apiUrl, GeolocationResponse.class);
            return response != null ? response.getCountry() : "Unknown";
        });
    }


    @Getter
    public static class GeolocationResponse {
        private String country;

        // getters and setters
    }
}

