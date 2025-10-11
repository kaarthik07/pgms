package com.pgms.domain;

import com.pgms.util.Enums;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.StringJoiner;
import java.util.UUID;

/**
 * Metadata for a stored object; actual bytes live in S3.
 */
@Entity
@Table(name = "files")
public class FileRecord {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private UUID orgId;

    @Column
    private UUID tenantId;     // nullable; set for tenant documents

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private Enums.FileCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Enums.FileVisibility visibility;

    @Column(nullable = false, length = 512)
    private String storageKey; // e.g., org/{orgId}/tenants/{tenantId}/idproofs/{uuid}.pdf

    @Column(nullable = false, length = 255)
    private String originalName;

    @Column(nullable = false, length = 128)
    private String mimeType;

    @Column(nullable = false)
    private long sizeBytes;

    @Column(nullable = false)
    private Instant createdAt;

    @Column
    private Instant deletedAt;

    public FileRecord() {
    }

    public static FileRecord create(UUID orgId,
                                    UUID tenantId,
                                    Enums.FileCategory category,
                                    Enums.FileVisibility visibility,
                                    String storageKey,
                                    String originalName,
                                    String mimeType,
                                    long sizeBytes) {
        FileRecord f = new FileRecord();
        f.id = UUID.randomUUID();
        f.orgId = orgId;
        f.tenantId = tenantId;
        f.category = category;
        f.visibility = visibility;
        f.storageKey = storageKey;
        f.originalName = originalName;
        f.mimeType = mimeType;
        f.sizeBytes = sizeBytes;
        f.createdAt = Instant.now();
        return f;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrgId() {
        return orgId;
    }

    public void setOrgId(UUID orgId) {
        this.orgId = orgId;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public Enums.FileCategory getCategory() {
        return category;
    }

    public void setCategory(Enums.FileCategory category) {
        this.category = category;
    }

    public Enums.FileVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Enums.FileVisibility visibility) {
        this.visibility = visibility;
    }

    public String getStorageKey() {
        return storageKey;
    }

    public void setStorageKey(String storageKey) {
        this.storageKey = storageKey;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", FileRecord.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("orgId=" + orgId)
                .add("tenantId=" + tenantId)
                .add("category=" + category)
                .add("visibility=" + visibility)
                .add("storageKey='" + storageKey + "'")
                .add("originalName='" + originalName + "'")
                .add("mimeType='" + mimeType + "'")
                .add("sizeBytes=" + sizeBytes)
                .add("createdAt=" + createdAt)
                .add("deletedAt=" + deletedAt)
                .toString();
    }
}
