package com.pgms.dto;

import com.pgms.util.Enums.TenantStatus;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.StringJoiner;

public class TenantDtos {
    public static class CreateRequest {
        @NotBlank
        public String orgCode;
        @NotBlank
        @Size(max = 100)
        public String fullName;
        @NotBlank
        @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian mobile format")
        public String phone;
        @Email
        public String email;
        public LocalDate dateOfBirth;
        public String fatherName;
        @Pattern(regexp = "^([6-9]\\d{9})?$", message = "Invalid Indian mobile format")
        public String fatherPhone;
        public String vehicleNumber;
        @Column(name = "created_at", nullable = false, updatable = false)
        private OffsetDateTime createdAt;

        @Column(name = "updated_at")
        private OffsetDateTime updatedAt;

        /** Allocation info (optional at onboarding) */
        public UUID roomId;

        /** 1-based bed index within the room; only used when roomId is sent */
        @Min(1)
        public Integer bedIndex;

        @Override
        public String toString() {
            return new StringJoiner(", ", "CreateRequest{", "}").add("orgCode='" + orgCode + "'").add("fullName='" + fullName + "'").add("phone='" + phone + "'").toString();
        }
    }

    public static class UpdateRequest {
        @NotNull
        public UUID id;
        @NotBlank
        @Size(max = 100)
        public String fullName;
        @NotBlank
        @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian mobile format")
        public String phone;
        @Email
        public String email;
        public LocalDate dateOfBirth;
        public String fatherName;
        @Pattern(regexp = "^([6-9]\\d{9})?$", message = "Invalid Indian mobile format")
        public String fatherPhone;
        public String vehicleNumber;
        public UUID roomId;
        public UUID bedId;
        public TenantStatus status;
        @Min(1) public Integer bedIndex; // must be sent with roomId if changing bed

        @Override
        public String toString() {
            return new StringJoiner(", ", "UpdateRequest{", "}")
                    .add("id=" + id)
                    .add("fullName='" + fullName + "'")
                    .add("status=" + status)
                    .toString();
        }
    }

    public static class Response {
        public UUID id;
        public String fullName;
        public String phone;
        public String email;
        public String status;
        public String room;
        public Integer bedIndex;

        @Override
        public String toString() {
            return new StringJoiner(", ", "Response{", "}")
                    .add("id=" + id)
                    .add("fullName='" + fullName + "'")
                    .toString();
        }
    }

    /** Query params (all optional) for search. */
    public static class SearchParams {
        /** Organization code (slug); strongly recommended to pass. */
        public String orgCode;

        /** Free-text search on name/phone/email. */
        @Size(max = 100)
        public String q;

        /** Filter by status. */
        public TenantStatus status;

        /** Filter by room number (e.g., "101"). */
        public String roomNumber;

        /** 1-based bed index */
        public Integer bedIndex;

        /** Optional date range on createdAt (joined date). */
        public LocalDate createdFrom;
        public LocalDate createdTo;

        /** Pagination & sorting */
        @Min(0) public Integer page = 0;
        @Min(1) public Integer size = 20; // default page size
        /** Spring sort syntax, e.g. "createdAt,desc" or "fullName,asc" */
        public String sort; // optional
    }

    /** Compact summary for list views. */
    public static class Summary {
        public UUID id;
        public String orgCode;
        public String fullName;
        public String phone;
        public String email;
        public TenantStatus status;
        public String roomNumber;
        public Integer bedIndex;
        public java.time.OffsetDateTime createdAt;
    }

    /** Generic page wrapper. */
    public static class PageResponse<T> {
        public List<T> content;
        public int page;
        public int size;
        public long totalElements;
        public int totalPages;
        public boolean first;
        public boolean last;
    }
}
