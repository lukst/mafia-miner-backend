package com.services.mafia.miner.services.user;

import com.services.mafia.miner.dto.transaction.TransactionDTO;
import com.services.mafia.miner.dto.user.UserDTO;
import com.services.mafia.miner.dto.user.WithdrawalRequestDTO;
import com.services.mafia.miner.dto.user.WithdrawalValidationDTO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface WithdrawalService {
    UserDTO requestWithdrawal(HttpServletRequest request, WithdrawalRequestDTO withdrawalRequestDTO);
    void validateWithdrawal(HttpServletRequest request, WithdrawalValidationDTO withdrawalValidationDTO);
    void rejectWithdrawal(HttpServletRequest request, WithdrawalValidationDTO withdrawalValidationDTO);
    List<TransactionDTO> findPendingWithdrawTransactions(HttpServletRequest request);
}
