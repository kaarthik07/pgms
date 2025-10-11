package com.pgms.repo;

import com.pgms.domain.Bed;
import com.pgms.domain.Organization;
import com.pgms.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BedRepo extends JpaRepository<Bed, UUID> {
    List<Bed> findByRoom(Room room);

    Optional<Organization> findBySlug(String slug);

    Optional<Organization> findByCode(String code);

    boolean existsBySlug(String slug);

    boolean existsByCode(String code);

    boolean existsByRoom_IdAndIndex(UUID roomId, int index);

    Optional<Bed> findByRoom_IdAndIndex(UUID roomId, int index);
}
