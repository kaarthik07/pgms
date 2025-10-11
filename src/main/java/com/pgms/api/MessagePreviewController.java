package com.pgms.api;

import com.pgms.dto.MessagePreviewDtos;
import com.pgms.service.MessagePreviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/messages")
public class MessagePreviewController {

    private final MessagePreviewService svc;

    public MessagePreviewController(MessagePreviewService svc) {
        this.svc = svc;
    }

    /**
     * Generate a preview of a message (SMS/WhatsApp/Email) by applying variables to a template.
     */
    @PostMapping("/preview")
    public ResponseEntity<MessagePreviewDtos.PreviewResponse> preview(
            @RequestBody @Valid MessagePreviewDtos.PreviewRequest req) {
        return ResponseEntity.ok(svc.preview(req));
    }
}
