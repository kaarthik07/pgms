package com.pgms.repo;

import com.pgms.domain.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SettlementRepo extends JpaRepository<Settlement, UUID> {
    List<Settlement> findByTenant_Id(UUID tenantId);
}
