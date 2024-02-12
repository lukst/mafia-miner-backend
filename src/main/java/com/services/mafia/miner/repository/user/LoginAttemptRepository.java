package com.services.mafia.miner.repository.user;

import com.services.mafia.miner.entity.user.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {
    void deleteByUsername(String username);
    Optional<LoginAttempt> findByUsername(String username);
}
