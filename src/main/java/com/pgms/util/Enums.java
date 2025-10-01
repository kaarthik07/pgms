package com.pgms.util;

public class Enums {
    public enum TenantStatus {ACTIVE, NOTICE, LEFT, BLOCKED}

    public enum BedStatus {AVAILABLE, OCCUPIED, MAINTENANCE}

    public enum NoticeStatus {PENDING, APPROVED, REJECTED, COMPLETED}

    public enum BillStatus {DUE, PARTIAL, PAID, CANCELLED}

    public enum IdProofType {AADHAAR, PAN, PASSPORT, DRIVING_LICENSE, OTHER}
}
