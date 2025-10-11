package com.pgms.api;

import com.pgms.dto.BedDtos;
import com.pgms.dto.RoomDtos;
import com.pgms.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {

    private final RoomService svc;

    public RoomController(RoomService svc) {
        this.svc = svc;
    }

    @PostMapping
    public ResponseEntity<UUID> create(@RequestBody @Valid RoomDtos.CreateRequest req) {
        return ResponseEntity.ok(svc.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody @Valid RoomDtos.UpdateRequest req) {
        req.id = id;
        svc.update(req);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        svc.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDtos.Response> get(@PathVariable UUID id) {
        return ResponseEntity.ok(svc.get(id));
    }

    @GetMapping("/{id}/beds")
    public ResponseEntity<List<BedDtos.Response>> listBeds(@PathVariable UUID id) {
        return ResponseEntity.ok(svc.listBeds(id));
    }
}
