package com.services.mafia.miner.controller.transaction;

import com.services.mafia.miner.dto.transaction.TransactionDTO;
import com.services.mafia.miner.services.transaction.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@AllArgsConstructor
@RequestMapping("/api/v1/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping
    @ResponseBody
    public Page<TransactionDTO> getPagedTransactionForUser(@RequestParam("page") Integer page,
                                                           @RequestParam("size") Integer size,
                                                           HttpServletRequest request) {
        return transactionService.filterTransactionsForUser(request, page, size);
    }
}
