package com.services.mafia.miner.services.user;

import com.services.mafia.miner.dto.user.AuthenticationRequest;
import com.services.mafia.miner.dto.user.AuthenticationResponse;
import com.services.mafia.miner.dto.user.NonceResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {
    AuthenticationResponse authenticateUser(HttpServletRequest request, AuthenticationRequest authenticationRequest);
    NonceResponse generateNonceForUser(String userAddress);
}
