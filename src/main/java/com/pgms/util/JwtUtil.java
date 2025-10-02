package com.pgms.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

public final class JwtUtil {
    private JwtUtil() {
    }

    public static String createToken(Map<String, Object> claims, String subject, long ttlSeconds, String secret) {
        long now = Instant.now().getEpochSecond();
        String header = b64Json(Map.of("alg", "HS256", "typ", "JWT"));
        String payload = b64Json(new java.util.LinkedHashMap<>() {{
            put("sub", subject);
            put("iat", now);
            put("exp", now + ttlSeconds);
            putAll(claims);
        }});
        String toSign = header + "." + payload;
        String sig = sign(toSign, secret);
        return toSign + "." + sig;
    }

    public static boolean verify(String token, String secret) {
        String[] p = token.split("\\.");
        if (p.length != 3) return false;
        String expected = sign(p[0] + "." + p[1], secret);
        if (!slowEquals(expected, p[2])) return false;
        String json = new String(Base64.getUrlDecoder().decode(p[1]), StandardCharsets.UTF_8);
        long exp = ((Number) new org.json.JSONObject(json).get("exp")).longValue();
        return Instant.now().getEpochSecond() < exp;
    }

    private static String sign(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] sig = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(sig);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static String b64Json(Map<String, Object> map) {
        String json = new org.json.JSONObject(map).toString();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }

    private static boolean slowEquals(String a, String b) {
        if (a.length() != b.length()) return false;
        int d = 0;
        for (int i = 0; i < a.length(); i++) d |= a.charAt(i) ^ b.charAt(i);
        return d == 0;
    }
}
