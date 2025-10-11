package com.pgms.service;

import com.pgms.util.Enums.FileCategory;
import com.pgms.domain.FileRecord;
import com.pgms.dto.FileDtos;
import com.pgms.repo.FileRecordRepo;
import com.pgms.storage.FileStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static com.pgms.util.Enums.FileCategory.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRecordRepo fileRepo;
    private final FileStorage storage;
    private final Tika tika = new Tika();

    /**
     * Build our canonical S3 key layout.
     */
    public String buildKey(UUID orgId, UUID tenantId, FileCategory category, String fileName) {
        String safeName = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        UUID leaf = UUID.randomUUID();
        return switch (category) {
            case ORG_LOGO -> "org/%s/logo/%s_%s".formatted(orgId, leaf, safeName);
            case ORG_QR -> "org/%s/qr/%s_%s".formatted(orgId, leaf, safeName);
            case TENANT_PHOTO -> "org/%s/tenants/%s/photo/%s_%s".formatted(orgId, tenantId, leaf, safeName);
            case TENANT_ID_PROOF -> "org/%s/tenants/%s/idproofs/%s_%s".formatted(orgId, tenantId, leaf, safeName);
            case BILL_PDF -> "bills/%s/%s_%s".formatted(orgId, leaf, safeName);
            case RECEIPT_PDF -> "receipts/%s/%s_%s".formatted(orgId, leaf, safeName);
            default -> "misc/%s/%s_%s".formatted(orgId, leaf, safeName);
        };
    }

    /**
     * Presign a PUT so client can upload directly to S3.
     */
    public FileDtos.PresignUploadResponse presignUpload(FileDtos.PresignUploadRequest req) {
        String key = buildKey(req.orgId(), req.tenantId(), req.category(), req.originalName());
        String mime = normalizeMime(req.mimeType(), req.originalName());
        URL url = storage.presignPut(key, Duration.ofSeconds(req.ttlSeconds()), mime, req.sizeBytes());
        return new FileDtos.PresignUploadResponse(url.toString(), key, Instant.now().plusSeconds(req.ttlSeconds()));
    }

    /**
     * After client uploads, we persist the metadata record.
     */
    @Transactional
    public FileRecord finalizeUpload(FileDtos.FinalizeRequest req) {
        String mime = normalizeMime(req.mimeType(), req.originalName());
        FileRecord fr = FileRecord.create(
                req.orgId(), req.tenantId(), req.category(), req.visibility(),
                req.storageKey(), req.originalName(), mime, req.sizeBytes()
        );
        FileRecord saved = fileRepo.save(fr);
        log.info("Finalized file: {}", saved);
        return saved;
    }

    /**
     * Presign a GET for secure, short-lived viewing.
     */
    public FileDtos.PresignDownloadResponse presignDownload(UUID fileId, UUID orgId, int ttlSeconds) {
        FileRecord f = fileRepo.findByIdAndOrgId(fileId, orgId)
                .orElseThrow(() -> new IllegalArgumentException("File not found or not in org"));
        URL url = storage.presignGet(f.getStorageKey(), Duration.ofSeconds(ttlSeconds));
        return new FileDtos.PresignDownloadResponse(url.toString(), Instant.now().plusSeconds(ttlSeconds), f.getMimeType());
    }

    /**
     * Hard delete from S3 + metadata (use with caution; consider soft delete by policy).
     */
    @Transactional
    public void delete(UUID fileId, UUID orgId) {
        FileRecord f = fileRepo.findByIdAndOrgId(fileId, orgId)
                .orElseThrow(() -> new IllegalArgumentException("File not found or not in org"));
        storage.delete(f.getStorageKey());
        fileRepo.delete(f);
        log.info("Deleted file {}", fileId);
    }

    /**
     * Guess MIME if missing/incorrect.
     */
    private String normalizeMime(String mime, String name) {
        try {
            if (mime == null || mime.isBlank() || "application/octet-stream".equalsIgnoreCase(mime)) {
                String detected = tika.detect(name);
                return detected != null ? detected : "application/octet-stream";
            }
            return mime;
        } catch (Exception e) {
            return "application/octet-stream";
        }
    }
}
