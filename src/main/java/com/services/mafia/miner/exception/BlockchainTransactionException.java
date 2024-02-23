package com.services.mafia.miner.exception;

public class BlockchainTransactionException extends RuntimeException{
    public BlockchainTransactionException(String message) {
        super(message);
    }

    public BlockchainTransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}

