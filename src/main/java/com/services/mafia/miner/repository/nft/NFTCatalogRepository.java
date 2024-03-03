package com.services.mafia.miner.repository.nft;

import com.services.mafia.miner.entity.nft.NFTCatalog;
import com.services.mafia.miner.entity.nft.NFTFamily;
import com.services.mafia.miner.entity.nft.NFTType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NFTCatalogRepository extends JpaRepository<NFTCatalog, Long> {
    Optional<NFTCatalog> findNftCatalogByType(NFTType nftType);
    Optional<NFTCatalog> findNftCatalogByName(String name);
    List<NFTCatalog> findAllByFamily(NFTFamily nftFamily);
}
