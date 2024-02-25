package com.services.mafia.miner.repository.nft;

import com.services.mafia.miner.entity.nft.NFT;
import com.services.mafia.miner.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NFTRepository extends JpaRepository<NFT, Long> {
    Page<NFT> findAllByUser(User user, Pageable pageable);
}
