package com.pgms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class PaymentService {
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final String razorpayKeyId = System.getenv().getOrDefault("RAZORPAY_KEY_ID", "rzp_test_key"); 
    private final String razorpaySecret = System.getenv().getOrDefault("RAZORPAY_KEY_SECRET", "secret");

    public Map<String, Object> createOrder(long amountPaise, String currency, String receipt) {
        String fakeOrderId = "order_" + System.currentTimeMillis();
        log.info("[Razorpay] Create order amount={}, currency={}, receipt={} -> {}", amountPaise, currency, receipt, fakeOrderId);
        return Map.of("id", fakeOrderId, "amount", amountPaise, "currency", currency, "receipt", receipt, "key", razorpayKeyId);
    }

    public boolean verifySignature(String orderId, String paymentId, String signatureHex) {
        try {
            String payload = orderId + "|" + paymentId;
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(razorpaySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] hash = sha256_HMAC.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String expected = bytesToHex(hash);
            return expected.equalsIgnoreCase(signatureHex);
        } catch (Exception e) {
            log.error("Signature verify failed", e);
            return false;
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
