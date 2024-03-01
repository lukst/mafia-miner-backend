package com.services.mafia.miner.repository.jackpot;

import com.services.mafia.miner.entity.jackpot.JackpotHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JackpotHistoryRepository extends JpaRepository<JackpotHistory, Long> {
    List<JackpotHistory> findTop30ByOrderByTimestampDesc();
}
