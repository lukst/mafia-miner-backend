package com.services.mafia.miner.repository.user;

import com.services.mafia.miner.entity.user.Nonce;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NonceRepository extends JpaRepository<Nonce, String> {

    Optional<Nonce> findNonceByUserAddress(String userAddress);

    void deleteByUserAddress(String userAddress);
}
