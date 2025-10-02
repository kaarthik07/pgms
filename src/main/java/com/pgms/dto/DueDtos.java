package com.pgms.dto;

import com.pgms.util.Enums;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public final class DueDtos {

    private DueDtos() {
    }

    // --- Create ---
    public static class Create {
        @NotNull
        public UUID orgId;
        public UUID tenantId; // optional
        @NotBlank
        public String tenantName;
        @Pattern(regexp = "^(?:\\+?91)?[6-9]\\d{9}$", message = "Invalid Indian mobile format")
        @NotBlank
        public String tenantPhone;
        public String tenantGovId;

        @NotNull
        @DecimalMin("0.01")
        public BigDecimal amount;
        @NotBlank
        public String reason;

        public LocalDate fromDate;
        public LocalDate toDate;
    }

    // --- Search / List ---
    public static class SearchParams {
        public String phoneOrName;   // fuzzy
        public Enums.DueStatus status;     // OPEN / CLEARED / DISPUTED
        public int page = 0;
        public int size = 20;
    }

    public static class Summary {
        public UUID id;
        public String tenantName;
        public String tenantPhone;
        public String reason;
        public BigDecimal amount;
        public Enums.DueStatus status;
    }

    public static class Detail extends Summary {
        public String tenantGovId;
        public LocalDate fromDate;
        public LocalDate toDate;
        public String disputeNotes;
        public String clearedNotes;
    }

    public static class ClearRequest {
        @NotBlank
        public String notes;
    }

    public static class DisputeRequest {
        @NotBlank
        public String notes;
    }
}
