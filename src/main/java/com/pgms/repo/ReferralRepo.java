package com.pgms.repo;

import com.pgms.domain.Referral;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReferralRepo extends JpaRepository<Referral, UUID> {
    List<Referral> findByReferrerId(UUID referrerId);
    List<Referral> findByReferredId(UUID referredId);

    boolean existsByReferredId(UUID referredId);
    boolean existsByReferrerIdAndReferredId(UUID referrerId, UUID referredId);
}
