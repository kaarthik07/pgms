package com.pgms.repo;

import com.pgms.domain.FileRecord;
import com.pgms.util.Enums;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FileRecordRepo extends JpaRepository<FileRecord, UUID> {
    Optional<FileRecord> findByIdAndOrgId(UUID id, UUID orgId);

    List<FileRecord> findByTenantIdAndOrgId(UUID tenantId, UUID orgId);

    long countByVisibility(Enums.FileVisibility visibility);
}
