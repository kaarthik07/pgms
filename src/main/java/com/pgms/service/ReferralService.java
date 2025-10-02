package com.pgms.service;

import com.pgms.domain.Referral;
import com.pgms.domain.Tenant;
import com.pgms.domain.Wallet;
import com.pgms.exception.BadRequestException;
import com.pgms.exception.NotFoundException;
import com.pgms.repo.ReferralRepo;
import com.pgms.repo.TenantRepo;
import com.pgms.repo.WalletRepo;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class ReferralService {
    private final ReferralRepo referrals;
    private final WalletRepo wallets;
    private final TenantRepo tenants;
    private final EntityManager em;

    public ReferralService(ReferralRepo referrals, WalletRepo wallets, TenantRepo tenants, EntityManager em) {
        this.referrals = referrals;
        this.wallets = wallets;
        this.tenants = tenants;
        this.em = em;
    }

    @Transactional
    public UUID createReferral(UUID referrerId, UUID referredId) {
        if (referrerId == null || referredId == null) {
            throw new BadRequestException("referrerId and referredId are required");
        }
        if (referrerId.equals(referredId)) {
            throw new BadRequestException("Self-referral is not allowed");
        }
        // validate both tenants exist
        if (!tenants.existsById(referrerId)) throw new NotFoundException("Referrer not found");
        if (!tenants.existsById(referredId)) throw new NotFoundException("Referred tenant not found");

        // prevent duplicate referral
        if (referrals.existsByReferrerIdAndReferredId(referrerId, referredId)) {
            throw new BadRequestException("Referral already recorded for this pair");
        }

        // obtain references (cheap proxies, no full fetch)
        Tenant referrer = tenants.findById(referrerId).orElseThrow(() -> new NotFoundException("Referrer not found"));
        Tenant referred = tenants.findById(referredId).orElseThrow(() -> new NotFoundException("Referred tenant not found"));

        Referral r = new Referral();
        r.setReferrer(referrer);
        r.setReferred(referred);
        referrals.save(r);

        // reward logic (example: ₹500 credit to referrer)
        Wallet wallet = wallets.findByTenant(referrer)
                .orElseGet(() -> {
                    Wallet w = new Wallet();
                    w.setTenant(referrer);
                    return wallets.save(w);
                });
        wallet.credit(new BigDecimal("500.00"));
        wallets.save(wallet);

        return r.getId();
    }

    public List<Referral> getReferralsForTenant(UUID tenantId) {
        return referrals.findByReferrerId(tenantId);
    }
}
