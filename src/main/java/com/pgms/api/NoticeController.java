package com.pgms.api;

import com.pgms.domain.Notice;
import com.pgms.service.NoticeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notices")
public class NoticeController {
    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @PostMapping
    public ResponseEntity<Notice> create(@RequestBody Notice notice) {
        return ResponseEntity.ok(noticeService.create(notice));
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<Notice>> listByTenant(@PathVariable UUID tenantId) {
        return ResponseEntity.ok(noticeService.listByTenant(tenantId));
    }
}
