package com.pgms.api;

import com.pgms.dto.ReceiptDtos;
import com.pgms.repo.ReceiptRepo;
import com.pgms.service.BillingService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/receipts")
public class ReceiptController {

    private final ReceiptRepo receipts;
    private final BillingService billingSvc; // used only if you later add filters/specs

    public ReceiptController(ReceiptRepo receipts, BillingService billingSvc) {
        this.receipts = receipts;
        this.billingSvc = billingSvc;
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        var pr = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "paidAt"));
        var res = receipts.findAll(pr).map(r -> {
            var s = new ReceiptDtos.Summary();
            s.id = r.getId();
            s.billId = r.getBill().getId();
            s.tenantId = r.getTenant().getId();
            s.amountPaid = r.getAmountPaid();
            s.mode = r.getPaymentMode();
            s.txnRef = r.getTxnRef();
            s.paidAt = r.getPaidAt();
            s.createdAt = r.getCreatedAt();
            return s;
        });
        return ResponseEntity.ok(
                new com.pgms.dto.BillDtos.PageResponse<>(
                        res.getContent(), res.getTotalElements(), res.getTotalPages()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReceiptDtos.Summary> get(@PathVariable UUID id) {
        var r = receipts.findById(id).orElseThrow(() -> new com.pgms.exception.NotFoundException("Receipt not found"));
        var s = new ReceiptDtos.Summary();
        s.id = r.getId();
        s.billId = r.getBill().getId();
        s.tenantId = r.getTenant().getId();
        s.amountPaid = r.getAmountPaid();
        s.mode = r.getPaymentMode();
        s.txnRef = r.getTxnRef();
        s.paidAt = r.getPaidAt();
        s.createdAt = r.getCreatedAt();
        return ResponseEntity.ok(s);
    }
}
