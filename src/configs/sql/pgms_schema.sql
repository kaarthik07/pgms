
-- PG Management System - Database Schema (PostgreSQL)

-- =============================
-- USERS TABLE (for Admin, Owner, Tenant login)
-- =============================
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'OWNER', 'TENANT')),
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20) UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    security_question VARCHAR(255),
    security_answer VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_users_role ON users(role);

-- =============================
-- PG TABLE
-- =============================
CREATE TABLE pgs (
    pg_id SERIAL PRIMARY KEY,
    owner_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(20) UNIQUE NOT NULL,
    address TEXT NOT NULL,
    logo_url TEXT,
    subscription_status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_pgs_owner_id ON pgs(owner_id);

-- =============================
-- ROOMS & BEDS TABLES
-- =============================
CREATE TABLE rooms (
    room_id SERIAL PRIMARY KEY,
    pg_id INT NOT NULL REFERENCES pgs(pg_id) ON DELETE CASCADE,
    room_number VARCHAR(20) NOT NULL,
    floor_number INT,
    capacity INT NOT NULL,
    type VARCHAR(20), -- e.g. Single, Double, Triple
    price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_rooms_pg_id ON rooms(pg_id);

CREATE TABLE beds (
    bed_id SERIAL PRIMARY KEY,
    room_id INT NOT NULL REFERENCES rooms(room_id) ON DELETE CASCADE,
    bed_number VARCHAR(20) NOT NULL,
    is_occupied BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_beds_room_id ON beds(room_id);

-- =============================
-- TENANTS TABLE
-- =============================
CREATE TABLE tenants (
    tenant_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    bed_id INT REFERENCES beds(bed_id) ON DELETE SET NULL,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    father_phone VARCHAR(20),
    vehicle_number VARCHAR(50),
    id_proofs JSONB, -- stores Aadhaar/PAN as JSON
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','NOTICE','LEFT','DUE')),
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    left_at TIMESTAMP
);
CREATE INDEX idx_tenants_bed_id ON tenants(bed_id);
CREATE INDEX idx_tenants_status ON tenants(status);

-- =============================
-- BILLING & RECEIPTS
-- =============================
CREATE TABLE bills (
    bill_id SERIAL PRIMARY KEY,
    tenant_id INT NOT NULL REFERENCES tenants(tenant_id) ON DELETE CASCADE,
    amount DECIMAL(10,2) NOT NULL,
    due_date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING','PAID','OVERDUE')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_bills_tenant_id ON bills(tenant_id);
CREATE INDEX idx_bills_status ON bills(status);

CREATE TABLE receipts (
    receipt_id SERIAL PRIMARY KEY,
    bill_id INT NOT NULL REFERENCES bills(bill_id) ON DELETE CASCADE,
    paid_amount DECIMAL(10,2) NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_method VARCHAR(50)
);
CREATE INDEX idx_receipts_bill_id ON receipts(bill_id);

-- =============================
-- NOTICES
-- =============================
CREATE TABLE notices (
    notice_id SERIAL PRIMARY KEY,
    tenant_id INT NOT NULL REFERENCES tenants(tenant_id) ON DELETE CASCADE,
    requested_date DATE NOT NULL,
    approved_date DATE,
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING','APPROVED','REJECTED')),
    early_exit_fee DECIMAL(10,2) DEFAULT 0,
    final_settlement JSONB
);
CREATE INDEX idx_notices_tenant_id ON notices(tenant_id);

-- SETTLEMENTS
CREATE TABLE settlements (
    settlement_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    notice_id UUID REFERENCES notices(id),
    final_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING','PAID')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_settlements_tenant_id ON settlements(tenant_id);

-- DUES REGISTRY
CREATE TABLE IF NOT EXISTS dues (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    org_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    tenant_id UUID REFERENCES tenants(id) ON DELETE SET NULL,

    tenant_name VARCHAR(120) NOT NULL,
    tenant_phone VARCHAR(15) NOT NULL,
    tenant_gov_id VARCHAR(40),

    amount NUMERIC(12,2) NOT NULL,
    reason VARCHAR(200) NOT NULL,

    from_date DATE,
    to_date DATE,

    status VARCHAR(20) NOT NULL CHECK (status IN ('OPEN','DISPUTED','CLEARED')) DEFAULT 'OPEN',
    cleared_at TIMESTAMPTZ,
    cleared_notes VARCHAR(200),
    dispute_notes VARCHAR(200),

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS ix_dues_org_id     ON dues(org_id);
CREATE INDEX IF NOT EXISTS ix_dues_tenant_id  ON dues(tenant_id);
CREATE INDEX IF NOT EXISTS ix_dues_phone      ON dues(tenant_phone);
CREATE INDEX IF NOT EXISTS ix_dues_status     ON dues(status);

CREATE INDEX IF NOT EXISTS ix_dues_org_id     ON dues(org_id);
CREATE INDEX IF NOT EXISTS ix_dues_tenant_id  ON dues(tenant_id);
CREATE INDEX IF NOT EXISTS ix_dues_phone      ON dues(tenant_phone);
CREATE INDEX IF NOT EXISTS ix_dues_status     ON dues(status);

-- =============================
-- REFERRALS & WALLET
-- =============================
CREATE TABLE referrals (
    referral_id SERIAL PRIMARY KEY,
    referrer_owner_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    referred_pg_id INT REFERENCES pgs(pg_id) ON DELETE SET NULL,
    bonus_amount DECIMAL(10,2) DEFAULT 0,
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING','APPROVED','REJECTED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_referrals_referrer ON referrals(referrer_owner_id);

CREATE TABLE wallets (
    wallet_id SERIAL PRIMARY KEY,
    owner_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    balance DECIMAL(10,2) DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_wallets_owner_id ON wallets(owner_id);

-- =============================
-- REMINDERS
-- =============================
CREATE TABLE reminders (
    reminder_id SERIAL PRIMARY KEY,
    tenant_id INT NOT NULL REFERENCES tenants(tenant_id) ON DELETE CASCADE,
    message TEXT NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING','SENT','FAILED')),
    sent_at TIMESTAMP
);
CREATE INDEX idx_reminders_tenant_id ON reminders(tenant_id);

-- =============================
-- PAYMENTS (Razorpay integration)
-- =============================
CREATE TABLE payments (
    payment_id SERIAL PRIMARY KEY,
    tenant_id INT NOT NULL REFERENCES tenants(tenant_id) ON DELETE CASCADE,
    order_id VARCHAR(100) NOT NULL,
    payment_status VARCHAR(20) DEFAULT 'CREATED' CHECK (payment_status IN ('CREATED','SUCCESS','FAILED')),
    amount DECIMAL(10,2) NOT NULL,
    method VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_payments_tenant_id ON payments(tenant_id);
CREATE INDEX idx_payments_order_id ON payments(order_id);
