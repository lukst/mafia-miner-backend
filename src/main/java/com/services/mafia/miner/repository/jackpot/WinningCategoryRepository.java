package com.services.mafia.miner.repository.jackpot;

import com.services.mafia.miner.entity.jackpot.WinningCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WinningCategoryRepository extends JpaRepository<WinningCategory, Long> {
    Optional<WinningCategory> findByRangeStartLessThanEqualAndRangeEndGreaterThanEqual(int generatedNumber1, int generatedNumber2);
}
