package com.pgms.repo;

import com.pgms.domain.Organization;
import com.pgms.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomRepo extends JpaRepository<Room, UUID> {
    // find all rooms for an organization
    List<Room> findByOrganization(Organization organization);

    // example composite lookups
    Optional<Room> findByOrganizationAndNumber(Organization organization, String number);

    // slug/code convenience (if Organization has field "code")
    List<Room> findByOrganization_Code(String orgCode);

    Optional<Room> findByOrganization_CodeAndNumber(String orgCode, String number);

    boolean existsByNumber(String number);

    Optional<Room> findByNumber(String number);
}
