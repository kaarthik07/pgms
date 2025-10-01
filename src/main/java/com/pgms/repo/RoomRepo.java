package com.pgms.repo;

import com.pgms.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;
import java.util.UUID;

public interface RoomRepo extends JpaRepository<Room, UUID> {
    List<Room> findByOrg(Organization org);
}
