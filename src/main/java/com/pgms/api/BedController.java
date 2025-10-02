package com.pgms.api;

import com.pgms.dto.BedDtos;
import com.pgms.service.BedService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/beds")
public class BedController {

    private final BedService svc;

    public BedController(BedService svc) {
        this.svc = svc;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BedDtos.Response> get(@PathVariable UUID id) {
        return ResponseEntity.ok(svc.get(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable UUID id, @RequestBody @Valid BedDtos.UpdateStatusRequest req) {
        svc.updateStatus(id, req);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody BedDtos.UpdateRequest req) {
        svc.update(id, req);
        return ResponseEntity.noContent().build();
    }
}
