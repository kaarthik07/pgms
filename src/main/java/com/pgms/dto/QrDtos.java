package com.pgms.dto;

import java.time.Instant;

public class QrDtos {

    /**
     * Returned after generating a QR invite.
     */
    public static class InviteResponse {
        public String orgCode;
        public String invite;      // onboarding_code
        public long ts;            // epoch seconds
        public String sig;         // HMAC-SHA256 base64url
        public String deepLink;    // full URL embedded in QR
    }

    /**
     * Returned by resolve endpoint so mobile app can auto-fill.
     */
    public static class OnboardingInfo {
        public String orgCode;
        public String orgName;
        public String contactPhone;
        public String address;
        public String tempPassword;
        public Instant expiresAt;
    }
}
