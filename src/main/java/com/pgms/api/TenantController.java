package com.pgms.api;

import com.pgms.domain.Tenant;
import com.pgms.dto.TenantDtos.*;
import com.pgms.service.TenantService;
import com.pgms.util.Enums.TenantStatus;
import jakarta.validation.Valid;
import org.slf4j.*;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tenants")
public class TenantController {
    private static final Logger log = LoggerFactory.getLogger(TenantController.class);
    private final TenantService svc;

    public TenantController(TenantService s) {
        this.svc = s;
    }

    @PostMapping
    public ResponseEntity<UUID> create(@RequestBody @Valid CreateRequest req) {
        log.debug("Create tenant {}", req);
        return ResponseEntity.ok(svc.create(req));
    }

    @PutMapping("/{id}")  // <-- add {id} here
    public ResponseEntity<Void> update(
            @PathVariable UUID id,                  // <-- take id from path
            @RequestBody @Valid UpdateRequest req) {
        log.debug("Update tenant id={}, {}", id, req);
        svc.update(id, req);                        // <-- service should accept id + dto
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        svc.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> get(@PathVariable UUID id) {
        Tenant t = svc.get(id);
        Response r = new Response();
        r.id = t.getId();
        r.fullName = t.getFullName();
        r.phone = t.getPhone();
        r.email = t.getEmail();
        r.status = t.getStatus().name();
        r.room = t.getRoom() != null ? t.getRoom().getNumber() : null;
        r.bedIndex = t.getBed() != null ? t.getBed().getIndex() : null;
        return ResponseEntity.ok(r);
    }

    @GetMapping
    public ResponseEntity<Page<Tenant>> list(@RequestParam String orgCode, @RequestParam(required = false) TenantStatus status, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(svc.list(orgCode, status, page, size));
    }
}
