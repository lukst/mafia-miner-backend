package com.services.mafia.miner.services.user;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Service
@Log4j2
public class MetamaskAuthService {
    public boolean verifySignature(String ethAddress, String nonce, String signature) {
        try {
            String messagePrefix = "Sign this message to confirm you own this wallet address. This action will not cost any gas fees.\n\nNonce: ";
            String message = messagePrefix + nonce;
            String prefixedMessage = "\u0019Ethereum Signed Message:\n" + message.length() + message;
            byte[] messageHash = Hash.sha3(prefixedMessage.getBytes(StandardCharsets.UTF_8));

            // Decode the signature
            byte[] signatureBytes = Numeric.hexStringToByteArray(signature);
            Sign.SignatureData sd = new Sign.SignatureData(
                    signatureBytes[64],
                    Arrays.copyOfRange(signatureBytes, 0, 32),
                    Arrays.copyOfRange(signatureBytes, 32, 64)
            );

            BigInteger publicKey = Sign.signedMessageHashToKey(messageHash, sd);
            String recoveredAddress = "0x" + Keys.getAddress(publicKey);

            // Convert both addresses to checksum address before comparing
            return Keys.toChecksumAddress(recoveredAddress).equals(Keys.toChecksumAddress(ethAddress));
        } catch (Exception e) {
            log.error("Error verifying signature: ", e);
            return false;
        }
    }

}