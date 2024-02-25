package com.services.mafia.miner.controller.user;

import com.services.mafia.miner.dto.transaction.TransactionDTO;
import com.services.mafia.miner.dto.user.UserDTO;
import com.services.mafia.miner.dto.user.WithdrawalRequestDTO;
import com.services.mafia.miner.dto.user.WithdrawalValidationDTO;
import com.services.mafia.miner.services.transaction.TransactionService;
import com.services.mafia.miner.services.user.WithdrawalService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/api/v1/withdraw")
public class WithdrawalController {
    private final WithdrawalService withdrawalService;
    private final TransactionService transactionService;

    @PostMapping("/request")
    public ResponseEntity<UserDTO> requestWithdrawal(@RequestBody WithdrawalRequestDTO withdrawalRequestDTO,
                                                     HttpServletRequest request) {
        return ResponseEntity.ok().body(withdrawalService.requestWithdrawal(request, withdrawalRequestDTO));
    }

    @PostMapping("/admin/transactions/validate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> validateWithdrawal(@Validated @RequestBody WithdrawalValidationDTO withdrawalValidationDTO,
                                                   HttpServletRequest request) {
        withdrawalService.validateWithdrawal(request, withdrawalValidationDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/admin/transactions/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> rejectWithdrawal(@Validated @RequestBody WithdrawalValidationDTO withdrawalValidationDTO,
                                                 HttpServletRequest request) {
        withdrawalService.rejectWithdrawal(request, withdrawalValidationDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/transactions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TransactionDTO>> getPendingWithdrawTransactions(HttpServletRequest request) {
        return ResponseEntity.ok().body(withdrawalService.findPendingWithdrawTransactions(request));
    }

    @GetMapping("/admin/transactions/{walletId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TransactionDTO>> getAllTransactionsForUser(@RequestParam("page") Integer page,
                                                                          @RequestParam("size") Integer size,
                                                                          @PathVariable String walletId) {
        return ResponseEntity.ok().body(transactionService.getAllTransactionsForUser(walletId, page, size));
    }
}
