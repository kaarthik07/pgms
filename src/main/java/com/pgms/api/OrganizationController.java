package com.pgms.api;

import com.pgms.dto.OrgDtos;
import com.pgms.service.OrganizationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Organization CRUD + branding endpoints.
 * Note: Slug & Code are immutable after creation in v1 (avoids QR/subdomain drift).
 */
@RestController
@RequestMapping("/api/v1/orgs")
public class OrganizationController {

    private static final Logger log = LoggerFactory.getLogger(OrganizationController.class);

    private final OrganizationService svc;

    public OrganizationController(OrganizationService svc) {
        this.svc = svc;
    }

    // --- CRUD ---

    @PostMapping
    public ResponseEntity<UUID> create(@Valid @RequestBody OrgDtos.CreateRequest req) {
        log.debug("Create org {}", req);
        return ResponseEntity.ok(svc.create(req));
    }

    @PutMapping
    public ResponseEntity<Void> update(@Valid @RequestBody OrgDtos.UpdateRequest req) {
        log.debug("Update org {}", req);
        svc.update(req);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.debug("Delete org {}", id);
        svc.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrgDtos.Response> get(@PathVariable UUID id) {
        return ResponseEntity.ok(svc.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<OrgDtos.Response>> list(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name,asc") String sort) {

        Sort s = Sort.by(sort.split(",")[0])
                .ascending();
        if (sort.endsWith(",desc")) {
            s = s.descending();
        }

        Pageable pageable = PageRequest.of(page, size, s);
        return ResponseEntity.ok(svc.search(q, pageable));
    }

    // --- Branding & code resolve ---

    /**
     * Used by tenant app to theme UI at runtime from org slug
     */
    @GetMapping("/{slug}/branding")
    public ResponseEntity<OrgDtos.Branding> branding(@PathVariable String slug) {
        return ResponseEntity.ok(svc.brandingBySlug(slug));
    }

    /**
     * Used by QR/code scans to resolve org quickly (returns id/slug/logo etc.)
     */
    @GetMapping("/resolve")
    public ResponseEntity<OrgDtos.Resolve> resolve(@RequestParam String code) {
        return ResponseEntity.ok(svc.resolveByCode(code));
    }
}
