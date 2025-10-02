package com.pgms.domain;

import com.pgms.util.Enums;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.StringJoiner;
import java.util.UUID;

@Entity
@Table(
    name = "dues",
    indexes = {
        @Index(name = "ix_dues_org_id", columnList = "org_id"),
        @Index(name = "ix_dues_tenant_id", columnList = "tenant_id"),
        @Index(name = "ix_dues_phone", columnList = "tenant_phone"),
        @Index(name = "ix_dues_status", columnList = "status")
    }
)
public class Due {

    @Id
    @GeneratedValue
    private UUID id;

    // Which PG/org reported this due
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "org_id", nullable = false)
    private Organization org;

    // Link to tenant if present in the same system (optional cross-PG)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    // Snapshot identifiers for cross-PG checks (not null)
    @Column(name = "tenant_name", nullable = false, length = 120)
    private String tenantName;

    @Column(name = "tenant_phone", nullable = false, length = 15)
    private String tenantPhone;

    // Optional government/ID snapshot for stronger matches
    @Column(name = "tenant_gov_id", length = 40)
    private String tenantGovId;

    // Money and reason
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "reason", nullable = false, length = 200)
    private String reason;

    // Dates & state
    @Column(name = "from_date")
    private LocalDate fromDate;

    @Column(name = "to_date")
    private LocalDate toDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Enums.DueStatus status = Enums.DueStatus.OPEN;

    // Clearing / dispute metadata
    @Column(name = "cleared_at")
    private OffsetDateTime clearedAt;

    @Column(name = "cleared_notes", length = 200)
    private String clearedNotes;

    @Column(name = "dispute_notes", length = 200)
    private String disputeNotes;

    // Audit
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    // --- getters/setters ---

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Organization getOrg() { return org; }
    public void setOrg(Organization org) { this.org = org; }

    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public String getTenantName() { return tenantName; }
    public void setTenantName(String tenantName) { this.tenantName = tenantName; }

    public String getTenantPhone() { return tenantPhone; }
    public void setTenantPhone(String tenantPhone) { this.tenantPhone = tenantPhone; }

    public String getTenantGovId() { return tenantGovId; }
    public void setTenantGovId(String tenantGovId) { this.tenantGovId = tenantGovId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDate getFromDate() { return fromDate; }
    public void setFromDate(LocalDate fromDate) { this.fromDate = fromDate; }

    public LocalDate getToDate() { return toDate; }
    public void setToDate(LocalDate toDate) { this.toDate = toDate; }

    public Enums.DueStatus getStatus() { return status; }
    public void setStatus(Enums.DueStatus status) { this.status = status; }

    public OffsetDateTime getClearedAt() { return clearedAt; }
    public void setClearedAt(OffsetDateTime clearedAt) { this.clearedAt = clearedAt; }

    public String getClearedNotes() { return clearedNotes; }
    public void setClearedNotes(String clearedNotes) { this.clearedNotes = clearedNotes; }

    public String getDisputeNotes() { return disputeNotes; }
    public void setDisputeNotes(String disputeNotes) { this.disputeNotes = disputeNotes; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PreUpdate
    public void touch() { this.updatedAt = OffsetDateTime.now(); }

    @Override
    public String toString() {
        return new StringJoiner(", ", Due.class.getSimpleName() + "[", "]")
            .add("id=" + id)
            .add("org=" + (org != null ? org.getId() : null))
            .add("tenant=" + (tenant != null ? tenant.getId() : null))
            .add("tenantPhone='" + tenantPhone + "'")
            .add("amount=" + amount)
            .add("status=" + status)
            .toString();
    }
}
