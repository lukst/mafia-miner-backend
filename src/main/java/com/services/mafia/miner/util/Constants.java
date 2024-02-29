package com.services.mafia.miner.util;

import java.math.BigDecimal;

public interface Constants {
    String GAME_WALLET = "0x118e61f22C180527ad390F2F0B9e14382fcc568e";
    String HOT_WALLET = "0xe2831D7daBAf0685f2764Be259B9648Fa0a6669F";
    String DEVELOPER_WALLET = "0xF7aDf469060906C1930375E01FDBCdd4Afc83711";
    String BNB_MAINNET = "0xbb4CdB9CBd36B01bD1cBaEBF2De08d9173bc095c";
    BigDecimal REFERRAL_FEE_PERCENT = new BigDecimal("0.05");
    BigDecimal NFT_FEE = new BigDecimal("0.1");
    BigDecimal WITHDRAW_FEE = new BigDecimal("0.05");
    BigDecimal MINIMUM_DEPOSIT = new BigDecimal("0.00025");
    BigDecimal MINIMUM_WITHDRAW = new BigDecimal("0.03");
    String DEPOSIT_OPERATION = "Deposit arrived";
    String DEPOSIT_OPERATION_SUCCESS = "Deposit success";
    String DEPOSIT_OPERATION_FAILED = "Deposit failed";
    String MINT_NFT_OPERATION = "Mint NFT";
    String STRONGBOX_ATTEMPT = "Strongbox attempt";
    String NFT_REWARD = "Reward";
}
