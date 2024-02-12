package com.services.mafia.miner.services.user;

import com.services.mafia.miner.dto.user.UserDTO;
import com.services.mafia.miner.entity.user.Token;
import com.services.mafia.miner.entity.user.User;
import com.services.mafia.miner.exception.InvalidTokenException;
import com.services.mafia.miner.exception.UserNotFoundException;
import com.services.mafia.miner.repository.user.TokenRepository;
import com.services.mafia.miner.repository.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    @Override
    public UserDTO getUser(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        User userFound = findUserByToken(token);
        return modelMapper.map(userFound, UserDTO.class);
    }

    @Override
    public String extractTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidTokenException("Token is not valid");
        }
        return authHeader.substring(7);
    }

    @Override
    public User findUserByToken(String token) {
        Token tokenFound = tokenRepository.findByTokenAndExpiredIsFalseAndRevokedIsFalse(token)
                .orElseThrow(() -> new InvalidTokenException("Token is not valid or expired"));
        return userRepository.findById(tokenFound.getUser().getId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
