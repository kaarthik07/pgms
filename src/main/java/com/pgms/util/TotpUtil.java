package com.pgms.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Locale;

public final class TotpUtil {
    private TotpUtil() {
    }

    public static String newBase32Secret() {
        byte[] buf = new byte[20];
        new SecureRandom().nextBytes(buf);
        return base32Encode(buf);
    }

    public static boolean verifyCode(String base32Secret, String code, long timeMillis, int window) {
        if (base32Secret == null || code == null || code.length() < 6) return false;
        long t = timeMillis / 1000 / 30;
        for (int w = -window; w <= window; w++) {
            if (totp(base32Secret, t + w).equals(code)) return true;
        }
        return false;
    }

    public static String otpauthUrl(String issuer, String account, String base32Secret) {
        return "otpauth://totp/" + url(issuer) + ":" + url(account) +
                "?secret=" + base32Secret + "&issuer=" + url(issuer) + "&digits=6&period=30";
    }

    // --- helpers ---

    private static String totp(String base32Secret, long counter) {
        byte[] key = base32Decode(base32Secret);
        byte[] msg = ByteBuffer.allocate(8).putLong(counter).array();
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key, "HmacSHA1"));
            byte[] h = mac.doFinal(msg);
            int offset = h[h.length - 1] & 0x0f;
            int bin = ((h[offset] & 0x7f) << 24) | ((h[offset + 1] & 0xff) << 16) |
                    ((h[offset + 2] & 0xff) << 8) | (h[offset + 3] & 0xff);
            int otp = bin % 1_000_000;
            return String.format(Locale.US, "%06d", otp);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    // Simple Base32 (RFC 4648) helpers
    private static final String B32 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

    private static String base32Encode(byte[] data) {
        StringBuilder out = new StringBuilder((data.length * 8 + 4) / 5);
        int curr = 0, bits = 0;
        for (byte b : data) {
            curr = (curr << 8) | (b & 0xff);
            bits += 8;
            while (bits >= 5) {
                out.append(B32.charAt((curr >> (bits - 5)) & 31));
                bits -= 5;
            }
        }
        if (bits > 0) out.append(B32.charAt((curr << (5 - bits)) & 31));
        return out.toString();
    }

    private static byte[] base32Decode(String s) {
        int buffer = 0, bits = 0, count = 0;
        byte[] out = new byte[s.length() * 5 / 8 + 5];
        for (char c : s.toUpperCase(Locale.US).toCharArray()) {
            int val = B32.indexOf(c);
            if (val < 0) continue;
            buffer = (buffer << 5) | val;
            bits += 5;
            if (bits >= 8) {
                out[count++] = (byte) ((buffer >> (bits - 8)) & 0xff);
                bits -= 8;
            }
        }
        byte[] res = new byte[count];
        System.arraycopy(out, 0, res, 0, count);
        return res;
    }

    private static String url(String s) {
        return java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8);
    }
}
