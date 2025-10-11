package com.pgms.domain;

import com.pgms.util.Enums;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

@Entity
@Table(
        name = "rooms",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_room_number", columnNames = {"number"})
        }
)
public class Room {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "number", nullable = false, length = 32)
    private String number;

    @Column(name = "floor_number")
    private int floorNumber;

    @Column(name = "capacity", nullable = false)
    private int capacity = 1;

    @Column(name = "base_rent")
    private BigDecimal baseRent;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Enums.RoomStatus status = Enums.RoomStatus.AVAILABLE;

    @ManyToOne(optional = false)
    @JoinColumn(name = "organization_id")
    private Organization organization; // <-- field name is "organization"

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bed> beds = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public BigDecimal getBaseRent() {
        return baseRent;
    }

    public void setBaseRent(BigDecimal baseRent) {
        this.baseRent = baseRent;
    }

    public Enums.RoomStatus getStatus() {
        return status;
    }

    public void setStatus(Enums.RoomStatus status) {
        this.status = status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<Bed> getBeds() {
        return beds;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Room.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("number='" + number + "'")
                .add("floor=" + floorNumber)
                .add("capacity=" + capacity)
                .add("status=" + status)
                .toString();
    }
}
