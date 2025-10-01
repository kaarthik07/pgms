package com.pgms.repo;

import com.pgms.domain.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;
import java.util.UUID;

public interface OrganizationRepo extends JpaRepository<Organization, UUID> {
    Optional<Organization> findBySlug(String slug);
    Optional<Organization> findByCode(String code);
    boolean existsBySlug(String slug);
    boolean existsByCode(String code);
}
