package com.pgms.domain;

import jakarta.persistence.*;

import java.util.*;
import java.util.StringJoiner;

@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue
    private UUID id;
    @ManyToOne(optional = false)
    private Organization org;
    @Column(nullable = false)
    private String number;
    private String floor;
    private int capacity;
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bed> beds = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public Organization getOrg() {
        return org;
    }

    public void setOrg(Organization o) {
        org = o;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String n) {
        number = n;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String f) {
        floor = f;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int c) {
        capacity = c;
    }

    public List<Bed> getBeds() {
        return beds;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "Room{", "}").add("id=" + id).add("number='" + number + "'").add("capacity=" + capacity).toString();
    }
}
