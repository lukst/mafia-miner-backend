package com.services.mafia.miner.util;

import java.math.BigDecimal;

public interface Constants {
    String GAME_WALLET = "0x74A5B9B57E2966d6111337D4cc40f9a3508A96DC";
    String HOT_WALLET = "0x5447c113f3C2518791276837A60cC235F861C5eF";
    String DEVELOPER_WALLET = "0xB0Ea76602E36E87C8585b1E2f80e196e69a28cA5";
    String BNB_MAINNET = "0xbb4CdB9CBd36B01bD1cBaEBF2De08d9173bc095c";
    BigDecimal REFERRAL_FEE_PERCENT = new BigDecimal("0.05");
    BigDecimal NFT_FEE = new BigDecimal("0.1");
    String DEPOSIT_OPERATION = "Deposit arrived";
    String DEPOSIT_OPERATION_SUCCESS = "Deposit success";
    String DEPOSIT_OPERATION_FAILED = "Deposit failed";
}
