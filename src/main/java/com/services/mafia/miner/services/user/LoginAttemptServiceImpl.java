package com.services.mafia.miner.services.user;

import com.services.mafia.miner.entity.user.LoginAttempt;
import com.services.mafia.miner.repository.user.LoginAttemptRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class LoginAttemptServiceImpl implements LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration BLOCK_DURATION = Duration.ofMinutes(1);

    private final LoginAttemptRepository repository;

    @Override
    public void loginSucceeded(String username) {
        // Reset attempt count upon successful login
        repository.deleteByUsername(username);
    }

    @Override
    public void loginFailed(String username, String ip) {
        // Increment attempt count upon failed login
        LoginAttempt attempt = repository.findByUsername(username)
                .orElse(LoginAttempt.builder()
                        .username(username)
                        .ip(ip == null ? "0.0.0.0" : ip)
                        .build());
        if (attempt.getCreated() == null) {
            attempt.setCreated(LocalDateTime.now());
        } else {
            attempt.setCreated(attempt.getCreated());
        }
        attempt.setAttemptCount(attempt.getAttemptCount() + 1);
        attempt.setLastAttempt(LocalDateTime.now());
        repository.save(attempt);
    }

    public boolean isBlocked(String username) {
        LoginAttempt attempt = repository.findByUsername(username).orElse(null);
        if (attempt == null) {
            return false;
        }

        if (attempt.getAttemptCount() < MAX_ATTEMPTS) {
            return false;
        }

        // If user has exceeded max attempts, check if block duration has expired
        Duration timeSinceLastAttempt = Duration.between(attempt.getLastAttempt(), LocalDateTime.now());
        if (timeSinceLastAttempt.compareTo(BLOCK_DURATION) > 0) {
            repository.delete(attempt);
            return false;  // Unblock the user
        }

        return true;
    }
}