package com.pgms.service;

import com.pgms.domain.Bed;
import com.pgms.dto.BedDtos;
import com.pgms.exception.BadRequestException;
import com.pgms.exception.NotFoundException;
import com.pgms.repo.BedRepo;
import com.pgms.repo.TenantRepo;
import com.pgms.util.Enums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class BedService {

    private static final Logger log = LoggerFactory.getLogger(BedService.class);

    private final BedRepo bedRepo;
    private final TenantRepo tenantRepo;

    public BedService(BedRepo bedRepo, TenantRepo tenantRepo) {
        this.bedRepo = bedRepo;
        this.tenantRepo = tenantRepo;
    }

    @Transactional(readOnly = true)
    public BedDtos.Response get(UUID bedId) {
        Bed b = bedRepo.findById(bedId).orElseThrow(() -> new NotFoundException("Bed not found"));
        BedDtos.Response res = new BedDtos.Response();
        res.id = b.getId();
        res.roomId = b.getRoom().getId();
        res.index = b.getIndex();
        res.status = b.getStatus();
        res.code = b.getCode();
        res.priceOverride = b.getPriceOverride();
        return res;
    }

    @Transactional
    public void updateStatus(UUID bedId, BedDtos.UpdateStatusRequest req) {
        Bed b = bedRepo.findById(bedId).orElseThrow(() -> new NotFoundException("Bed not found"));
        if (req.status == Enums.BedStatus.AVAILABLE && tenantRepo.existsByBed_Id(bedId)) {
            throw new BadRequestException("Cannot mark AVAILABLE while tenant assigned.");
        }
        b.setStatus(req.status);
        bedRepo.save(b);
        log.info("Updated bed {} status -> {}", bedId, req.status);
    }

    @Transactional
    public void update(UUID bedId, BedDtos.UpdateRequest req) {
        Bed b = bedRepo.findById(bedId).orElseThrow(() -> new NotFoundException("Bed not found"));
        if (req.code != null && !req.code.equals(b.getCode())) {
            if (bedRepo.existsByCode(req.code)) throw new BadRequestException("Bed code already exists");
            b.setCode(req.code);
        }
        if (req.priceOverride != null) {
            b.setPriceOverride(req.priceOverride);
        }
        bedRepo.save(b);
        log.info("Updated bed {}", bedId);
    }
}
