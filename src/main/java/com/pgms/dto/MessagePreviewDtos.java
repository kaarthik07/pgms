package com.pgms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public final class MessagePreviewDtos {

    public enum Channel {SMS, WHATSAPP, EMAIL}

    public static class PreviewRequest {
        /**
         * Channel helps the UI know if a subject is relevant (EMAIL only).
         */
        @NotNull
        public Channel channel;

        /**
         * Optional: email subject template (used when channel == EMAIL).
         */
        public String subjectTemplate;

        /**
         * Required: body template supporting {{placeholders}}.
         */
        @NotBlank
        public String bodyTemplate;

        /**
         * Variables used to render templates; user-provided take precedence.
         */
        public Map<String, String> variables = Collections.emptyMap();

        /**
         * Optional: enrich variables from tenant/org (if present).
         */
        public String tenantId;      // UUID string, optional
        public String organizationId;// UUID string, optional

        public Channel getChannel() {
            return channel;
        }

        public void setChannel(Channel channel) {
            this.channel = channel;
        }

        public String getSubjectTemplate() {
            return subjectTemplate;
        }

        public void setSubjectTemplate(String subjectTemplate) {
            this.subjectTemplate = subjectTemplate;
        }

        public String getBodyTemplate() {
            return bodyTemplate;
        }

        public void setBodyTemplate(String bodyTemplate) {
            this.bodyTemplate = bodyTemplate;
        }

        public Map<String, String> getVariables() {
            return variables;
        }

        public void setVariables(Map<String, String> variables) {
            this.variables = variables;
        }

        public String getTenantId() {
            return tenantId;
        }

        public void setTenantId(String tenantId) {
            this.tenantId = tenantId;
        }

        public String getOrganizationId() {
            return organizationId;
        }

        public void setOrganizationId(String organizationId) {
            this.organizationId = organizationId;
        }
    }

    public static class PreviewResponse {
        public Channel channel;
        public String subject;      // may be null/empty for SMS/WhatsApp
        public String body;
        public int bodyLength;
        public Set<String> placeholdersFound;
        public Set<String> placeholdersMissing; // in template but not provided/enriched
        public Set<String> variablesUnused;     // provided vars not used in template

        public Channel getChannel() {
            return channel;
        }

        public void setChannel(Channel channel) {
            this.channel = channel;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public int getBodyLength() {
            return bodyLength;
        }

        public void setBodyLength(int bodyLength) {
            this.bodyLength = bodyLength;
        }

        public Set<String> getPlaceholdersFound() {
            return placeholdersFound;
        }

        public void setPlaceholdersFound(Set<String> placeholdersFound) {
            this.placeholdersFound = placeholdersFound;
        }

        public Set<String> getPlaceholdersMissing() {
            return placeholdersMissing;
        }

        public void setPlaceholdersMissing(Set<String> placeholdersMissing) {
            this.placeholdersMissing = placeholdersMissing;
        }

        public Set<String> getVariablesUnused() {
            return variablesUnused;
        }

        public void setVariablesUnused(Set<String> variablesUnused) {
            this.variablesUnused = variablesUnused;
        }
    }
}
