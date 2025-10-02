package com.pgms.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "referrals")
public class Referral {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "referrer_id", nullable = false)
    private Tenant referrer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "referred_id", nullable = false)
    private Tenant referred;

    @Column(nullable = false)
    private LocalDate referredDate = LocalDate.now();

    @Column(nullable = false)
    private boolean rewardClaimed = false;

    // Getters & Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Tenant getReferrer() { return referrer; }
    public void setReferrer(Tenant referrer) { this.referrer = referrer; }

    public Tenant getReferred() { return referred; }
    public void setReferred(Tenant referred) { this.referred = referred; }

    public LocalDate getReferredDate() { return referredDate; }
    public void setReferredDate(LocalDate referredDate) { this.referredDate = referredDate; }

    public boolean isRewardClaimed() { return rewardClaimed; }
    public void setRewardClaimed(boolean rewardClaimed) { this.rewardClaimed = rewardClaimed; }
}
