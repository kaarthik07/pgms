package com.pgms.api;

import com.pgms.domain.Organization;
import com.pgms.repo.OrganizationRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class HealthController {
    private final OrganizationRepo orgs;

    public HealthController(OrganizationRepo o) {
        this.orgs = o;
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/bootstrap/org/{code}")
    public ResponseEntity<String> bootstrapOrg(@PathVariable String code) {
        Organization o = new Organization();
        o.setCode(code);
        o.setName(code.toUpperCase());
        orgs.save(o);
        return ResponseEntity.ok("Created org " + code);
    }
}
