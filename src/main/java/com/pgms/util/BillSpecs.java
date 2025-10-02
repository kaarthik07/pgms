package com.pgms.util;

import com.pgms.domain.Bill;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.UUID;

public class BillSpecs {

    public static Specification<Bill> byFilters(UUID tenantId, Enums.BillStatus status,
                                                LocalDate startFrom, LocalDate startTo) {
        return (root, query, cb) -> {
            var p = cb.conjunction();

            if (tenantId != null) {
                p = cb.and(p, cb.equal(root.get("tenant").get("id"), tenantId));
            }
            if (status != null) {
                p = cb.and(p, cb.equal(root.get("status"), status));
            }
            if (startFrom != null) {
                p = cb.and(p, cb.greaterThanOrEqualTo(root.get("periodStart"), startFrom));
            }
            if (startTo != null) {
                p = cb.and(p, cb.lessThanOrEqualTo(root.get("periodStart"), startTo));
            }
            return p;
        };
    }
}
