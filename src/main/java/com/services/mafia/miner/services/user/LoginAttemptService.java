package com.services.mafia.miner.services.user;

public interface LoginAttemptService {
    void loginSucceeded(String username);
    void loginFailed(String username, String ip);
    boolean isBlocked(String username);
}