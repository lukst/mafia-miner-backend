package com.services.mafia.miner.services;

import com.services.mafia.miner.exception.InvalidInputException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitingCacheService {
    private static class RateLimitInfo {
        LocalDateTime lastActionTime;
        int actionCount;
    }

    private ConcurrentHashMap<Long, RateLimitInfo> userRateLimitCache = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, RateLimitInfo> userActionRateLimitCache = new ConcurrentHashMap<>();

    public synchronized void checkRateLimit(Long userId, int quantityToMint) {
        RateLimitInfo info = userRateLimitCache.getOrDefault(userId, new RateLimitInfo());

        LocalDateTime now = LocalDateTime.now();
        if (info.lastActionTime == null ||
                ChronoUnit.SECONDS.between(info.lastActionTime, now) >= 2) {
            // Reset or start new window
            info.lastActionTime = now;
            info.actionCount = quantityToMint;
        } else {
            // Within the 5-second window
            if (info.actionCount + quantityToMint > 1) {
                long timeBlocked = 2 - ChronoUnit.SECONDS.between(info.lastActionTime, now);
                throw new InvalidInputException("Limit exceeded. Please wait " + timeBlocked + " more seconds.");
            }
            info.actionCount += quantityToMint;
        }
        userRateLimitCache.put(userId, info);
    }

    public synchronized void checkRateLimitForJackpotPlay(Long userId) {
        RateLimitInfo info = userActionRateLimitCache.getOrDefault(userId, new RateLimitInfo());

        LocalDateTime now = LocalDateTime.now();
        // Checks if more than 3 seconds have passed since the last play
        if (info.lastActionTime == null || ChronoUnit.SECONDS.between(info.lastActionTime, now) >= 3) {
            // Allows the play, resetting the timer
            info.lastActionTime = now;
            info.actionCount = 1; // Sets actionCount to 1, assuming one play per action
        } else {
            // If less than 3 seconds have passed, blocks the play
            long timeToWait = 3 - ChronoUnit.SECONDS.between(info.lastActionTime, now);
            throw new InvalidInputException("Please wait " + timeToWait + " more seconds to play again.");
        }
        userActionRateLimitCache.put(userId, info);
    }
}