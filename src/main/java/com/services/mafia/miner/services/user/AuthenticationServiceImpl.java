package com.services.mafia.miner.services.user;

import com.services.mafia.miner.dto.user.AuthenticationRequest;
import com.services.mafia.miner.dto.user.AuthenticationResponse;
import com.services.mafia.miner.dto.user.NonceResponse;
import com.services.mafia.miner.entity.nft.FarmType;
import com.services.mafia.miner.entity.nft.NFT;
import com.services.mafia.miner.entity.nft.NFTCatalog;
import com.services.mafia.miner.entity.nft.NFTType;
import com.services.mafia.miner.entity.user.*;
import com.services.mafia.miner.exception.InvalidInputException;
import com.services.mafia.miner.repository.nft.NFTCatalogRepository;
import com.services.mafia.miner.repository.nft.NFTRepository;
import com.services.mafia.miner.repository.user.NonceRepository;
import com.services.mafia.miner.repository.user.TokenRepository;
import com.services.mafia.miner.repository.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
    private final NFTRepository nftRepository;
    private final NFTCatalogRepository nftCatalogRepository;
    private final LoginAttemptService loginAttemptService;

    @Override
    @Transactional
    public AuthenticationResponse authenticateUser(HttpServletRequest request, AuthenticationRequest authenticationRequest) {
        String ip = extractIp(request);
        if (loginAttemptService.isBlocked(authenticationRequest.getUserAddress())) {
            throw new AuthenticationServiceException("Too many failed login attempts. Please try again later.");
        }
        String userAddress = authenticationRequest.getUserAddress();
        String signature = authenticationRequest.getSignature();
        Nonce nonce = nonceRepository.findById(userAddress)
                .orElseThrow(() -> {
                    loginAttemptService.loginFailed(userAddress, ip);
                    return new InvalidInputException("Nonce not found " + userAddress + " " + ip);
                });
        boolean isVerified = metamaskAuthService.verifySignature(userAddress.toLowerCase(), nonce.getNonce(), signature);
        if (!isVerified) {
            loginAttemptService.loginFailed(userAddress, ip);
            throw new InvalidInputException("Invalid signature for user " + userAddress + " with ip " + ip);
        }
        var user = userRepository.findByWalletAddress(userAddress)
                .orElseGet(() -> registerUser(authenticationRequest, request));

        loginAttemptService.loginSucceeded(authenticationRequest.getUserAddress());
        nonceRepository.delete(nonce);
        revokeAllUserTokens(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
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

    private User registerUser(AuthenticationRequest authenticationRequest, HttpServletRequest request) {
        String ip = extractIp(request);
        User referUser = null;
        if (authenticationRequest.getReferralCode() != null) {
            referUser = userRepository.findByReferralCode(authenticationRequest.getReferralCode()).orElse(null);
        }
        User user = userRepository.save(User.builder()
                .role(Role.MANAGER)
                .walletAddress(authenticationRequest.getUserAddress())
                .referralCode(randomCodeGeneratorService.generateReferralCode())
                .build());
        if (referUser != null) {
            user.setReferrer(referUser);
        }
        user.getIpAddresses().add(ip);
        if (authenticationRequest.getReferralCode() != null) {
            userRepository.findByReferralCode(authenticationRequest.getReferralCode()).ifPresent(user::setReferrer);
        }
        var savedUser = userRepository.save(user);
        NFTCatalog nftCatalog = nftCatalogRepository.findNftCatalogByType(NFTType.FREE).orElseThrow(() -> new InvalidInputException("Catalog does not exist"));
        nftRepository.save(NFT.builder()
                .user(user)
                .nftCatalog(nftCatalog)
                .farmType(FarmType.BNB)
                .availableMiningDays(50)
                .maxMiningDays(50)
                .build());
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
            if (remoteAddr == null || remoteAddr.isEmpty()) {
                remoteAddr = request.getRemoteAddr();
            }
        }
        return remoteAddr;
    }
}