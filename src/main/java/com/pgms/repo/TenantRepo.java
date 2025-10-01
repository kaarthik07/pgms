package com.pgms.repo;

import com.pgms.domain.*;
import com.pgms.util.Enums.TenantStatus;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TenantRepo extends JpaRepository<Tenant, UUID> {
    Page<Tenant> findByOrgAndStatus(Organization org, TenantStatus status, Pageable pageable);

    Page<Tenant> findByOrg(Organization org, Pageable pageable);
}
