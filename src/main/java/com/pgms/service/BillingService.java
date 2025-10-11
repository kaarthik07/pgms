package com.pgms.service;

import com.pgms.domain.Bill;
import com.pgms.domain.Organization;
import com.pgms.domain.Receipt;
import com.pgms.domain.Tenant;
import com.pgms.dto.BillDtos;
import com.pgms.dto.ReceiptDtos;
import com.pgms.exception.BadRequestException;
import com.pgms.exception.NotFoundException;
import com.pgms.repo.BillRepo;
import com.pgms.repo.OrganizationRepo;
import com.pgms.repo.ReceiptRepo;
import com.pgms.repo.TenantRepo;
import com.pgms.util.BillSpecs;
import com.pgms.util.Enums;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BillingService {

    private static final Logger log = LoggerFactory.getLogger(BillingService.class);

    private final BillRepo bills;
    private final ReceiptRepo receipts;
    private final TenantRepo tenants;
    private final OrganizationRepo orgs;

    public BillingService(BillRepo bills, ReceiptRepo receipts, TenantRepo tenants, OrganizationRepo orgs) {
        this.bills = bills;
        this.receipts = receipts;
        this.tenants = tenants;
        this.orgs = orgs;
    }

    // ---------- Create bill (monthly or ad-hoc) ----------
    @Transactional
    public UUID createBill(BillDtos.CreateRequest req) {
        Organization org = orgs.findByCode(req.orgCode)
                .orElseThrow(() -> new NotFoundException("Org not found: " + req.orgCode));
        Tenant tenant = tenants.findById(req.tenantId)
                .orElseThrow(() -> new NotFoundException("Tenant not found: " + req.tenantId));

        if (req.periodStart.isAfter(req.periodEnd)) {
            throw new BadRequestException("periodStart must be <= periodEnd");
        }

        // Prevent duplicate overlapping bills for same period
        List<Bill> overlaps = bills.findByTenantAndPeriodStartLessThanEqualAndPeriodEndGreaterThanEqual(
                tenant, req.periodEnd, req.periodStart);
        if (!overlaps.isEmpty()) {
            throw new BadRequestException("Overlapping bill already exists for tenant in this period");
        }

        Bill b = new Bill();
        b.setOrg(org);
        b.setTenant(tenant);
        b.setPeriodStart(req.periodStart);
        b.setPeriodEnd(req.periodEnd);
        b.setDueDate(req.dueDate);
        b.setRentAmount(req.rentAmount);
        b.setUtilitiesAmount(req.utilitiesAmount);
        b.setDiscountAmount(req.discountAmount);
        b.setNotes(req.notes);

        bills.save(b);
        log.info("Created bill {} for tenant {}", b.getId(), tenant.getId());
        return b.getId();
    }

    // ---------- Search bills ----------
    @Transactional(readOnly = true)
    public BillDtos.PageResponse<BillDtos.Summary> search(UUID tenantId, Enums.BillStatus status,
                                                          LocalDate startFrom, LocalDate startTo,
                                                          int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "periodStart"));
        var spec = BillSpecs.byFilters(tenantId, status, startFrom, startTo);
        Page<Bill> result = bills.findAll(spec, pageable);
        var summaries = result.getContent().stream().map(this::toSummary).collect(Collectors.toList());
        return new BillDtos.PageResponse<>(summaries, result.getTotalElements(), result.getTotalPages());
    }

    // ---------- Get single bill ----------
    @Transactional(readOnly = true)
    public BillDtos.Summary get(UUID id) {
        Bill b = bills.findById(id).orElseThrow(() -> new NotFoundException("Bill not found: " + id));
        return toSummary(b);
    }

    // ---------- Mark payment & generate receipt ----------
    @Transactional
    public ReceiptDtos.Summary pay(BillDtos.PaymentRequest req) {
        Bill b = bills.findById(req.billId)
                .orElseThrow(() -> new NotFoundException("Bill not found: " + req.billId));

        if (b.getStatus() == Enums.BillStatus.CANCELLED) throw new BadRequestException("Bill is cancelled");
        if (req.amount.compareTo(BigDecimal.ZERO) <= 0) throw new BadRequestException("Amount must be > 0");
        if (req.amount.compareTo(b.outstanding()) > 0) {
            throw new BadRequestException("Amount exceeds outstanding");
        }

        // Apply payment on bill
        b.applyPayment(req.amount);

        // Create receipt
        Receipt r = new Receipt();
        r.setOrg(b.getOrg());
        r.setTenant(b.getTenant());
        r.setBill(b);
        r.setAmountPaid(req.amount);
        r.setPaymentMode(req.mode);
        r.setTxnRef(req.txnRef);
        r.setPaidAt(req.paidAt != null ? req.paidAt : OffsetDateTime.now());

        receipts.save(r);
        bills.save(b);

        log.info("Payment recorded: bill={}, amount={}, mode={}, receipt={}",
                b.getId(), req.amount, req.mode, r.getId());

        return toReceiptSummary(r);
    }

    // ---------- Map helpers ----------
    private BillDtos.Summary toSummary(Bill b) {
        var s = new BillDtos.Summary();
        s.id = b.getId();
        s.tenantId = b.getTenant().getId();
        s.periodStart = b.getPeriodStart();
        s.periodEnd = b.getPeriodEnd();
        s.dueDate = b.getDueDate();
        s.status = b.getStatus();
        s.totalAmount = b.totalAmount();
        s.paidAmount = b.getPaidAmount();
        s.outstanding = b.outstanding();
        s.notes = b.getNotes();
        return s;
    }

    private ReceiptDtos.Summary toReceiptSummary(Receipt r) {
        var s = new ReceiptDtos.Summary();
        s.id = r.getId();
        s.billId = r.getBill().getId();
        s.tenantId = r.getTenant().getId();
        s.amountPaid = r.getAmountPaid();
        s.mode = r.getPaymentMode();
        s.txnRef = r.getTxnRef();
        s.paidAt = r.getPaidAt();
        s.createdAt = r.getCreatedAt();
        return s;
    }
}
