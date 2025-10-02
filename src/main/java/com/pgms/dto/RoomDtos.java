package com.pgms.dto;

import com.pgms.util.Enums.RoomStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public final class RoomDtos {

    public static class CreateRequest {
        @NotBlank
        @Size(max = 32)
        public String number;
        public int floorNumber;
        @Min(1)
        @Max(20)
        public int capacity = 1;
        @DecimalMin(value = "0.0", inclusive = true)
        public BigDecimal baseRent;
    }

    public static class UpdateRequest {
        public UUID id;
        @NotBlank
        @Size(max = 32)
        public String number;
        public int floorNumber;
        @Min(1)
        @Max(20)
        public int capacity;
        @DecimalMin(value = "0.0", inclusive = true)
        public BigDecimal baseRent;
        public RoomStatus status;
    }

    public static class Response {
        public UUID id;
        public String number;
        public int floorNumber;
        public int capacity;
        public java.math.BigDecimal baseRent;
        public RoomStatus status;
    }

    private RoomDtos() {
    }
}
