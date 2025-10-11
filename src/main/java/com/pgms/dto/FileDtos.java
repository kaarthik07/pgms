package com.pgms.dto;

import com.pgms.util.Enums.FileCategory;
import com.pgms.util.Enums.FileVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;
import java.util.UUID;

/**
 * DTOs for file operations.
 */
public final class FileDtos {

    /**
     * Request: Ask for a presigned PUT to upload directly to S3 from client.
     */
    public record PresignUploadRequest(
            @NotNull UUID orgId,
            UUID tenantId,
            @NotNull FileCategory category,
            @NotNull FileVisibility visibility,
            @NotBlank String originalName,
            @Positive long sizeBytes,
            @NotBlank String mimeType,
            @Positive int ttlSeconds
    ) {
    }

    /**
     * Response: Presigned PUT details + the S3 key we expect to receive.
     */
    public record PresignUploadResponse(
            String url,
            String storageKey,
            Instant expiresAt
    ) {
    }

    /**
     * Finalize metadata after client uploaded with PUT to S3.
     */
    public record FinalizeRequest(
            @NotNull UUID orgId,
            UUID tenantId,
            @NotNull FileCategory category,
            @NotNull FileVisibility visibility,
            @NotBlank String originalName,
            @Positive long sizeBytes,
            @NotBlank String mimeType,
            @NotBlank String storageKey
    ) {
    }

    /**
     * Response for presigned GET.
     */
    public record PresignDownloadResponse(
            String url,
            Instant expiresAt,
            String mimeType
    ) {
    }
}
