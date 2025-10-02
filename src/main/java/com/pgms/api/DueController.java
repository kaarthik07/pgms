package com.pgms.api;

import com.pgms.dto.DueDtos;
import com.pgms.service.DueService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dues")
public class DueController {

    private final DueService svc;

    public DueController(DueService svc) {
        this.svc = svc;
    }

    @PostMapping
    public ResponseEntity<UUID> create(@Valid @RequestBody DueDtos.Create req) {
        return ResponseEntity.ok(svc.create(req));
    }

    @GetMapping
    public ResponseEntity<?> search(@Valid DueDtos.SearchParams p) {
        return ResponseEntity.ok(svc.search(p));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable UUID id) {
        return ResponseEntity.ok(svc.get(id));
    }

    @PostMapping("/{id}/clear")
    public ResponseEntity<Void> clear(@PathVariable UUID id,
                                      @Valid @RequestBody DueDtos.ClearRequest req) {
        svc.clear(id, req.notes);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/dispute")
    public ResponseEntity<Void> dispute(@PathVariable UUID id,
                                        @Valid @RequestBody DueDtos.DisputeRequest req) {
        svc.dispute(id, req.notes);
        return ResponseEntity.noContent().build();
    }
}
