package com.pgms.service;

import com.pgms.domain.Due;
import com.pgms.domain.Organization;
import com.pgms.domain.Tenant;
import com.pgms.dto.DueDtos;
import com.pgms.exception.BadRequestException;
import com.pgms.exception.NotFoundException;
import com.pgms.repo.DueRepo;
import com.pgms.repo.OrganizationRepo;
import com.pgms.repo.TenantRepo;
import com.pgms.service.spec.DueSpecs;
import com.pgms.util.Enums;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DueService {

    private static final Logger log = LoggerFactory.getLogger(DueService.class);

    private final DueRepo dues;
    private final OrganizationRepo orgs;
    private final TenantRepo tenants;

    public DueService(DueRepo dues, OrganizationRepo orgs, TenantRepo tenants) {
        this.dues = dues;
        this.orgs = orgs;
        this.tenants = tenants;
    }

    // --- Writer APIs ---

    @Transactional
    public UUID create(DueDtos.Create req) {
        Organization org = orgs.findById(req.orgId)
            .orElseThrow(() -> new NotFoundException("Org not found: " + req.orgId));

        Due due = new Due();
        due.setOrg(org);
        if (req.tenantId != null) {
            Tenant t = tenants.findById(req.tenantId)
                .orElseThrow(() -> new NotFoundException("Tenant not found: " + req.tenantId));
            due.setTenant(t);
        }
        due.setTenantName(req.tenantName);
        due.setTenantPhone(req.tenantPhone);
        due.setTenantGovId(req.tenantGovId);
        due.setAmount(req.amount);
        due.setReason(req.reason);
        due.setFromDate(req.fromDate);
        due.setToDate(req.toDate);
        due.setStatus(Enums.DueStatus.OPEN);

        dues.save(due);
        log.info("Created due {}", due);
        return due.getId();
    }

    @Transactional
    public void clear(UUID id, String notes) {
        Due d = dues.findById(id).orElseThrow(() -> new NotFoundException("Due not found"));
        if (d.getStatus() == Enums.DueStatus.CLEARED) {
            throw new BadRequestException("Already cleared");
        }
        d.setStatus(Enums.DueStatus.CLEARED);
        d.setClearedAt(OffsetDateTime.now());
        d.setClearedNotes(notes);
        dues.save(d);
        log.info("Cleared due {}", d.getId());
    }

    @Transactional
    public void dispute(UUID id, String notes) {
        Due d = dues.findById(id).orElseThrow(() -> new NotFoundException("Due not found"));
        if (d.getStatus() == Enums.DueStatus.CLEARED) {
            throw new BadRequestException("Cannot dispute a cleared due");
        }
        d.setStatus(Enums.DueStatus.DISPUTED);
        d.setDisputeNotes(notes);
        dues.save(d);
        log.info("Disputed due {}", d.getId());
    }

    // --- Reader APIs ---

    public Page<DueDtos.Summary> search(DueDtos.SearchParams p) {
        Pageable pageable = PageRequest.of(Math.max(0, p.page), Math.min(200, p.size),
            Sort.by(Sort.Direction.DESC, "createdAt"));

        Specification<Due> spec = Specification
            .where(DueSpecs.phoneOrNameLike(p.phoneOrName))
            .and(DueSpecs.withStatus(p.status));

        return dues.findAll(spec, pageable)
            .map(this::toSummary);
    }

    public DueDtos.Detail get(UUID id) {
        Due d = dues.findById(id).orElseThrow(() -> new NotFoundException("Due not found"));
        return toDetail(d);
    }

    public boolean hasOpenDues(String phone, String govId) {
        List<Due> hits = dues.findOpenByIdentifiers(phone, govId);
        return !hits.isEmpty();
    }

    // --- mappers ---
    private DueDtos.Summary toSummary(Due d) {
        DueDtos.Summary s = new DueDtos.Summary();
        s.id = d.getId();
        s.tenantName = d.getTenantName();
        s.tenantPhone = d.getTenantPhone();
        s.reason = d.getReason();
        s.amount = d.getAmount();
        s.status = d.getStatus();
        return s;
    }

    private DueDtos.Detail toDetail(Due d) {
        DueDtos.Detail x = new DueDtos.Detail();
        x.id = d.getId();
        x.tenantName = d.getTenantName();
        x.tenantPhone = d.getTenantPhone();
        x.tenantGovId = d.getTenantGovId();
        x.reason = d.getReason();
        x.amount = d.getAmount();
        x.status = d.getStatus();
        x.fromDate = d.getFromDate();
        x.toDate = d.getToDate();
        x.clearedNotes = d.getClearedNotes();
        x.disputeNotes = d.getDisputeNotes();
        return x;
    }
}
