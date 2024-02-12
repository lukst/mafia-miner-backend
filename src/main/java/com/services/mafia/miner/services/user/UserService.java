package com.services.mafia.miner.services.user;

import com.services.mafia.miner.dto.user.UserDTO;
import com.services.mafia.miner.entity.user.User;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
    UserDTO getUser(HttpServletRequest request);
    String extractTokenFromRequest(HttpServletRequest request);
    User findUserByToken(String token);
}
