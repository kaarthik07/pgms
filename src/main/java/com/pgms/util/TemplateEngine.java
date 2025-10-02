package com.pgms.util;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Minimal, dependency-free template engine that replaces {{placeholders}}
 * with values from a context map. Unknown placeholders are left as-is.
 * Also reports found/missing/unused placeholders for a nice preview UX.
 */
@Component
public class TemplateEngine {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{\\s*([a-zA-Z0-9_\\.]+)\\s*\\}\\}");

    public RenderResult render(String template, Map<String, String> ctx) {
        if (template == null) template = "";

        Set<String> found = new LinkedHashSet<>();
        Set<String> missing = new LinkedHashSet<>();
        StringBuffer out = new StringBuffer();

        Matcher m = PLACEHOLDER.matcher(template);
        while (m.find()) {
            String key = m.group(1);
            found.add(key);
            String replacement = ctx.get(key);
            if (replacement == null) {
                missing.add(key);
                replacement = m.group(0); // leave {{key}} in place for visibility
            }
            // Escape backslashes and dollars to be safe in replacement
            replacement = Matcher.quoteReplacement(replacement);
            m.appendReplacement(out, replacement);
        }
        m.appendTail(out);

        Set<String> unused = new LinkedHashSet<>();
        if (ctx != null) {
            for (String k : ctx.keySet()) {
                if (!found.contains(k)) unused.add(k);
            }
        }

        return new RenderResult(out.toString(), found, missing, unused);
    }

    public static final class RenderResult {
        private final String output;
        private final Set<String> found;
        private final Set<String> missing;
        private final Set<String> unused;

        public RenderResult(String output, Set<String> found, Set<String> missing, Set<String> unused) {
            this.output = output;
            this.found = Collections.unmodifiableSet(found);
            this.missing = Collections.unmodifiableSet(missing);
            this.unused = Collections.unmodifiableSet(unused);
        }
        public String output() { return output; }
        public Set<String> placeholdersFound() { return found; }
        public Set<String> placeholdersMissing() { return missing; }
        public Set<String> variablesUnused(Set<String> providedKeys) { return unused; }
    }
}
