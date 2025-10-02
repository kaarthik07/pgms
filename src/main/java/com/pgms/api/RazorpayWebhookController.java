package com.pgms.api;

import com.pgms.domain.Payment;
import com.pgms.repo.PaymentRepo;
import com.razorpay.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/payments")
public class RazorpayWebhookController {

    private final PaymentRepo payments;
    private final String webhookSecret;

    public RazorpayWebhookController(PaymentRepo payments,
                                     @Value("${payments.razorpay.webhookSecret:}") String webhookSecret) {
        this.payments = payments;
        this.webhookSecret = webhookSecret;
    }

    @PostMapping("/webhook") // configure this URL in Razorpay dashboard
    public ResponseEntity<Void> handle(HttpServletRequest request,
                                       @RequestHeader("X-Razorpay-Signature") String signature) throws Exception {

        String payload = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);

        if (webhookSecret == null || webhookSecret.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        boolean valid = Utils.verifyWebhookSignature(payload, signature, webhookSecret);
        if (!valid) return ResponseEntity.status(401).build();

        JSONObject event = new JSONObject(payload);
        String eventType = event.optString("event");

        if ("payment.captured".equals(eventType)) {
            JSONObject entity = event.getJSONObject("payload").getJSONObject("payment").getJSONObject("entity");
            String orderId = entity.optString("order_id", null);
            String paymentId = entity.optString("id", null);

            if (orderId != null && paymentId != null) {
                Optional<Payment> opt = payments.findByRazorpayOrderId(orderId);
                opt.ifPresent(p -> {
                    p.setRazorpayPaymentId(paymentId);
                    p.setStatus("PAID");
                    payments.save(p);
                });
            }
        }
        // handle other events as needed

        return ResponseEntity.noContent().build();
    }
}
