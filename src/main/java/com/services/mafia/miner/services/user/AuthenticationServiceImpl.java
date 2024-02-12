package com.services.mafia.miner.services.user;

import com.services.mafia.miner.dto.user.AuthenticationRequest;
import com.services.mafia.miner.dto.user.AuthenticationResponse;
import com.services.mafia.miner.dto.user.NonceResponse;
import com.services.mafia.miner.entity.user.*;
import com.services.mafia.miner.exception.InvalidInputException;
import com.services.mafia.miner.repository.user.NonceRepository;
import com.services.mafia.miner.repository.user.TokenRepository;
import com.services.mafia.miner.repository.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final RandomCodeGeneratorService randomCodeGeneratorService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final MetamaskAuthService metamaskAuthService;
    private final NonceRepository nonceRepository;
    private final IpGeolocationService ipGeolocationService;
    private final TokenRepository tokenRepository;

    @Override
    @Transactional
    public AuthenticationResponse authenticateUser(HttpServletRequest request, AuthenticationRequest authenticationRequest) {
        String userAddress = authenticationRequest.getUserAddress();
        String signature = authenticationRequest.getSignature();
        Nonce nonce = nonceRepository.findById(userAddress)
                .orElseThrow(() -> new InvalidInputException("Nonce not found"));
        boolean isVerified = metamaskAuthService.verifySignature(userAddress.toLowerCase(), nonce.getNonce(), signature);
        if (!isVerified) {
            throw new InvalidInputException("Invalid signature");
        }
        var user = userRepository.findByWalletAddress(userAddress)
                .orElseGet(() -> registerUser(authenticationRequest, request));
        nonceRepository.delete(nonce);
        revokeAllUserTokens(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        String ip = extractIp(request);
        saveUserToken(user, jwtToken);
        user.getIpAddresses().add(ip);
        userRepository.save(user);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public NonceResponse generateNonceForUser(String userAddress) {
        String nonce = randomCodeGeneratorService.generateNonceCode();
        Nonce nonceEntity = nonceRepository.save(Nonce.builder()
                .userAddress(userAddress)
                .nonce(nonce)
                .createdAt(LocalDateTime.now())
                .build());
        return NonceResponse.builder().nonce(nonceEntity.getNonce()).build();
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private User registerUser(AuthenticationRequest authenticationRequest, HttpServletRequest request){
        String ip = extractIp(request);
        User user = userRepository.save(User.builder()
                .role(Role.MANAGER)
                .walletAddress(authenticationRequest.getUserAddress())
                .referralCode(randomCodeGeneratorService.generateReferralCode())
                .build());
        user.getIpAddresses().add(ip);
        if (authenticationRequest.getReferralCode() != null) {
            User referrer = userRepository.findByReferralCode(authenticationRequest.getReferralCode()).orElse(null);
            if (referrer != null) {
                user.setReferrer(referrer);
            }
        }
        var savedUser = userRepository.save(user);
        ipGeolocationService.getCountryByIp(ip).thenAccept(country -> {
            user.setCountry(country);
            userRepository.save(user);
        });
        return savedUser;
    }

    private String extractIp(HttpServletRequest request) {
        String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }
        return remoteAddr;
    }
}