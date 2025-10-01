package com.pgms.domain;

import com.pgms.util.Enums.TenantStatus;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.*;
import java.util.StringJoiner;

@Entity
@Table(name = "tenants")
public class Tenant {
    @Id
    @GeneratedValue
    private UUID id;
    @ManyToOne(optional = false)
    private Organization org;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TenantStatus status = TenantStatus.ACTIVE;
    @Column(nullable = false)
    private String fullName;
    @Column(nullable = false)
    private String phone;
    private String email;
    private LocalDate dateOfBirth;
    private String fatherName;
    private String fatherPhone;
    private String vehicleNumber;
    @ManyToOne
    private Room room;
    @ManyToOne
    private Bed bed;

    public UUID getId() {
        return id;
    }

    public Organization getOrg() {
        return org;
    }

    public void setOrg(Organization o) {
        org = o;
    }

    public TenantStatus getStatus() {
        return status;
    }

    public void setStatus(TenantStatus s) {
        status = s;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String n) {
        fullName = n;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String p) {
        phone = p;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String e) {
        email = e;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate d) {
        dateOfBirth = d;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String f) {
        fatherName = f;
    }

    public String getFatherPhone() {
        return fatherPhone;
    }

    public void setFatherPhone(String f) {
        fatherPhone = f;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String v) {
        vehicleNumber = v;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room r) {
        room = r;
    }

    public Bed getBed() {
        return bed;
    }

    public void setBed(Bed b) {
        bed = b;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "Tenant{", "}").add("id=" + id).add("name='" + fullName + "'").add("phone='" + phone + "'").add("status=" + status).toString();
    }
}
