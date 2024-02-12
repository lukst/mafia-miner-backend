package com.services.mafia.miner.repository.user;

import com.services.mafia.miner.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByWalletAddress(String walletAddress);
    Optional<User> findByReferralCode(String referralCode);
    @Query("SELECT SUM(u.totalDeposit) FROM User u")
    BigDecimal sumTotalDeposit();
    @Query("SELECT SUM(u.totalWithdraw) FROM User u")
    BigDecimal sumTotalWithdraw();
    List<User> findAllByReferrer(User referrer);
}
