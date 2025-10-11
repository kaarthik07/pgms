package com.pgms.repo;

import com.pgms.domain.Organization;
import com.pgms.domain.Receipt;
import com.pgms.domain.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface ReceiptRepo extends JpaRepository<Receipt, UUID>, JpaSpecificationExecutor<Receipt> {
    List<Receipt> findByTenant(Tenant tenant);

    List<Receipt> findByOrg(Organization org);
}
