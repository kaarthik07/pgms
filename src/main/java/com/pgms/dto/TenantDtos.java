package com.pgms.dto;

import com.pgms.util.Enums.TenantStatus;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
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
}
