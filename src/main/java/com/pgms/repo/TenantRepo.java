package com.pgms.repo;

import com.pgms.domain.*;
import com.pgms.util.Enums.TenantStatus;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface TenantRepo extends JpaRepository<Tenant, UUID>, JpaSpecificationExecutor<Tenant> {
    Page<Tenant> findByOrgAndStatus(Organization org, TenantStatus status, Pageable pageable);

    Page<Tenant> findByOrg(Organization org, Pageable pageable);
    boolean existsByBed_Id(UUID bedId);
    Optional<Tenant> findByBed_Id(UUID bedId);
}
