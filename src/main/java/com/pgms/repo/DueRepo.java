package com.pgms.repo;

import com.pgms.domain.Due;
import com.pgms.domain.Organization;
import com.pgms.util.Enums;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DueRepo extends JpaRepository<Due, UUID>, JpaSpecificationExecutor<Due> {

    // For cross-PG checks during onboarding
    @Query("""
                select d from Due d
                 where d.status = 'OPEN'
                   and (lower(d.tenantPhone) = lower(:phone)
                        or (coalesce(:govId, '') <> '' and lower(d.tenantGovId) = lower(:govId)))
            """)
    List<Due> findOpenByIdentifiers(String phone, String govId);

    List<Due> findByOrgAndStatus(Organization org, Enums.DueStatus status);

    Optional<Due> findFirstByTenantPhoneAndStatusOrderByCreatedAtDesc(String phone, Enums.DueStatus status);
}
