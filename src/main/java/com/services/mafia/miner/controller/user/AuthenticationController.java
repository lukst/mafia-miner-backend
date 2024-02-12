package com.services.mafia.miner.controller.user;

import com.services.mafia.miner.dto.user.AuthenticationRequest;
import com.services.mafia.miner.dto.user.AuthenticationResponse;
import com.services.mafia.miner.dto.user.NonceResponse;
import com.services.mafia.miner.services.user.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticateUser(
            @RequestBody AuthenticationRequest authenticationRequest,
            HttpServletRequest httpRequest) {
        return new ResponseEntity<>(authenticationService.authenticateUser(httpRequest, authenticationRequest), HttpStatus.OK);
    }

    @GetMapping("/nonce/{userAddress}")
    public ResponseEntity<NonceResponse> getNonce(@PathVariable String userAddress) {
        return new ResponseEntity<>(authenticationService.generateNonceForUser(userAddress), HttpStatus.OK);
    }
}
