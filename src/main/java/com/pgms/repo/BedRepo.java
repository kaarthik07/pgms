package com.pgms.repo;

import com.pgms.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;
import java.util.UUID;

public interface BedRepo extends JpaRepository<Bed, UUID> {
    List<Bed> findByRoom(Room room);
}
