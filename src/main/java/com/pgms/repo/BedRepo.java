package com.pgms.repo;

import com.pgms.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;
import java.util.UUID;

public interface BedRepo extends JpaRepository<Bed, UUID> {
    List<Bed> findByRoom(Room room);
    Optional<Organization> findBySlug(String slug);
    Optional<Organization> findByCode(String code);
    boolean existsBySlug(String slug);
    boolean existsByCode(String code);
}
