package com.pgms.service.spec;

import com.pgms.domain.Due;
import com.pgms.util.Enums;
import org.springframework.data.jpa.domain.Specification;

public final class DueSpecs {

    private DueSpecs() {}

    public static Specification<Due> phoneOrNameLike(String q) {
        if (q == null || q.isBlank()) return null;
        String like = "%" + q.toLowerCase() + "%";
        return (root, cq, cb) -> cb.or(
            cb.like(cb.lower(root.get("tenantPhone")), like),
            cb.like(cb.lower(root.get("tenantName")), like)
        );
    }

    public static Specification<Due> withStatus(Enums.DueStatus status) {
        if (status == null) return null;
        return (root, cq, cb) -> cb.equal(root.get("status"), status);
    }
}
