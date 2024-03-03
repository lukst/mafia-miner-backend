package com.services.mafia.miner.initialization;

import com.services.mafia.miner.entity.jackpot.Jackpot;
import com.services.mafia.miner.entity.jackpot.JackpotRewardType;
import com.services.mafia.miner.entity.jackpot.WinningCategory;
import com.services.mafia.miner.entity.nft.NFTCatalog;
import com.services.mafia.miner.entity.nft.NFTFamily;
import com.services.mafia.miner.entity.nft.NFTType;
import com.services.mafia.miner.entity.strongbox.StrongBoxGame;
import com.services.mafia.miner.repository.jackpot.JackpotRepository;
import com.services.mafia.miner.repository.jackpot.WinningCategoryRepository;
import com.services.mafia.miner.repository.nft.NFTCatalogRepository;
import com.services.mafia.miner.repository.strongbox.StrongBoxGameRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {
    private final NFTCatalogRepository nftCatalogRepository;
    private final StrongBoxGameRepository strongBoxGameRepository;
    private final JackpotRepository jackpotRepository;
    private final WinningCategoryRepository winningCategoryRepository;

    @Override
    public void run(String... args) {
        createNFTCatalog();
        generateFirstStrongboxGame();
        createFirstJackpot();
        createWinningCategory();
    }

    private void createWinningCategory() {
        if (!winningCategoryRepository.findAll().isEmpty()) {
            return;
        }
        winningCategoryRepository.save(WinningCategory.builder()
                .rewardType(JackpotRewardType.NOTHING)
                .winningChance("87.7")
                .rangeStart(124)
                .rangeEnd(1000)
                .build());
        winningCategoryRepository.save(WinningCategory.builder()
                .rewardType(JackpotRewardType.X1point5)
                .winningChance("6")
                .rangeStart(64)
                .rangeEnd(123)
                .build());
        winningCategoryRepository.save(WinningCategory.builder()
                .rewardType(JackpotRewardType.X2)
                .winningChance("3")
                .rangeStart(34)
                .rangeEnd(63)
                .build());
        winningCategoryRepository.save(WinningCategory.builder()
                .rewardType(JackpotRewardType.X3)
                .winningChance("1.5")
                .rangeStart(19)
                .rangeEnd(33)
                .build());
        winningCategoryRepository.save(WinningCategory.builder()
                .rewardType(JackpotRewardType.X5)
                .winningChance("1")
                .rangeStart(9)
                .rangeEnd(18)
                .build());
        winningCategoryRepository.save(WinningCategory.builder()
                .rewardType(JackpotRewardType.X10)
                .winningChance("0.5")
                .rangeStart(4)
                .rangeEnd(8)
                .build());
        winningCategoryRepository.save(WinningCategory.builder()
                .rewardType(JackpotRewardType.SOLDATO)
                .winningChance("0.2")
                .rangeStart(2)
                .rangeEnd(3)
                .build());
        winningCategoryRepository.save(WinningCategory.builder()
                .rewardType(JackpotRewardType.JACKPOT)
                .winningChance("0.1")
                .rangeStart(1)
                .rangeEnd(1)
                .build());
    }

    private void createFirstJackpot() {
        List<Jackpot> jackpots = jackpotRepository.findAll();
        if (jackpots.isEmpty()) {
            Jackpot firstJackpot = Jackpot.builder().build();
            jackpotRepository.save(firstJackpot);
        }
    }

    private void generateFirstStrongboxGame() {
        if (strongBoxGameRepository.findAll().isEmpty()) {
            StrongBoxGame game = StrongBoxGame.builder()
                    .winningCombination((int) (Math.random() * 100))
                    .build();
            game.initializeCombinations();
            strongBoxGameRepository.save(game);
        }
    }

    private void createNFTCatalog() {
        if (nftCatalogRepository.findAll().isEmpty()) {
            nftCatalogRepository.save(NFTCatalog.builder()
                    .name("Chihuahua")
                    .image("free.png")
                    .type(NFTType.FREE)
                    .nfts(new ArrayList<>())
                    .bnbCost(BigDecimal.ZERO)
                    .mcoinCost(BigDecimal.ZERO)
                    .minFarmDays(50)
                    .maxFarmDays(50)
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
        if (nftCatalogRepository.findAllByFamily(NFTFamily.YAKUZA).isEmpty()) {
            nftCatalogRepository.save(NFTCatalog.builder()
                    .name("Shatei")
                    .image("shatei.png")
                    .roi("200%")
                    .type(NFTType.PAID)
                    .family(NFTFamily.YAKUZA)
                    .nfts(new ArrayList<>())
                    .bnbCost(new BigDecimal("0.04").setScale(5, RoundingMode.HALF_UP))
                    .mcoinCost(new BigDecimal("10").setScale(5, RoundingMode.HALF_UP))
                    .minFarmDays(42)
                    .maxFarmDays(42)
                    .dailyFarm(new BigDecimal("0.00190").setScale(5, RoundingMode.HALF_UP))
                    .build());
            nftCatalogRepository.save(NFTCatalog.builder()
                    .name("Kyodai")
                    .image("kyodai.png")
                    .roi("200%")
                    .type(NFTType.PAID)
                    .family(NFTFamily.YAKUZA)
                    .nfts(new ArrayList<>())
                    .bnbCost(new BigDecimal("0.06").setScale(5, RoundingMode.HALF_UP))
                    .mcoinCost(new BigDecimal("10").setScale(5, RoundingMode.HALF_UP))
                    .minFarmDays(42)
                    .maxFarmDays(42)
                    .dailyFarm(new BigDecimal("0.00286").setScale(5, RoundingMode.HALF_UP))
                    .build());
            nftCatalogRepository.save(NFTCatalog.builder()
                    .name("Shingiin")
                    .image("shingiin.png")
                    .roi("200%")
                    .type(NFTType.PAID)
                    .family(NFTFamily.YAKUZA)
                    .nfts(new ArrayList<>())
                    .bnbCost(new BigDecimal("0.1").setScale(5, RoundingMode.HALF_UP))
                    .mcoinCost(new BigDecimal("10").setScale(5, RoundingMode.HALF_UP))
                    .minFarmDays(42)
                    .maxFarmDays(42)
                    .dailyFarm(new BigDecimal("0.00476").setScale(5, RoundingMode.HALF_UP))
                    .build());
            nftCatalogRepository.save(NFTCatalog.builder()
                    .name("Shateigashira")
                    .image("shateigashira.png")
                    .roi("200%")
                    .type(NFTType.PAID)
                    .family(NFTFamily.YAKUZA)
                    .nfts(new ArrayList<>())
                    .bnbCost(new BigDecimal("0.2").setScale(5, RoundingMode.HALF_UP))
                    .mcoinCost(new BigDecimal("10").setScale(5, RoundingMode.HALF_UP))
                    .minFarmDays(42)
                    .maxFarmDays(42)
                    .dailyFarm(new BigDecimal("0.00952").setScale(5, RoundingMode.HALF_UP))
                    .build());
            nftCatalogRepository.save(NFTCatalog.builder()
                    .name("Wakagashira")
                    .image("wakagashira.png")
                    .roi("200%")
                    .type(NFTType.PAID)
                    .family(NFTFamily.YAKUZA)
                    .nfts(new ArrayList<>())
                    .bnbCost(new BigDecimal("0.4").setScale(5, RoundingMode.HALF_UP))
                    .mcoinCost(new BigDecimal("10").setScale(5, RoundingMode.HALF_UP))
                    .minFarmDays(42)
                    .maxFarmDays(42)
                    .dailyFarm(new BigDecimal("0.01905").setScale(5, RoundingMode.HALF_UP))
                    .build());
            nftCatalogRepository.save(NFTCatalog.builder()
                    .name("Oyabun")
                    .image("oyabun.png")
                    .roi("200%")
                    .type(NFTType.PAID)
                    .family(NFTFamily.YAKUZA)
                    .nfts(new ArrayList<>())
                    .bnbCost(new BigDecimal("1").setScale(5, RoundingMode.HALF_UP))
                    .mcoinCost(new BigDecimal("10").setScale(5, RoundingMode.HALF_UP))
                    .minFarmDays(42)
                    .maxFarmDays(42)
                    .dailyFarm(new BigDecimal("0.04762").setScale(5, RoundingMode.HALF_UP))
                    .build());
        }
    }

}
