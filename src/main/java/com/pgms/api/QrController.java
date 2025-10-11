package com.pgms.api;

import com.pgms.dto.QrDtos.InviteResponse;
import com.pgms.dto.QrDtos.OnboardingInfo;
import com.pgms.service.QrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
public class QrController {

    private final QrService qr;

    /**
     * Admin/Owner: create a new invite and return QR (PNG) bytes.
     */
    @PostMapping(value = "/orgs/{orgCode}/qr", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateQr(
            @PathVariable String orgCode,
            @RequestParam(defaultValue = "320") int size) throws Exception {

        InviteResponse invite = qr.createInvite(orgCode);
        byte[] png = qr.generateQrPng(invite.deepLink, Math.max(200, Math.min(size, 1024)));

        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .contentType(MediaType.IMAGE_PNG)
                .body(png);
    }

    /**
     * (Optional) If you want to retrieve the deep-link JSON without PNG
     */
    @PostMapping("/orgs/{orgCode}/qr/meta")
    public InviteResponse createInviteMeta(@PathVariable String orgCode) {
        return qr.createInvite(orgCode);
    }

    /**
     * Mobile app calls this after decoding QR to fetch autofill info.
     */
    @GetMapping("/onboarding/resolve")
    public OnboardingInfo resolve(
            @RequestParam String org,
            @RequestParam String invite,
            @RequestParam long ts,
            @RequestParam String sig) {
        return qr.resolve(org, invite, ts, sig);
    }
}
