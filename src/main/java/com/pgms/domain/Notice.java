package com.pgms.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;
import java.util.StringJoiner;

@Entity
@Table(name = "notices")
public class Notice {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false)
    private String type; // VACATE, WARNING, INFO (you can convert to enum later)

    @Column(nullable = false)
    private String message;

    @Column(name = "issued_date")
    private LocalDate issuedDate;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDate getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(LocalDate issuedDate) {
        this.issuedDate = issuedDate;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Notice.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("tenant=" + (tenant != null ? tenant.getId() : null))
                .add("type='" + type + "'")
                .add("message='" + message + "'")
                .add("issuedDate=" + issuedDate)
                .add("effectiveDate=" + effectiveDate)
                .toString();
    }
}
