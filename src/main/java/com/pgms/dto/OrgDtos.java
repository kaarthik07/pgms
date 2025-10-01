package com.pgms.dto;

import jakarta.validation.constraints.*;
import java.util.StringJoiner;
import java.util.UUID;

public final class OrgDtos {

    public static class CreateRequest {
        @NotBlank @Size(max = 120)
        public String name;

        /** lowercase letters, digits, hyphen only */
        @NotBlank @Pattern(regexp = "^[a-z0-9-]{2,60}$", message = "slug must be lowercase, digits or hyphen")
        public String slug;

        /** short public code (letters+digits) */
        @NotBlank @Pattern(regexp = "^[A-Za-z0-9]{4,32}$", message = "code must be alphanumeric 4-32 chars")
        public String code;

        @Size(max = 512) public String logoUrl;
        @Size(max = 16)  public String primaryColor;
        @Size(max = 16)  public String secondaryColor;

        @Size(max = 160) public String addressLine1;
        @Size(max = 160) public String addressLine2;
        @Size(max = 80)  public String city;
        @Size(max = 80)  public String state;
        @Size(max = 16)  public String pincode;

        /** Indian mobile format (10 digits, starts 6-9) */
        @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Invalid Indian mobile format")
        public String contactPhone;

        @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Invalid Indian mobile format")
        public String whatsappNumber;

        @Min(0) @Max(10_000_00)  // up to ₹100,000 in paise
        public Integer referralBonusCents;

        @Override public String toString() {
            return new StringJoiner(", ", CreateRequest.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("slug='" + slug + "'")
                .add("code='" + code + "'")
                .toString();
        }
    }

    public static class UpdateRequest {
        @NotNull public UUID id;

        @NotBlank @Size(max = 120)
        public String name;

        @Size(max = 512) public String logoUrl;
        @Size(max = 16)  public String primaryColor;
        @Size(max = 16)  public String secondaryColor;

        @Size(max = 160) public String addressLine1;
        @Size(max = 160) public String addressLine2;
        @Size(max = 80)  public String city;
        @Size(max = 80)  public String state;
        @Size(max = 16)  public String pincode;

        @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Invalid Indian mobile format")
        public String contactPhone;

        @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Invalid Indian mobile format")
        public String whatsappNumber;

        @Min(0) @Max(10_000_00)
        public Integer referralBonusCents;

        @Override public String toString() {
            return new StringJoiner(", ", UpdateRequest.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .toString();
        }
    }

    public static class Response {
        public UUID id;
        public String name;
        public String slug;
        public String code;
        public String logoUrl;
        public String primaryColor;
        public String secondaryColor;
        public String addressLine1;
        public String addressLine2;
        public String city;
        public String state;
        public String pincode;
        public String contactPhone;
        public String whatsappNumber;
        public Integer referralBonusCents;
    }

    /** what the apps need for white-label runtime theming */
    public static class Branding {
        public String orgId;
        public String name;
        public String slug;
        public String logoUrl;
        public String primaryColor;
        public String secondaryColor;
    }

    /** /resolve?code=XYZ response */
    public static class Resolve {
        public UUID orgId;
        public String slug;
        public String name;
        public String logoUrl;
    }

    private OrgDtos() { }
}
