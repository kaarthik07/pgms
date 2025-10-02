package com.pgms.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "ix_users_email", columnList = "email", unique = true),
    @Index(name = "ix_users_phone", columnList = "phone", unique = true)
})
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 120, unique = true)
    private String email;

    @Column(length = 20, unique = true)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.TENANT;

    /** Stored as: pbkdf2$iterations$saltBase64$hashBase64 */
    @Column(name = "password_hash", nullable = false, length = 400)
    private String passwordHash;

    /** Base32 secret if TOTP enabled; null otherwise */
    @Column(name = "totp_secret", length = 64)
    private String totpSecret;

    @Column(nullable = false, length = 16)
    private String status = "ACTIVE"; // ACTIVE / LOCKED (reserved)

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    // --- getters/setters ---

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email == null ? null : email.toLowerCase().trim(); }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getTotpSecret() { return totpSecret; }
    public void setTotpSecret(String totpSecret) { this.totpSecret = totpSecret; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
