package com.services.mafia.miner.repository.strongbox;

import com.services.mafia.miner.entity.strongbox.StrongboxHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StrongboxHistoryRepository extends JpaRepository<StrongboxHistory, Long> {
    List<StrongboxHistory> findTop5ByOrderByWinTimeDesc();
}
