package com.pgms.repo;

import com.pgms.domain.Bill;
import com.pgms.domain.Organization;
import com.pgms.domain.Tenant;
import com.pgms.util.Enums;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BillRepo extends JpaRepository<Bill, UUID>, JpaSpecificationExecutor<Bill> {
    List<Bill> findByTenantAndPeriodStartLessThanEqualAndPeriodEndGreaterThanEqual(
            Tenant tenant, LocalDate start, LocalDate end);

    List<Bill> findByOrgAndStatus(Organization org, Enums.BillStatus status);
}
