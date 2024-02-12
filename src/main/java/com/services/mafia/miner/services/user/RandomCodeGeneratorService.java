package com.services.mafia.miner.services.user;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
public class RandomCodeGeneratorService {

    private static final int REFERRAL_CODE_LENGTH = 10;
    private static final int NONCE_LENGTH = 15;

    public String generateReferralCode() {
        return RandomStringUtils.randomAlphanumeric(REFERRAL_CODE_LENGTH);
    }

    public String generateNonceCode() {
        return RandomStringUtils.randomAlphanumeric(NONCE_LENGTH);
    }
}