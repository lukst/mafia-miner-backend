package com.services.mafia.miner.initialization;

import com.services.mafia.miner.entity.nft.NFTCatalog;
import com.services.mafia.miner.entity.nft.NFTType;
import com.services.mafia.miner.repository.nft.NFTCatalogRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

@Component
@AllArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {
    private final NFTCatalogRepository nftCatalogRepository;


    @Override
    public void run(String... args) {
        createNFTCatalog();
    }

    private void createNFTCatalog() {
        if (nftCatalogRepository.findAll().isEmpty()) {
            nftCatalogRepository.save(NFTCatalog.builder()
                    .name("Pet")
                    .image("free.png")
                    .type(NFTType.FREE)
                    .nfts(new ArrayList<>())
                    .bnbCost(BigDecimal.ZERO)
                    .mcoinCost(BigDecimal.ZERO)
                    .minFarmDays(60)
                    .maxFarmDays(60)
                    .dailyFarm(new BigDecimal("0.0012").setScale(5, RoundingMode.HALF_UP))
                    .build());
            nftCatalogRepository.save(NFTCatalog.builder()
                    .name("Soldato")
                    .image("soldato.png")
                    .type(NFTType.PAID)
                    .nfts(new ArrayList<>())
                    .bnbCost(new BigDecimal("0.05").setScale(5, RoundingMode.HALF_UP))
                    .mcoinCost(new BigDecimal("0.0475").setScale(5, RoundingMode.HALF_UP))
                    .minFarmDays(25)
                    .maxFarmDays(30)
                    .dailyFarm(new BigDecimal("0.0024").setScale(5, RoundingMode.HALF_UP))
                    .build());
            nftCatalogRepository.save(NFTCatalog.builder()
                    .name("Avvocato")
                    .image("avvocato.png")
                    .type(NFTType.PAID)
                    .nfts(new ArrayList<>())
                    .bnbCost(new BigDecimal("0.1").setScale(5, RoundingMode.HALF_UP))
                    .mcoinCost(new BigDecimal("0.095").setScale(5, RoundingMode.HALF_UP))
                    .minFarmDays(25)
                    .maxFarmDays(30)
                    .dailyFarm(new BigDecimal("0.00488").setScale(5, RoundingMode.HALF_UP))
                    .build());
            nftCatalogRepository.save(NFTCatalog.builder()
                    .name("Consigliere")
                    .image("consigliere.png")
                    .type(NFTType.PAID)
                    .nfts(new ArrayList<>())
                    .bnbCost(new BigDecimal("0.25").setScale(5, RoundingMode.HALF_UP))
                    .mcoinCost(new BigDecimal("0.2375").setScale(5, RoundingMode.HALF_UP))
                    .minFarmDays(25)
                    .maxFarmDays(30)
                    .dailyFarm(new BigDecimal("0.0124").setScale(5, RoundingMode.HALF_UP))
                    .build());
            nftCatalogRepository.save(NFTCatalog.builder()
                    .name("Sottocapo")
                    .image("sottocapo.png")
                    .type(NFTType.PAID)
                    .nfts(new ArrayList<>())
                    .bnbCost(new BigDecimal("0.5").setScale(5, RoundingMode.HALF_UP))
                    .mcoinCost(new BigDecimal("0.475").setScale(5, RoundingMode.HALF_UP))
                    .minFarmDays(25)
                    .maxFarmDays(30)
                    .dailyFarm(new BigDecimal("0.0252").setScale(5, RoundingMode.HALF_UP))
                    .build());
            nftCatalogRepository.save(NFTCatalog.builder()
                    .name("Regina")
                    .image("regina.png")
                    .type(NFTType.PAID)
                    .nfts(new ArrayList<>())
                    .bnbCost(new BigDecimal("1").setScale(5, RoundingMode.HALF_UP))
                    .mcoinCost(new BigDecimal("0.95").setScale(5, RoundingMode.HALF_UP))
                    .minFarmDays(25)
                    .maxFarmDays(30)
                    .dailyFarm(new BigDecimal("0.0512").setScale(5, RoundingMode.HALF_UP))
                    .build());
            nftCatalogRepository.save(NFTCatalog.builder()
                    .name("Capo")
                    .image("capo.png")
                    .type(NFTType.PAID)
                    .nfts(new ArrayList<>())
                    .bnbCost(new BigDecimal("2").setScale(5, RoundingMode.HALF_UP))
                    .mcoinCost(new BigDecimal("1.9").setScale(5, RoundingMode.HALF_UP))
                    .minFarmDays(25)
                    .maxFarmDays(30)
                    .dailyFarm(new BigDecimal("0.104").setScale(5, RoundingMode.HALF_UP))
                    .build());
        }
    }

}
