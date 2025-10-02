package com.pgms.dto;

import com.pgms.util.Enums;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class ReceiptDtos {

    public static class Summary {
        public UUID id;
        public UUID billId;
        public UUID tenantId;
        public BigDecimal amountPaid;
        public Enums.PaymentMode mode;
        public String txnRef;
        public OffsetDateTime paidAt;
        public OffsetDateTime createdAt;
    }
}
