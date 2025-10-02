package com.pgms.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public final class PaymentDtos {

    public static final class CreateOrderRequest {
        @NotNull public UUID tenantId;
        @NotNull @Min(1) public BigDecimal amount;  // in rupees
        public String currency = "INR";
        public String receipt;                      // optional
        public Map<String, String> notes;          // optional

        public UUID getTenantId() {
            return tenantId;
        }

        public void setTenantId(UUID tenantId) {
            this.tenantId = tenantId;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getReceipt() {
            return receipt;
        }

        public void setReceipt(String receipt) {
            this.receipt = receipt;
        }

        public Map<String, String> getNotes() {
            return notes;
        }

        public void setNotes(Map<String, String> notes) {
            this.notes = notes;
        }
    }

    public static final class CreateOrderResponse {
        public String orderId;
        public String keyId;  // publishable key for Checkout
        public String currency;
        public int amountInPaise;
        public String receipt;

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getKeyId() {
            return keyId;
        }

        public void setKeyId(String keyId) {
            this.keyId = keyId;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public int getAmountInPaise() {
            return amountInPaise;
        }

        public void setAmountInPaise(int amountInPaise) {
            this.amountInPaise = amountInPaise;
        }

        public String getReceipt() {
            return receipt;
        }

        public void setReceipt(String receipt) {
            this.receipt = receipt;
        }
    }

    public static final class VerifyRequest {
        @NotNull public String razorpayOrderId;
        @NotNull public String razorpayPaymentId;
        @NotNull public String razorpaySignature;

        public String getRazorpayOrderId() {
            return razorpayOrderId;
        }

        public void setRazorpayOrderId(String razorpayOrderId) {
            this.razorpayOrderId = razorpayOrderId;
        }

        public String getRazorpayPaymentId() {
            return razorpayPaymentId;
        }

        public void setRazorpayPaymentId(String razorpayPaymentId) {
            this.razorpayPaymentId = razorpayPaymentId;
        }

        public String getRazorpaySignature() {
            return razorpaySignature;
        }

        public void setRazorpaySignature(String razorpaySignature) {
            this.razorpaySignature = razorpaySignature;
        }
    }
}
