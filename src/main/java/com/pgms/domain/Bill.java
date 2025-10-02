package com.pgms.domain;

import com.pgms.util.Enums;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.StringJoiner;
import java.util.UUID;

@Entity
@Table(name = "bills", indexes = {
        @Index(name = "ix_bills_org_id", columnList = "org_id"),
        @Index(name = "ix_bills_tenant_id", columnList = "tenant_id"),
        @Index(name = "ix_bills_period", columnList = "period_start,period_end"),
        @Index(name = "ix_bills_status", columnList = "status")
})
public class Bill {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "org_id")
    private Organization org;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "rent_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal rentAmount = BigDecimal.ZERO;

    @Column(name = "utilities_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal utilitiesAmount = BigDecimal.ZERO;

    @Column(name = "discount_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "paid_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Enums.BillStatus status = Enums.BillStatus.PENDING;

    @Column(length = 500)
    private String notes;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    // ---- convenience totals ----
    @Transient
    public BigDecimal totalAmount() {
        return rentAmount.add(utilitiesAmount).subtract(discountAmount.max(BigDecimal.ZERO));
    }

    @Transient
    public BigDecimal outstanding() {
        return totalAmount().subtract(paidAmount);
    }

    public void applyPayment(BigDecimal amount) {
        this.paidAmount = this.paidAmount.add(amount);
        if (outstanding().signum() <= 0) {
            this.status = Enums.BillStatus.PAID;
            this.paidAmount = totalAmount();
        } else {
            this.status = Enums.BillStatus.PARTIAL;
        }
        this.updatedAt = OffsetDateTime.now();
    }

    public void maybeMarkOverdue(LocalDate today) {
        if ((status == Enums.BillStatus.PENDING || status == Enums.BillStatus.PARTIAL) && today.isAfter(dueDate)) {
            this.status = Enums.BillStatus.OVERDUE;
            this.updatedAt = OffsetDateTime.now();
        }
    }

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

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDate periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getRentAmount() {
        return rentAmount;
    }

    public void setRentAmount(BigDecimal rentAmount) {
        this.rentAmount = rentAmount;
    }

    public BigDecimal getUtilitiesAmount() {
        return utilitiesAmount;
    }

    public void setUtilitiesAmount(BigDecimal utilitiesAmount) {
        this.utilitiesAmount = utilitiesAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Enums.BillStatus getStatus() {
        return status;
    }

    public void setStatus(Enums.BillStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Bill.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("org=" + org)
                .add("tenant=" + tenant)
                .add("periodStart=" + periodStart)
                .add("periodEnd=" + periodEnd)
                .add("dueDate=" + dueDate)
                .add("rentAmount=" + rentAmount)
                .add("utilitiesAmount=" + utilitiesAmount)
                .add("discountAmount=" + discountAmount)
                .add("paidAmount=" + paidAmount)
                .add("status=" + status)
                .add("notes='" + notes + "'")
                .add("createdAt=" + createdAt)
                .add("updatedAt=" + updatedAt)
                .toString();
    }
}
