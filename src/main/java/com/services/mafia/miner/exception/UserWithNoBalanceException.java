package com.services.mafia.miner.exception;

public class UserWithNoBalanceException extends RuntimeException {
    public UserWithNoBalanceException(String message) {
        super(message);
    }

    public UserWithNoBalanceException(String message, Throwable cause) {
        super(message, cause);
    }
}
