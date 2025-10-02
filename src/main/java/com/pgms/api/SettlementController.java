package com.pgms.api;

import com.pgms.domain.Settlement;
import com.pgms.service.SettlementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/settlements")
public class SettlementController {
    private final SettlementService settlementService;

    public SettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @PostMapping
    public ResponseEntity<Settlement> create(@RequestBody Settlement settlement) {
        return ResponseEntity.ok(settlementService.create(settlement));
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<Settlement>> listByTenant(@PathVariable UUID tenantId) {
        return ResponseEntity.ok(settlementService.listByTenant(tenantId));
    }
}
