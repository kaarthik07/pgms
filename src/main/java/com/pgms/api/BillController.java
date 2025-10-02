package com.pgms.api;

import com.pgms.dto.BillDtos;
import com.pgms.dto.ReceiptDtos;
import com.pgms.service.BillingService;
import com.pgms.util.Enums;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bills")
public class BillController {

    private static final Logger log = LoggerFactory.getLogger(BillController.class);
    private final BillingService svc;

    public BillController(BillingService svc) {
        this.svc = svc;
    }

    // Create bill
    @PostMapping
    public ResponseEntity<UUID> create(@RequestBody @Valid BillDtos.CreateRequest req) {
        log.debug("Create bill {}", req);
        return ResponseEntity.ok(svc.createBill(req));
    }

    // Search bills
    @GetMapping
    public ResponseEntity<BillDtos.PageResponse<BillDtos.Summary>> search(
            @RequestParam(required = false) UUID tenantId,
            @RequestParam(required = false) Enums.BillStatus status,
            @RequestParam(required = false) LocalDate startFrom,
            @RequestParam(required = false) LocalDate startTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(svc.search(tenantId, status, startFrom, startTo, page, size));
    }

    // Get one bill
    @GetMapping("/{id}")
    public ResponseEntity<BillDtos.Summary> get(@PathVariable UUID id) {
        return ResponseEntity.ok(svc.get(id));
    }

    // Pay bill -> creates receipt
    @PostMapping("/{id}/pay")
    public ResponseEntity<ReceiptDtos.Summary> pay(@PathVariable UUID id,
                                                   @RequestBody @Valid BillDtos.PaymentRequest req) {
        req.billId = id; // enforce path id
        return ResponseEntity.ok(svc.pay(req));
    }
}
