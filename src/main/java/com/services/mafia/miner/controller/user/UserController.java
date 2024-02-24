package com.services.mafia.miner.controller.user;

import com.services.mafia.miner.dto.user.AddBalanceRequest;
import com.services.mafia.miner.dto.user.UserDTO;
import com.services.mafia.miner.services.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/info")
    public ResponseEntity<UserDTO> getUserInfo(
            HttpServletRequest request
    ) {
        return new ResponseEntity<>(userService.getUser(request), HttpStatus.OK);
    }

    @PostMapping("/balance/add")
    public ResponseEntity<UserDTO> addBalanceToUserByToken(
            HttpServletRequest request,
            @RequestBody AddBalanceRequest addBalanceRequest
    ) {
        return new ResponseEntity<>(userService.addPendingTransaction(request, addBalanceRequest), HttpStatus.CREATED);
    }
}