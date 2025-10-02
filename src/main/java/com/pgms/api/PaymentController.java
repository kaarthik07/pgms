package com.pgms.api;

import com.pgms.dto.PaymentDtos;
import com.pgms.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService svc;

    public PaymentController(PaymentService svc) {
        this.svc = svc;
    }

    /**
     * Step 1: Backend creates Razorpay Order, frontend gets {orderId, keyId, amountInPaise}
     */
    @PostMapping("/orders")
    public ResponseEntity<PaymentDtos.CreateOrderResponse> create(@Valid @RequestBody PaymentDtos.CreateOrderRequest req) throws Exception {
        return ResponseEntity.ok(svc.createOrder(req));
    }

    /**
     * Step 2: Frontend posts the callback data to backend for verification
     */
    @PostMapping("/verify")
    public ResponseEntity<Void> verify(@Valid @RequestBody PaymentDtos.VerifyRequest req) throws Exception {
        svc.verifyAndFinalize(req);
        return ResponseEntity.noContent().build();
    }
}
