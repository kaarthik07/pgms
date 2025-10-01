package com.pgms.domain;

import com.pgms.util.Enums.BedStatus;
import jakarta.persistence.*;

import java.util.*;
import java.util.StringJoiner;

@Entity
@Table(name = "beds", uniqueConstraints = @UniqueConstraint(columnNames = {"room_id", "bed_index"}))
public class Bed {
    @Id
    @GeneratedValue
    private UUID id;
    @ManyToOne(optional = false)
    private Room room;
    @Column(name = "bed_index", nullable = false)
    private int index;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BedStatus status = BedStatus.AVAILABLE;
    private Double priceOverride;

    public UUID getId() {
        return id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room r) {
        room = r;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int i) {
        index = i;
    }

    public BedStatus getStatus() {
        return status;
    }

    public void setStatus(BedStatus s) {
        status = s;
    }

    public Double getPriceOverride() {
        return priceOverride;
    }

    public void setPriceOverride(Double p) {
        priceOverride = p;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "Bed{", "}").add("id=" + id).add("room=" + (room != null ? room.getNumber() : null)).add("index=" + index).add("status=" + status).toString();
    }
}
