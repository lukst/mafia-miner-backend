package com.services.mafia.miner.entity.user;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String walletAddress;
    @Column
    private String country;
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime created = LocalDateTime.now();
    @Column(unique = true, nullable = false)
    private String referralCode;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referrer_id")
    private User referrer;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    @ToString.Exclude
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Token> tokens;
    @Column(nullable = false, precision = 19, scale = 5)
    @Builder.Default
    private BigDecimal bnbBalance = new BigDecimal("0").setScale(5, RoundingMode.HALF_UP);
    @Column(nullable = false, precision = 19, scale = 5)
    @Builder.Default
    private BigDecimal mcoinBalance = new BigDecimal("0").setScale(5, RoundingMode.HALF_UP);
    @Column(nullable = false, precision = 19, scale = 5)
    @Builder.Default
    private BigDecimal totalDeposit = new BigDecimal("0").setScale(5, RoundingMode.HALF_UP);
    @Column(nullable = false, precision = 19, scale = 5)
    @Builder.Default
    private BigDecimal totalWithdraw = new BigDecimal("0").setScale(5, RoundingMode.HALF_UP);
    @Column(nullable = false)
    @Builder.Default
    private boolean isAccountNonLocked = true;
    @ElementCollection
    @CollectionTable(name = "user_ips", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "ip_address")
    @Builder.Default
    private Set<String> ipAddresses = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return walletAddress;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}