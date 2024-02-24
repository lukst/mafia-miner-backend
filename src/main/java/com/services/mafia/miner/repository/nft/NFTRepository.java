package com.services.mafia.miner.repository.nft;

import com.services.mafia.miner.entity.nft.NFT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NFTRepository extends JpaRepository<NFT, Long> {
}
