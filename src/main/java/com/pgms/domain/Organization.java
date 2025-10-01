package com.pgms.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.StringJoiner;
import java.util.UUID;

@Entity
@Table(
    name = "organizations",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_org_slug", columnNames = {"slug"}),
        @UniqueConstraint(name = "uk_org_code", columnNames = {"code"})
    }
)
public class Organization {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 120)
    private String name;

    /** Short, URL-safe id used for subdomain / QR / runtime branding */
    @Column(nullable = false, length = 60)
    private String slug;

    /** Public alphanumeric code that owners can share (used to “resolve” org quickly) */
    @Column(nullable = false, length = 32)
    private String code;

    @Column(name = "logo_url", length = 512)
    private String logoUrl;

    @Column(name = "primary_color", length = 16)
    private String primaryColor;

    @Column(name = "secondary_color", length = 16)
    private String secondaryColor;

    @Column(name = "address_line1", length = 160)
    private String addressLine1;

    @Column(name = "address_line2", length = 160)
    private String addressLine2;

    @Column(length = 80)
    private String city;

    @Column(length = 80)
    private String state;

    @Column(length = 16)
    private String pincode;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "whatsapp_number", length = 20)
    private String whatsappNumber;

    /** Referral incentive in paise/cents */
    @Column(name = "referral_bonus_cents")
    private Integer referralBonusCents;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    // Getters & Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public String getPrimaryColor() { return primaryColor; }
    public void setPrimaryColor(String primaryColor) { this.primaryColor = primaryColor; }

    public String getSecondaryColor() { return secondaryColor; }
    public void setSecondaryColor(String secondaryColor) { this.secondaryColor = secondaryColor; }

    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getWhatsappNumber() { return whatsappNumber; }
    public void setWhatsappNumber(String whatsappNumber) { this.whatsappNumber = whatsappNumber; }

    public Integer getReferralBonusCents() { return referralBonusCents; }
    public void setReferralBonusCents(Integer referralBonusCents) { this.referralBonusCents = referralBonusCents; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return new StringJoiner(", ", Organization.class.getSimpleName() + "[", "]")
            .add("id=" + id)
            .add("name='" + name + "'")
            .add("slug='" + slug + "'")
            .add("code='" + code + "'")
            .add("logoUrl='" + logoUrl + "'")
            .add("primaryColor='" + primaryColor + "'")
            .add("secondaryColor='" + secondaryColor + "'")
            .add("city='" + city + "'")
            .add("state='" + state + "'")
            .add("pincode='" + pincode + "'")
            .toString();
    }
}
