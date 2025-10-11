package com.pgms.util;

public class Enums {
    public enum TenantStatus {ACTIVE, NOTICE, LEFT, BLOCKED}

    public enum BedStatus {AVAILABLE, OCCUPIED, MAINTENANCE}

    public enum NoticeStatus {PENDING, APPROVED, REJECTED, COMPLETED}

    public enum BillStatus {
        PENDING,       // created, not yet due or unpaid
        OVERDUE,       // dueDate passed and not yet fully paid
        PARTIAL,       // some receipts exist but outstanding > 0
        PAID,          // fully settled
        CANCELLED      // voided by owner/admin
    }

    public enum PaymentMode {
        CASH, UPI, CARD, BANK_TRANSFER, RAZORPAY
    }

    public enum IdProofType {AADHAAR, PAN, PASSPORT, DRIVING_LICENSE, OTHER}

    public enum PaymentStatus {CREATED, CAPTURED, FAILED}

    public enum RoomStatus {AVAILABLE, FULL, MAINTENANCE}

    public enum DueStatus {
        OPEN,         // recorded and yet to be cleared
        DISPUTED,     // tenant disputes the amount
        CLEARED       // cleared/settled (cannot be edited further)
    }

    /**
     * Visibility of stored files. Keep Aadhaar/PAN PRIVATE.
     */
    public enum FileVisibility {
        PRIVATE,  // serve via presigned URL or backend stream
        PUBLIC    // can be served via CDN/public URL (e.g., logos/QR)
    }

    /**
     * Logical grouping helps with housekeeping and lifecycle rules.
     */
    public enum FileCategory {
        ORG_LOGO,
        ORG_QR,
        TENANT_PHOTO,
        TENANT_ID_PROOF,
        BILL_PDF,
        RECEIPT_PDF,
        OTHER
    }

}
