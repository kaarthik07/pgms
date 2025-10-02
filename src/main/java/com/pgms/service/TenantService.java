package com.pgms.service;

import com.pgms.domain.*;
import com.pgms.dto.TenantDtos.*;
import com.pgms.exception.BadRequestException;
import com.pgms.exception.NotFoundException;
import com.pgms.repo.*;
import com.pgms.util.Enums;
import com.pgms.util.Enums.TenantStatus;
import org.slf4j.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class TenantService {
    private static final Logger log = LoggerFactory.getLogger(TenantService.class);
    private final TenantRepo tenants;
    private final OrganizationRepo orgs;
    private final RoomRepo rooms;
    private final BedRepo beds;

    public TenantService(TenantRepo t, OrganizationRepo o, RoomRepo r, BedRepo b) {
        tenants = t;
        orgs = o;
        rooms = r;
        beds = b;
    }

    @Transactional
    public UUID create(CreateRequest req) {
        Organization org = orgs.findByCode(req.orgCode).orElseThrow(() -> new NotFoundException("Org not found: " + req.orgCode));
        Tenant t = new Tenant();
        t.setOrg(org);
        t.setFullName(req.fullName);
        t.setPhone(req.phone);
        t.setEmail(req.email);
        t.setDateOfBirth(req.dateOfBirth);
        t.setFatherName(req.fatherName);
        t.setFatherPhone(req.fatherPhone);
        t.setVehicleNumber(req.vehicleNumber);
        if(req.roomId != null){
            Room room = rooms.findById(req.roomId).orElseThrow(() -> new NotFoundException("Room not found"));
            t.setRoom(room);
            if(req.bedIndex != null){
                Bed bed = beds.findByRoom_IdAndIndex(room.getId(), req.bedIndex)
                        .orElseThrow(() -> new NotFoundException("Bed index not found in room"));
                ensureBedAvailable(bed);
                occupyBed(bed);
                t.setBed(bed);
            }
        }
        tenants.save(t);
        log.info("Created tenant {}", t);
        return t.getId();
    }

    @Transactional
    public void update(UUID id, UpdateRequest req) {
        Tenant t = tenants.findById(id).orElseThrow(() -> new NotFoundException("Tenant not found: " + req.id));
        t.setFullName(req.fullName);
        t.setPhone(req.phone);
        t.setEmail(req.email);
        t.setDateOfBirth(req.dateOfBirth);
        t.setFatherName(req.fatherName);
        t.setFatherPhone(req.fatherPhone);
        t.setVehicleNumber(req.vehicleNumber);
        if (req.status != null) t.setStatus(req.status);
        if (req.roomId != null)
            t.setRoom(rooms.findById(req.roomId).orElseThrow(() -> new NotFoundException("Room not found")));
        if (req.bedId != null)
            t.setBed(beds.findById(req.bedId).orElseThrow(() -> new NotFoundException("Bed not found")));
        log.info("Updated tenant {}", t);
    }

    @Transactional
    public void delete(UUID id) {
        Tenant t = tenants.findById(id).orElseThrow(() -> new NotFoundException("Tenant not found: " + id));
        tenants.delete(t);
        log.info("Deleted tenant {}", id);
    }

    public Page<Tenant> list(String orgCode, TenantStatus status, int page, int size) {
        Organization org = orgs.findByCode(orgCode).orElseThrow(() -> new NotFoundException("Org not found: " + orgCode));
        if (status == null) return tenants.findByOrg(org, PageRequest.of(page, size));
        return tenants.findByOrgAndStatus(org, status, PageRequest.of(page, size));
    }

    public Tenant get(UUID id) {
        return tenants.findById(id).orElseThrow(() -> new NotFoundException("Tenant not found: " + id));
    }

    /** Throws 400 if bed is not available to allocate. */
    private void ensureBedAvailable(Bed bed) {
        if (bed == null) {
            throw new BadRequestException("Bed is required");
        }
        if (bed.getStatus() == Enums.BedStatus.MAINTENANCE) {
            throw new BadRequestException("Bed is under maintenance");
        }
        if (bed.getStatus() == Enums.BedStatus.OCCUPIED) {
            throw new BadRequestException("Bed is already occupied");
        }
        // OK if AVAILABLE
    }

    /** Marks the bed as occupied and persists it. */
    private void occupyBed(Bed bed) {
        bed.setStatus(Enums.BedStatus.OCCUPIED);
        bed.setOccupiedAt(OffsetDateTime.now());
        beds.save(bed);
    }

    /** Optional: free a previously assigned bed (use in update/delete flows). */
    private void freeBed(Bed bed) {
        if (bed == null) return;
        bed.setStatus(Enums.BedStatus.AVAILABLE);
        bed.setOccupiedAt(null);
        beds.save(bed);
    }
}
