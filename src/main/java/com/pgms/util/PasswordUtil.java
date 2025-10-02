package com.pgms.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public final class PasswordUtil {
    private static final SecureRandom RND = new SecureRandom();
    private static final int SALT_LEN = 16;
    private static final int ITER = 120_000;
    private static final int KEY_LEN = 256; // bits

    private PasswordUtil() {}

    public static String hash(String raw) {
        byte[] salt = new byte[SALT_LEN];
        RND.nextBytes(salt);
        byte[] hash = pbkdf2(raw.toCharArray(), salt, ITER, KEY_LEN);
        return "pbkdf2$" + ITER + "$" + b64(salt) + "$" + b64(hash);
    }

    public static boolean verify(String raw, String stored) {
        try {
            String[] parts = stored.split("\\$");
            int iter = Integer.parseInt(parts[1]);
            byte[] salt = b64d(parts[2]);
            byte[] expected = b64d(parts[3]);
            byte[] actual = pbkdf2(raw.toCharArray(), salt, iter, expected.length * 8);
            return slowEquals(expected, actual);
        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] pbkdf2(char[] pwd, byte[] salt, int iter, int keyLenBits) {
        try {
            var spec = new PBEKeySpec(pwd, salt, iter, keyLenBits);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("PBKDF2 failure", e);
        }
    }

    private static boolean slowEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; i++) diff |= (a[i] ^ b[i]);
        return diff == 0;
    }

    private static String b64(byte[] x) { return Base64.getEncoder().encodeToString(x); }
    private static byte[] b64d(String s) { return Base64.getDecoder().decode(s); }
}
