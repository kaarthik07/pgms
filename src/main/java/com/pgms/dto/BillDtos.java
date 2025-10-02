package com.pgms.dto;

import com.pgms.util.Enums;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class BillDtos {

    // --------- Create Bill ----------
    public static class CreateRequest {
        @NotBlank
        public String orgCode;
        @NotNull
        public UUID tenantId;

        @NotNull
        public LocalDate periodStart;
        @NotNull
        public LocalDate periodEnd;

        @NotNull
        public LocalDate dueDate;

        @NotNull
        @DecimalMin("0.00")
        public BigDecimal rentAmount;

        @NotNull
        @DecimalMin("0.00")
        public BigDecimal utilitiesAmount = BigDecimal.ZERO;

        @NotNull
        @DecimalMin("0.00")
        public BigDecimal discountAmount = BigDecimal.ZERO;

        public String notes;
    }

    // --------- Bill Summary ----------
    public static class Summary {
        public UUID id;
        public UUID tenantId;
        public LocalDate periodStart;
        public LocalDate periodEnd;
        public LocalDate dueDate;
        public Enums.BillStatus status;
        public BigDecimal totalAmount;
        public BigDecimal paidAmount;
        public BigDecimal outstanding;
        public String notes;
    }

    // --------- Payment (mark paid) ----------
    public static class PaymentRequest {
        @NotNull
        public UUID billId;
        @NotNull
        @DecimalMin("0.01")
        public BigDecimal amount;
        @NotNull
        public Enums.PaymentMode mode;
        public String txnRef;
        public OffsetDateTime paidAt; // optional (defaults to now)
    }

    // --------- Page ----------
    public static class PageResponse<T> {
        public List<T> content;
        public long totalElements;
        public int totalPages;

        public PageResponse(List<T> c, long e, int p) {
            this.content = c;
            this.totalElements = e;
            this.totalPages = p;
        }
    }
}
