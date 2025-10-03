-- =============================================================
-- PGMS – PostgreSQL Schema (finalized)
-- Compatible with PostgreSQL 16+
-- Run as superuser or a role with CREATE EXTENSION privileges
-- =============================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;   -- for gen_random_uuid()

CREATE SCHEMA IF NOT EXISTS pgms;
SET search_path TO pgms, public;

-- Organizations
CREATE TABLE IF NOT EXISTS organizations (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name          VARCHAR(120) NOT NULL,
  code          VARCHAR(60)  NOT NULL UNIQUE,
  logo_url      TEXT,
  primary_color VARCHAR(16),
  secondary_color VARCHAR(16),
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_org_name ON organizations (name);

-- Users (roles: ADMIN, OWNER, TENANT)
CREATE TABLE IF NOT EXISTS users (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  org_id          UUID NULL REFERENCES organizations(id) ON DELETE SET NULL,
  role            VARCHAR(12) NOT NULL CHECK (role IN ('ADMIN','OWNER','TENANT')),
  email           VARCHAR(180),
  phone           VARCHAR(32),
  login           VARCHAR(180) UNIQUE,
  password_hash   TEXT NOT NULL,
  force_pwd_change BOOLEAN NOT NULL DEFAULT TRUE,
  totp_secret     VARCHAR(64),
  is_active       BOOLEAN NOT NULL DEFAULT TRUE,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT uq_user_email UNIQUE (email),
  CONSTRAINT uq_user_phone UNIQUE (phone)
);
CREATE INDEX IF NOT EXISTS idx_users_org_role ON users(org_id, role);

-- Rooms
CREATE TABLE IF NOT EXISTS rooms (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  org_id      UUID NOT NULL REFERENCES organizations(id) ON DELETE RESTRICT,
  room_number VARCHAR(32) NOT NULL,
  floor       INT,
  capacity    INT NOT NULL CHECK (capacity >= 1),
  base_rent   NUMERIC(12,2) NOT NULL CHECK (base_rent >= 0),
  created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT uq_room_per_org UNIQUE (org_id, room_number)
);
CREATE INDEX IF NOT EXISTS idx_rooms_org ON rooms(org_id);

-- Beds
CREATE TABLE IF NOT EXISTS beds (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  room_id     UUID NOT NULL REFERENCES rooms(id) ON DELETE RESTRICT,
  bed_index   INT  NOT NULL CHECK (bed_index >= 1),
  status      VARCHAR(16) NOT NULL CHECK (status IN ('AVAILABLE','OCCUPIED','MAINTENANCE')),
  created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT uq_bed_per_room UNIQUE (room_id, bed_index)
);
CREATE INDEX IF NOT EXISTS idx_beds_room ON beds(room_id);
CREATE INDEX IF NOT EXISTS idx_beds_status ON beds(status);

-- Tenants
CREATE TABLE IF NOT EXISTS tenants (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  org_id        UUID NOT NULL REFERENCES organizations(id) ON DELETE RESTRICT,
  user_id       UUID UNIQUE REFERENCES users(id) ON DELETE SET NULL,
  full_name     VARCHAR(160) NOT NULL,
  phone         VARCHAR(32)  NOT NULL,
  email         VARCHAR(180),
  father_phone  VARCHAR(32),
  vehicle_number VARCHAR(32),
  status        VARCHAR(12) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','NOTICE','LEFT')),
  rent_amount   NUMERIC(12,2) NOT NULL CHECK (rent_amount >= 0),
  bed_id        UUID UNIQUE REFERENCES beds(id) ON DELETE SET NULL,
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_tenants_org ON tenants(org_id);
CREATE INDEX IF NOT EXISTS idx_tenants_status ON tenants(status);
CREATE INDEX IF NOT EXISTS idx_tenants_phone ON tenants(phone);

-- Tenant ID proofs
CREATE TABLE IF NOT EXISTS tenant_id_proofs (
  id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  tenant_id  UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
  proof_type VARCHAR(16) NOT NULL CHECK (proof_type IN ('AADHAAR','PAN','OTHER')),
  proof_value VARCHAR(120) NOT NULL,
  file_url   TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_tidp_tenant ON tenant_id_proofs(tenant_id);

-- Bills
CREATE TABLE IF NOT EXISTS bills (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  tenant_id   UUID NOT NULL REFERENCES tenants(id) ON DELETE RESTRICT,
  month       CHAR(7) NOT NULL,
  amount      NUMERIC(12,2) NOT NULL CHECK (amount >= 0),
  status      VARCHAR(12) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING','PAID','PARTIAL','CANCELLED')),
  notes       TEXT,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT uq_bill_tenant_month UNIQUE (tenant_id, month)
);
CREATE INDEX IF NOT EXISTS idx_bills_tenant ON bills(tenant_id);
CREATE INDEX IF NOT EXISTS idx_bills_status ON bills(status);

-- Receipts
CREATE TABLE IF NOT EXISTS receipts (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  bill_id     UUID NOT NULL REFERENCES bills(id) ON DELETE RESTRICT,
  amount      NUMERIC(12,2) NOT NULL CHECK (amount > 0),
  mode        VARCHAR(16) NOT NULL,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_receipts_bill ON receipts(bill_id);

-- Notices
CREATE TABLE IF NOT EXISTS notices (
  id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  tenant_id        UUID NOT NULL REFERENCES tenants(id) ON DELETE RESTRICT,
  start_date       DATE NOT NULL DEFAULT CURRENT_DATE,
  requested_move_out DATE NOT NULL,
  status           VARCHAR(12) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING','APPROVED','REJECTED','MODIFIED')),
  created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_notices_tenant ON notices(tenant_id);

-- Settlements
CREATE TABLE IF NOT EXISTS settlements (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  tenant_id   UUID NOT NULL REFERENCES tenants(id) ON DELETE RESTRICT,
  rent_due    NUMERIC(12,2) NOT NULL DEFAULT 0,
  utilities   NUMERIC(12,2) NOT NULL DEFAULT 0,
  early_exit_fee NUMERIC(12,2) NOT NULL DEFAULT 0,
  discount    NUMERIC(12,2) NOT NULL DEFAULT 0,
  final_amount NUMERIC(12,2) NOT NULL,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_settlements_tenant ON settlements(tenant_id);

-- Dues
CREATE TABLE IF NOT EXISTS dues (
  id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  tenant_id  UUID NOT NULL REFERENCES tenants(id) ON DELETE RESTRICT,
  org_id     UUID NOT NULL REFERENCES organizations(id) ON DELETE RESTRICT,
  amount     NUMERIC(12,2) NOT NULL CHECK (amount > 0),
  reason     TEXT NOT NULL,
  status     VARCHAR(12) NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN','RESOLVED')),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_dues_tenant ON dues(tenant_id);
CREATE INDEX IF NOT EXISTS idx_dues_org ON dues(org_id);
CREATE INDEX IF NOT EXISTS idx_dues_status ON dues(status);

-- Referrals
CREATE TABLE IF NOT EXISTS referrals (
  id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  referrer_owner_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
  referred_phone    VARCHAR(32) NOT NULL,
  status            VARCHAR(12) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING','CLAIMED','REJECTED')),
  created_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_referrals_referrer ON referrals(referrer_owner_id);
CREATE INDEX IF NOT EXISTS idx_referrals_status ON referrals(status);

-- Wallets
CREATE TABLE IF NOT EXISTS wallets (
  owner_id  UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
  balance   NUMERIC(12,2) NOT NULL DEFAULT 0,
  currency  VARCHAR(8) NOT NULL DEFAULT 'INR',
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Wallet transactions
CREATE TABLE IF NOT EXISTS wallet_txns (
  id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  owner_id   UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  amount     NUMERIC(12,2) NOT NULL,
  reason     VARCHAR(80) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_wtx_owner ON wallet_txns(owner_id);

-- Payments
CREATE TABLE IF NOT EXISTS payments (
  id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  bill_id        UUID REFERENCES bills(id) ON DELETE SET NULL,
  tenant_id      UUID REFERENCES tenants(id) ON DELETE SET NULL,
  razorpay_order_id   VARCHAR(64),
  razorpay_payment_id VARCHAR(64),
  amount_paise   BIGINT,
  currency       VARCHAR(8),
  status         VARCHAR(20),
  raw_payload    JSONB,
  created_at     TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_payments_bill ON payments(bill_id);
CREATE INDEX IF NOT EXISTS idx_payments_tenant ON payments(tenant_id);
CREATE INDEX IF NOT EXISTS idx_payments_rzp_order ON payments(razorpay_order_id);
CREATE INDEX IF NOT EXISTS idx_payments_status ON payments(status);

-- Touch triggers
CREATE OR REPLACE FUNCTION touch_updated_at()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
  NEW.updated_at = now();
  RETURN NEW;
END $$;

DO $$ BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_org_touch') THEN
    CREATE TRIGGER trg_org_touch BEFORE UPDATE ON organizations
      FOR EACH ROW EXECUTE FUNCTION touch_updated_at();
  END IF;
END $$;

DO $$ BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_rooms_touch') THEN
    CREATE TRIGGER trg_rooms_touch BEFORE UPDATE ON rooms
      FOR EACH ROW EXECUTE FUNCTION touch_updated_at();
  END IF;
END $$;

DO $$ BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_beds_touch') THEN
    CREATE TRIGGER trg_beds_touch BEFORE UPDATE ON beds
      FOR EACH ROW EXECUTE FUNCTION touch_updated_at();
  END IF;
END $$;

DO $$ BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_tenants_touch') THEN
    CREATE TRIGGER trg_tenants_touch BEFORE UPDATE ON tenants
      FOR EACH ROW EXECUTE FUNCTION touch_updated_at();
  END IF;
END $$;

DO $$ BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'trg_bills_touch') THEN
    CREATE TRIGGER trg_bills_touch BEFORE UPDATE ON bills
      FOR EACH ROW EXECUTE FUNCTION touch_updated_at();
  END IF;
END $$;

-- View: room occupancy
CREATE OR REPLACE VIEW v_room_occupancy AS
SELECT r.id AS room_id, r.room_number, r.capacity,
       COUNT(b.*) FILTER (WHERE b.status='OCCUPIED') AS occupied,
       COUNT(b.*) FILTER (WHERE b.status='AVAILABLE') AS available
FROM rooms r
JOIN beds b ON b.room_id = r.id
GROUP BY r.id;