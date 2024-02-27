package com.services.mafia.miner.controller.user;

import com.services.mafia.miner.dto.transaction.TransactionDTO;
import com.services.mafia.miner.dto.user.AddBNB;
import com.services.mafia.miner.dto.user.AddBalanceRequest;
import com.services.mafia.miner.dto.user.UserDTO;
import com.services.mafia.miner.services.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/admin/send-bnb")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> sendBNB(
            HttpServletRequest request,
            @RequestBody AddBNB addBNB) {
        return ResponseEntity.ok().body(userService.addGiftBNB(request, addBNB));
    }
}
