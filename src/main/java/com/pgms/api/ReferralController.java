package com.pgms.api;

import com.pgms.domain.Referral;
import com.pgms.service.ReferralService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/referrals")
public class ReferralController {

    private final ReferralService svc;

    public ReferralController(ReferralService svc) {
        this.svc = svc;
    }

    @PostMapping
    public ResponseEntity<UUID> createReferral(@RequestParam UUID referrerId,
                                               @RequestParam UUID referredId) {
        return ResponseEntity.ok(svc.createReferral(referrerId, referredId));
    }

    @GetMapping("/{tenantId}")
    public ResponseEntity<List<Referral>> listByReferrer(@PathVariable UUID tenantId) {
        return ResponseEntity.ok(svc.getReferralsForTenant(tenantId));
    }
}
