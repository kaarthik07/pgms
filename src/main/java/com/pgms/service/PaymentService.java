package com.pgms.service;

import com.pgms.domain.Payment;
import com.pgms.domain.Tenant;
import com.pgms.dto.PaymentDtos;
import com.pgms.exception.NotFoundException;
import com.pgms.repo.PaymentRepo;
import com.pgms.repo.TenantRepo;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    private final RazorpayClient razorpay;
    private final PaymentRepo payments;
    private final TenantRepo tenants;
    private final String keyId;
    private final String keySecret;

    public PaymentService(RazorpayClient razorpay,
                          PaymentRepo payments,
                          TenantRepo tenants,
                          @Value("${payments.razorpay.keyId}") String keyId,
                          @Value("${payments.razorpay.keySecret}") String keySecret) {
        this.razorpay = razorpay;
        this.payments = payments;
        this.tenants = tenants;
        this.keyId = keyId;
        this.keySecret = keySecret;
    }

    @Transactional
    public PaymentDtos.CreateOrderResponse createOrder(PaymentDtos.CreateOrderRequest req) throws Exception {
        Tenant tenant = tenants.findById(req.tenantId)
                .orElseThrow(() -> new NotFoundException("Tenant not found"));

        int amountInPaise = req.amount.multiply(new BigDecimal("100")).intValueExact();

        JSONObject options = new JSONObject();
        options.put("amount", amountInPaise);
        options.put("currency", req.currency == null ? "INR" : req.currency);
        if (req.receipt != null) options.put("receipt", req.receipt);
        options.put("payment_capture", 1); // auto-capture

        if (req.notes != null && !req.notes.isEmpty()) {
            JSONObject notes = new JSONObject(req.notes);
            options.put("notes", notes);
        }

        Order order = razorpay.orders.create(options);

        Payment p = new Payment();
        p.setTenant(tenant);
        p.setAmount(req.amount);
        p.setCurrency(options.getString("currency"));
        p.setRazorpayOrderId(order.get("id"));
        p.setStatus("CREATED");
        payments.save(p);

        PaymentDtos.CreateOrderResponse res = new PaymentDtos.CreateOrderResponse();
        res.orderId = order.get("id");
        res.keyId = keyId; // frontend needs this for Razorpay Checkout
        res.currency = p.getCurrency();
        res.amountInPaise = amountInPaise;
        res.receipt = req.receipt;
        return res;
    }

    @Transactional
    public void verifyAndFinalize(PaymentDtos.VerifyRequest req) throws Exception {
        // Build JSON for signature verification
        JSONObject attrs = new JSONObject()
                .put("razorpay_order_id", req.getRazorpayOrderId())
                .put("razorpay_payment_id", req.getRazorpayPaymentId())
                .put("razorpay_signature", req.getRazorpaySignature());

        boolean ok = Utils.verifyPaymentSignature(attrs, keySecret); // keySecret from config

        Payment p = payments.findByRazorpayOrderId(req.getRazorpayOrderId())
                .orElseThrow(() -> new NotFoundException("Order not found: " + req.getRazorpayOrderId()));

        if (ok) {
            p.setRazorpayPaymentId(req.getRazorpayPaymentId());
            p.setRazorpaySignature(req.getRazorpaySignature());
            p.setStatus("PAID");
            payments.save(p);
        } else {
            p.setStatus("FAILED");
            payments.save(p);
            throw new IllegalArgumentException("Invalid signature");
        }
    }
}
