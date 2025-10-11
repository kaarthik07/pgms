package com.pgms.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.pgms.domain.Organization;
import com.pgms.dto.QrDtos.InviteResponse;
import com.pgms.dto.QrDtos.OnboardingInfo;
import com.pgms.exception.NotFoundException;
import com.pgms.repo.OrganizationRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class QrService {

    private final OrganizationRepo orgs;

    @Value("${pgms.qr.baseUrl}")
    private String baseUrl;

    @Value("${pgms.qr.secret}")
    private String hmacSecret;

    @Value("${pgms.qr.ttlSeconds:86400}")
    private long ttlSeconds;

    /**
     * Create a fresh invite for the org and return metadata (URL + sig).
     */
    @Transactional
    public InviteResponse createInvite(String orgCode) {
        Organization org = orgs.findByCode(orgCode)
                .orElseThrow(() -> new NotFoundException("Org not found: " + orgCode));

        // Generate a new short invite code (can be used as "temp password" for first login)
        String invite = randomCode(8);
        Instant expiresAt = Instant.now().plusSeconds(ttlSeconds);

        org.setOnboardingCode(invite);
        org.setOnboardingExpiresAt(expiresAt);
        orgs.save(org);

        long ts = Instant.now().getEpochSecond();
        String sig = sign(orgCode, invite, ts);

        String deepLink = baseUrl +
                "?org=" + url(orgCode) +
                "&invite=" + url(invite) +
                "&ts=" + ts +
                "&sig=" + url(sig);

        InviteResponse resp = new InviteResponse();
        resp.orgCode = orgCode;
        resp.invite = invite;
        resp.ts = ts;
        resp.sig = sig;
        resp.deepLink = deepLink;
        return resp;
    }

    /**
     * Generate QR PNG for the deep-link.
     */
    public byte[] generateQrPng(String deepLink, int size) throws Exception {
        QRCodeWriter writer = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix matrix = writer.encode(deepLink, BarcodeFormat.QR_CODE, size, size, hints);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", out);
        return out.toByteArray();
    }

    /**
     * Validate link + return org info for autofill.
     */
    @Transactional(readOnly = true)
    public OnboardingInfo resolve(String orgCode, String invite, long ts, String sig) {
        // Basic replay protection / expiry
        if (Instant.ofEpochSecond(ts).plusSeconds(ttlSeconds).isBefore(Instant.now())) {
            throw new IllegalArgumentException("Link expired");
        }
        // Signature check
        String expected = sign(orgCode, invite, ts);
        if (!Objects.equals(expected, sig)) {
            throw new IllegalArgumentException("Invalid signature");
        }

        Organization org = orgs.findByCode(orgCode)
                .orElseThrow(() -> new NotFoundException("Org not found: " + orgCode));

        if (org.getOnboardingCode() == null ||
                !org.getOnboardingCode().equals(invite) ||
                (org.getOnboardingExpiresAt() != null && org.getOnboardingExpiresAt().isBefore(Instant.now()))) {
            throw new IllegalArgumentException("Invite invalid or expired");
        }

        OnboardingInfo info = new OnboardingInfo();
        info.orgCode = org.getCode();
        info.orgName = org.getName();
        info.contactPhone = org.getContactPhone();
        info.address = org.getAddressLine1();
        info.tempPassword = invite;
        info.expiresAt = org.getOnboardingExpiresAt();
        return info;
    }

    // --- helpers ---

    private String randomCode(int len) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        Random r = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(chars.charAt(r.nextInt(chars.length())));
        return sb.toString();
    }

    private String sign(String org, String invite, long ts) {
        try {
            String msg = org + "|" + invite + "|" + ts;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(hmacSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] h = mac.doFinal(msg.getBytes(StandardCharsets.UTF_8));
            // base64url without padding
            return Base64.encodeBase64URLSafeString(h);
        } catch (Exception e) {
            throw new RuntimeException("HMAC error", e);
        }
    }

    private String url(String v) {
        return URLEncoder.encode(v, StandardCharsets.UTF_8);
    }
}
