package com.pgms.repo;

import com.pgms.domain.Tenant;
import com.pgms.domain.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepo extends JpaRepository<Wallet, UUID> {
    Optional<Wallet> findByTenant(Tenant tenant);
}