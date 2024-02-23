package com.services.mafia.miner.util;

import java.math.BigDecimal;

public interface Constants {
    String GAME_WALLET_ADDRESS = "0xB0Ea76602E36E87C8585b1E2f80e196e69a28cA5";
    String DEVELOPER_WALLET_ADDRESS = "0x2c97ADc8d1E280f6dB24dE073263B2BbD82763Ca";
    String BNB_MAINNET_CONTRACT_ADDRESS = "0xbb4CdB9CBd36B01bD1cBaEBF2De08d9173bc095c";
    BigDecimal REFERRAL_FEE_PERCENT = new BigDecimal("0.03");
    String DEPOSIT_OPERATION = "Deposit arrived";
    String DEPOSIT_OPERATION_SUCCESS = "Deposit success";
    String DEPOSIT_OPERATION_FAILED = "Deposit failed";
}
