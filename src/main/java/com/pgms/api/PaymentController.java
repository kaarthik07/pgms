package com.pgms.api;

import com.pgms.service.PaymentService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentService paymentService;
    public PaymentController(PaymentService s){ this.paymentService=s; }

    @PostMapping("/orders")
    public ResponseEntity<Map<String,Object>> createOrder(@RequestParam @Min(1) long amountPaise,
                                                          @RequestParam(defaultValue = "INR") String currency,
                                                          @RequestParam String receipt){
        return ResponseEntity.ok(paymentService.createOrder(amountPaise, currency, receipt));
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String,Object>> verify(@RequestParam @NotBlank String orderId,
                                                     @RequestParam @NotBlank String paymentId,
                                                     @RequestParam @NotBlank String signature){
        boolean ok = paymentService.verifySignature(orderId, paymentId, signature);
        return ResponseEntity.ok(Map.of("verified", ok));
    }
}
