package com.pgms.api;

import com.pgms.domain.FileRecord;
import com.pgms.dto.FileDtos;
import com.pgms.service.FileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Endpoints to:
 * 1) Get a presigned PUT (client uploads directly to S3)
 * 2) Finalize and store metadata
 * 3) Presign a GET for viewing private files
 * 4) Delete a file
 * <p>
 * NOTE: Secure these with your existing auth (role/org checks).
 */
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    /**
     * Step 1: Client asks for presigned PUT to upload.
     */
    @PostMapping("/presign-upload")
    public ResponseEntity<FileDtos.PresignUploadResponse> presignUpload(@Valid @RequestBody FileDtos.PresignUploadRequest req) {
        var out = fileService.presignUpload(req);
        return ResponseEntity.ok(out);
    }

    /**
     * Step 2: Client calls finalize after successful PUT to S3.
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> finalizeUpload(@Valid @RequestBody FileDtos.FinalizeRequest req) {
        FileRecord saved = fileService.finalizeUpload(req);
        return ResponseEntity.created(URI.create("/api/v1/files/" + saved.getId()))
                .body(Map.of(
                        "id", saved.getId(),
                        "orgId", saved.getOrgId(),
                        "tenantId", saved.getTenantId(),
                        "category", saved.getCategory(),
                        "visibility", saved.getVisibility(),
                        "storageKey", saved.getStorageKey(),
                        "mimeType", saved.getMimeType(),
                        "sizeBytes", saved.getSizeBytes(),
                        "createdAt", saved.getCreatedAt()
                ));
    }

    /**
     * Secure view: short-lived URL for private files.
     */
    @GetMapping("/{fileId}/presign")
    public ResponseEntity<Map<String, Object>> presignDownload(@PathVariable UUID fileId,
                                                               @RequestParam UUID orgId,
                                                               @RequestParam(defaultValue = "300") int ttlSeconds) {
        var resp = fileService.presignDownload(fileId, orgId, ttlSeconds);
        return ResponseEntity.ok(Map.of(
                "url", resp.url(),
                "expiresAt", resp.expiresAt(),
                "mimeType", resp.mimeType()
        ));
    }

    /**
     * Hard delete (consider policy/soft-delete in real life).
     */
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> delete(@PathVariable UUID fileId, @RequestParam UUID orgId) {
        fileService.delete(fileId, orgId);
        return ResponseEntity.noContent().build();
    }
}
