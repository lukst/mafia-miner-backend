package com.services.mafia.miner.repository.strongbox;

import com.services.mafia.miner.entity.strongbox.StrongBoxGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StrongBoxGameRepository extends JpaRepository<StrongBoxGame, Long> {
    Optional<StrongBoxGame> findTopByOrderByLastResetTimeDesc();
}
