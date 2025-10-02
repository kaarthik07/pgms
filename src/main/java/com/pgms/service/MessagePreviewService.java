package com.pgms.service;

import com.pgms.domain.Organization;
import com.pgms.domain.Tenant;
import com.pgms.dto.MessagePreviewDtos;
import com.pgms.repo.OrganizationRepo;
import com.pgms.repo.TenantRepo;
import com.pgms.util.TemplateEngine;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MessagePreviewService {

    private final TenantRepo tenants;
    private final OrganizationRepo orgs;
    private final TemplateEngine engine;

    public MessagePreviewService(TenantRepo tenants, OrganizationRepo orgs, TemplateEngine engine) {
        this.tenants = tenants;
        this.orgs = orgs;
        this.engine = engine;
    }

    public MessagePreviewDtos.PreviewResponse preview(MessagePreviewDtos.PreviewRequest req) {
        // Build base context
        Map<String, String> ctx = new LinkedHashMap<>();
        enrichFromIds(ctx, req.tenantId, req.organizationId);

        // User-supplied variables override enriched values
        if (req.variables != null) {
            ctx.putAll(req.variables);
        }

        // Render body
        TemplateEngine.RenderResult body = engine.render(req.bodyTemplate, ctx);

        // Render subject (email only)
        String subject = null;
        if (req.channel == MessagePreviewDtos.Channel.EMAIL && req.subjectTemplate != null) {
            subject = engine.render(req.subjectTemplate, ctx).output();
        }

        // Response
        MessagePreviewDtos.PreviewResponse res = new MessagePreviewDtos.PreviewResponse();
        res.channel = req.channel;
        res.subject = subject;
        res.body = body.output();
        res.bodyLength = res.body != null ? res.body.length() : 0;
        res.placeholdersFound = body.placeholdersFound();
        res.placeholdersMissing = body.placeholdersMissing();
        res.variablesUnused = body.variablesUnused(ctx.keySet());
        return res;
    }

    private void enrichFromIds(Map<String, String> ctx, String tenantId, String orgId) {
        if (tenantId != null && !tenantId.isBlank()) {
            tenants.findById(UUID.fromString(tenantId)).ifPresent(t -> {
                putIfNotNull(ctx, "tenantId", String.valueOf(t.getId()));
                putIfNotNull(ctx, "tenantName", t.getFullName());
                putIfNotNull(ctx, "tenantPhone", t.getPhone());
                if (t.getBed() != null) {
                    putIfNotNull(ctx, "bedNumber", t.getBed().getCode());
                }
                if (t.getRoom() != null) {
                    putIfNotNull(ctx, "roomNumber", t.getRoom().getNumber());
                }
                if (t.getOrg() != null) {
                    putIfNotNull(ctx, "orgCode", t.getOrg().getCode());
                    putIfNotNull(ctx, "orgName", t.getOrg().getName());
                }
            });
        }
        if (orgId != null && !orgId.isBlank()) {
            orgs.findById(UUID.fromString(orgId)).ifPresent(o -> {
                putIfNotNull(ctx, "orgId", String.valueOf(o.getId()));
                putIfNotNull(ctx, "orgCode", o.getCode());
                putIfNotNull(ctx, "orgName", o.getName());
                putIfNotNull(ctx, "orgPhone", o.getContactPhone());
            });
        }
        // Add a few generics that are often useful
        ctx.putIfAbsent("today", java.time.LocalDate.now().toString());
    }

    private static void putIfNotNull(Map<String, String> map, String k, Object v) {
        if (v != null) map.put(k, String.valueOf(v));
    }
}
