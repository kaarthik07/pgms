package com.pgms.domain;

import com.pgms.util.Enums;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.StringJoiner;
import java.util.UUID;

@Entity
@Table(name = "receipts", indexes = {
        @Index(name = "ix_receipts_org_id", columnList = "org_id"),
        @Index(name = "ix_receipts_tenant_id", columnList = "tenant_id"),
        @Index(name = "ix_receipts_bill_id", columnList = "bill_id"),
        @Index(name = "ix_receipts_paid_at", columnList = "paid_at")
})
public class Receipt {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "org_id")
    private Organization org;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bill_id")
    private Bill bill;

    @Column(name = "amount_paid", nullable = false, precision = 12, scale = 2)
    private BigDecimal amountPaid;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_mode", nullable = false)
    private Enums.PaymentMode paymentMode;

    @Column(name = "txn_ref", length = 120)
    private String txnRef;

    @Column(name = "paid_at", nullable = false)
    private OffsetDateTime paidAt = OffsetDateTime.now();

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Organization getOrg() {
        return org;
    }

    public void setOrg(Organization org) {
        this.org = org;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Enums.PaymentMode getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(Enums.PaymentMode paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getTxnRef() {
        return txnRef;
    }

    public void setTxnRef(String txnRef) {
        this.txnRef = txnRef;
    }

    public OffsetDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(OffsetDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Receipt.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("org=" + org)
                .add("tenant=" + tenant)
                .add("bill=" + bill)
                .add("amountPaid=" + amountPaid)
                .add("paymentMode=" + paymentMode)
                .add("txnRef='" + txnRef + "'")
                .add("paidAt=" + paidAt)
                .add("createdAt=" + createdAt)
                .toString();
    }
}
