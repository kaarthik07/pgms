package com.pgms.service.spec;

import com.pgms.domain.Tenant;
import com.pgms.dto.TenantDtos;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

public final class TenantSpecs {

    private TenantSpecs() {
    }

    public static Specification<Tenant> build(TenantDtos.SearchParams p) {
        Specification<Tenant> spec = Specification.where(null);

        // orgCode
        if (p.orgCode != null && !p.orgCode.isBlank()) {
            spec = spec.and((root, cq, cb) ->
                    cb.equal(root.get("organization").get("code"), p.orgCode));
        }

        // status
        if (p.status != null) {
            spec = spec.and((root, cq, cb) ->
                    cb.equal(root.get("status"), p.status));
        }

        // roomNumber
        if (p.roomNumber != null && !p.roomNumber.isBlank()) {
            spec = spec.and((root, cq, cb) ->
                    cb.equal(root.get("room").get("number"), p.roomNumber));
        }

        // bedIndex
        if (p.bedIndex != null) {
            spec = spec.and((root, cq, cb) ->
                    cb.equal(root.get("bed").get("index"), p.bedIndex));
        }

        // free-text q -> name/phone/email (ILIKE-ish)
        if (p.q != null && !p.q.isBlank()) {
            String like = "%" + p.q.trim().toLowerCase() + "%";
            spec = spec.and((root, cq, cb) -> cb.or(
                    cb.like(cb.lower(root.get("fullName")), like),
                    cb.like(cb.lower(root.get("phone")), like),
                    cb.like(cb.lower(root.get("email")), like)
            ));
        }

        // createdAt date range
        if (p.createdFrom != null || p.createdTo != null) {
            LocalDate from = p.createdFrom != null ? p.createdFrom : LocalDate.of(1970, 1, 1);
            LocalDate to = p.createdTo != null ? p.createdTo : LocalDate.now();
            OffsetDateTime start = from.atStartOfDay().atOffset(java.time.ZoneOffset.UTC);
            OffsetDateTime end = to.atTime(LocalTime.MAX).atOffset(java.time.ZoneOffset.UTC);
            spec = spec.and((root, cq, cb) ->
                    cb.between(root.get("createdAt"), start, end));
        }

        return spec;
    }

    public static Specification<Tenant> hasNameLike(String name) {
        return (root, query, cb) -> name == null ? null :
                cb.like(cb.lower(root.get("fullName")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Tenant> hasPhone(String phone) {
        return (root, query, cb) -> phone == null ? null :
                cb.equal(root.get("phone"), phone);
    }
}
