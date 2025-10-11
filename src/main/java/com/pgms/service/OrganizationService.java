package com.pgms.service;

import com.pgms.domain.Organization;
import com.pgms.dto.OrgDtos;
import com.pgms.exception.BadRequestException;
import com.pgms.exception.NotFoundException;
import com.pgms.repo.OrganizationRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrganizationService {

    private static final Logger log = LoggerFactory.getLogger(OrganizationService.class);

    private final OrganizationRepo repo;

    public OrganizationService(OrganizationRepo repo) {
        this.repo = repo;
    }

    @Transactional
    public UUID create(OrgDtos.CreateRequest req) {
        log.debug("Create org {}", req);
        if (repo.existsBySlug(req.slug)) {
            throw new BadRequestException("slug already exists");
        }
        if (repo.existsByCode(req.code)) {
            throw new BadRequestException("code already exists");
        }
        Organization o = new Organization();
        o.setName(req.name);
        o.setSlug(req.slug);
        o.setCode(req.code);
        o.setLogoUrl(req.logoUrl);
        o.setPrimaryColor(req.primaryColor);
        o.setSecondaryColor(req.secondaryColor);
        o.setAddressLine1(req.addressLine1);
        o.setAddressLine2(req.addressLine2);
        o.setCity(req.city);
        o.setState(req.state);
        o.setPincode(req.pincode);
        o.setContactPhone(req.contactPhone);
        o.setWhatsappNumber(req.whatsappNumber);
        o.setReferralBonusCents(req.referralBonusCents);
        return repo.save(o).getId();
    }

    @Transactional
    public void update(OrgDtos.UpdateRequest req) {
        log.debug("Update org {}", req);
        Organization o = repo.findById(req.id)
                .orElseThrow(() -> new NotFoundException("Organization not found: " + req.id));
        o.setName(req.name);
        o.setLogoUrl(req.logoUrl);
        o.setPrimaryColor(req.primaryColor);
        o.setSecondaryColor(req.secondaryColor);
        o.setAddressLine1(req.addressLine1);
        o.setAddressLine2(req.addressLine2);
        o.setCity(req.city);
        o.setState(req.state);
        o.setPincode(req.pincode);
        o.setContactPhone(req.contactPhone);
        o.setWhatsappNumber(req.whatsappNumber);
        o.setReferralBonusCents(req.referralBonusCents);
        // slug/code immutable in v1 (avoid downstream conflicts)
        repo.save(o);
    }

    @Transactional
    public void delete(UUID id) {
        log.debug("Delete org {}", id);
        if (!repo.existsById(id)) {
            throw new NotFoundException("Organization not found: " + id);
        }
        // In future: safety checks (rooms/tenants exist) -> soft delete or block
        repo.deleteById(id);
    }

    @Transactional(readOnly = true)
    public OrgDtos.Response get(UUID id) {
        Organization o = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Organization not found: " + id));
        return toResponse(o);
    }

    @Transactional(readOnly = true)
    public Page<OrgDtos.Response> search(String q, Pageable pageable) {
        Page<Organization> page;
        if (q == null || q.isBlank()) {
            page = repo.findAll(pageable);
        } else {
            // Simple contains filter on name or slug using Example matcher for now
            Organization probe = new Organization();
            probe.setName(q);
            probe.setSlug(q);
            ExampleMatcher matcher = ExampleMatcher.matchingAny()
                    .withIgnoreCase()
                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                    .withIgnorePaths("id", "code", "logoUrl", "primaryColor", "secondaryColor",
                            "addressLine1", "addressLine2", "city", "state", "pincode",
                            "contactPhone", "whatsappNumber", "referralBonusCents",
                            "createdAt", "updatedAt");
            page = repo.findAll(Example.of(probe, matcher), pageable);
        }
        return page.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public OrgDtos.Branding brandingBySlug(String slug) {
        Organization o = repo.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Organization not found by slug: " + slug));
        OrgDtos.Branding b = new OrgDtos.Branding();
        b.orgId = o.getId().toString();
        b.name = o.getName();
        b.slug = o.getSlug();
        b.logoUrl = o.getLogoUrl();
        b.primaryColor = o.getPrimaryColor();
        b.secondaryColor = o.getSecondaryColor();
        return b;
    }

    @Transactional(readOnly = true)
    public OrgDtos.Resolve resolveByCode(String code) {
        Organization o = repo.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Organization not found by code: " + code));
        OrgDtos.Resolve r = new OrgDtos.Resolve();
        r.orgId = o.getId();
        r.slug = o.getSlug();
        r.name = o.getName();
        r.logoUrl = o.getLogoUrl();
        return r;
    }

    private OrgDtos.Response toResponse(Organization o) {
        OrgDtos.Response r = new OrgDtos.Response();
        r.id = o.getId();
        r.name = o.getName();
        r.slug = o.getSlug();
        r.code = o.getCode();
        r.logoUrl = o.getLogoUrl();
        r.primaryColor = o.getPrimaryColor();
        r.secondaryColor = o.getSecondaryColor();
        r.addressLine1 = o.getAddressLine1();
        r.addressLine2 = o.getAddressLine2();
        r.city = o.getCity();
        r.state = o.getState();
        r.pincode = o.getPincode();
        r.contactPhone = o.getContactPhone();
        r.whatsappNumber = o.getWhatsappNumber();
        r.referralBonusCents = o.getReferralBonusCents();
        return r;
    }
}
