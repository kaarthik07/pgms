package com.pgms.repo;

import com.pgms.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NoticeRepo extends JpaRepository<Notice, UUID> {
    List<Notice> findByTenant_Id(UUID tenantId);
}
