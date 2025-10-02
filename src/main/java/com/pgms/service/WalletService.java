package com.pgms.service;

import com.pgms.domain.Tenant;
import com.pgms.domain.Wallet;
import com.pgms.repo.WalletRepo;
import org.springframework.stereotype.Service;

@Service
public class WalletService {
    private final WalletRepo wallets;

    public WalletService(WalletRepo wallets) {
        this.wallets = wallets;
    }

    public Wallet getOrCreateWallet(Tenant tenant) {
        return wallets.findByTenant(tenant).orElseGet(() -> {
            Wallet w = new Wallet();
            w.setTenant(tenant);
            return wallets.save(w);
        });
    }
}