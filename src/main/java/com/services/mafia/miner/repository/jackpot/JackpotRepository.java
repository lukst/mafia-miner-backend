package com.services.mafia.miner.repository.jackpot;

import com.services.mafia.miner.entity.jackpot.Jackpot;
import com.services.mafia.miner.entity.jackpot.JackpotStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JackpotRepository extends JpaRepository<Jackpot, Long> {
    @Lock(LockModeType.OPTIMISTIC)
    Optional<Jackpot> findFirstByOrderByIdDesc();
    Optional<Jackpot> findFirstByStatusOrderByIdDesc(JackpotStatus status);
}

