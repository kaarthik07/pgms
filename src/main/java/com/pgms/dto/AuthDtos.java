package com.pgms.dto;

import com.pgms.domain.Role;
import jakarta.validation.constraints.*;

public class AuthDtos {

    public static class SignupRequest {
        @Email
        @NotBlank
        public String email;
        @Pattern(regexp = "^([6-9]\\d{9})?$", message = "Invalid Indian mobile")
        public String phone; // optional
        @NotNull
        public Role role;
        @NotBlank
        @Size(min = 8, max = 120)
        public String tempPassword;
    }

    public static class LoginRequest {
        /**
         * email or phone
         */
        @NotBlank
        public String login;
        @NotBlank
        public String password;
        /**
         * optional TOTP code if enabled
         */
        public String totp;
    }

    public static class LoginResponse {
        public String token;
        public boolean requireTotp;
        public String message;
    }

    public static class ChangePasswordRequest {
        @NotBlank
        public String login;
        @NotBlank
        public String oldPassword;
        @NotBlank
        @Size(min = 8, max = 120)
        public String newPassword;
    }

    public static class EnableTotpResponse {
        public String secretBase32;
        public String otpauthUrl;
    }

    public static class AdminResetRequest {
        @NotBlank
        public String adminResetCode;
        @NotBlank
        public String login;      // user to reset
        @NotBlank
        @Size(min = 8, max = 120)
        public String newTempPassword;
    }
}
