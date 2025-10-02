package com.pgms.service;

import com.pgms.domain.Settlement;
import com.pgms.repo.SettlementRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SettlementService {
    private final SettlementRepo settlementRepo;

    public SettlementService(SettlementRepo settlementRepo) {
        this.settlementRepo = settlementRepo;
    }

    public Settlement create(Settlement settlement) {
        return settlementRepo.save(settlement);
    }

    @Transactional(readOnly = true)
    public List<Settlement> listByTenant(UUID tenantId) {
        return settlementRepo.findByTenant_Id(tenantId);
    }
}
