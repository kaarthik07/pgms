package com.pgms.api;

import com.pgms.domain.Tenant;
import com.pgms.domain.Wallet;
import com.pgms.service.WalletService;
import jakarta.persistence.EntityManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallets")
public class WalletController {
    private final WalletService svc;
    private final EntityManager em;

    public WalletController(WalletService svc, EntityManager em) {
        this.svc = svc;
        this.em = em;
    }

    @GetMapping("/{tenantId}")
    public ResponseEntity<Wallet> get(@PathVariable UUID tenantId) {
        Tenant tenant = em.getReference(Tenant.class, tenantId);
        return ResponseEntity.ok(svc.getOrCreateWallet(tenant));
    }
}
