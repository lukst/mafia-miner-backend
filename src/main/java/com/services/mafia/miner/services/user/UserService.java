package com.services.mafia.miner.services.user;

import com.services.mafia.miner.dto.user.AddBNB;
import com.services.mafia.miner.dto.user.AddBalanceRequest;
import com.services.mafia.miner.dto.user.UserDTO;
import com.services.mafia.miner.entity.user.User;
import com.services.mafia.miner.entity.transaction.Transaction;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.List;

public interface UserService {
    UserDTO getUser(HttpServletRequest request);
    String extractTokenFromRequest(HttpServletRequest request);
    User findUserByToken(String token);
    User findUserByWallet(String wallet);
    UserDTO addPendingTransaction(HttpServletRequest request, AddBalanceRequest addBalanceRequest);
    void addUserBalance(Transaction transaction);
    User save(User user);
    void subtractUserBNB(User userFound, BigDecimal bnbToSubtract);
    void subtractUserMCOIN(User userFound, BigDecimal mcoinToSubtract);
    List<User> findAllByReferrer(User referrer);
    UserDTO addGiftBNB(HttpServletRequest request, AddBNB addBNB);
}
