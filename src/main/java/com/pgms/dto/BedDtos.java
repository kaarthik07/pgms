package com.pgms.dto;

import com.pgms.util.Enums.BedStatus;
import jakarta.validation.constraints.*;
import java.util.UUID;

public final class BedDtos {

    public static class Response {
        public UUID id;
        public UUID roomId;
        public int index;
        public BedStatus status;
        public String code;
        public Double priceOverride;
    }

    public static class UpdateStatusRequest {
        @NotNull public BedStatus status;
    }

    public static class UpdateRequest {
        public String code;
        public Double priceOverride;
    }

    private BedDtos() {}
}
