package com.pgms.domain;

import jakarta.persistence.*;

import java.util.*;
import java.util.StringJoiner;

@Entity
@Table(name = "organizations")
public class Organization {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(nullable = false, unique = true, length = 64)
    private String code;
    @Column(nullable = false)
    private String name;
    private String brandingPrimaryColor;
    private String logoUrl;

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String c) {
        code = c;
    }

    public String getName() {
        return name;
    }

    public void setName(String n) {
        name = n;
    }

    public String getBrandingPrimaryColor() {
        return brandingPrimaryColor;
    }

    public void setBrandingPrimaryColor(String c) {
        brandingPrimaryColor = c;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String l) {
        logoUrl = l;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "Organization{", "}").add("id=" + id).add("code='" + code + "'").add("name='" + name + "'").toString();
    }
}
