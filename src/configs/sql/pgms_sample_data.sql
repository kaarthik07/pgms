SET search_path TO pgms, public;

-- Org
INSERT INTO organizations (id, name, code, logo_url, primary_color, secondary_color)
VALUES ('00000000-0000-0000-0000-000000000001','V2 CoLive','V2-COLIVE',
        'https://cdn.example.com/v2/logo.png','#5B8DEF','#111827')
ON CONFLICT (code) DO NOTHING;

-- Users (password hashes are placeholders)
INSERT INTO users (id, org_id, role, email, phone, login, password_hash, force_pwd_change, is_active)
VALUES ('00000000-0000-0000-0000-00000000A001', NULL, 'ADMIN', 'admin@pgms.local', NULL, 'admin@pgms.local', '$2a$10$changeme', FALSE, TRUE)
ON CONFLICT (login) DO NOTHING;

INSERT INTO users (id, org_id, role, email, phone, login, password_hash, force_pwd_change, is_active)
VALUES ('00000000-0000-0000-0000-00000000O001','00000000-0000-0000-0000-000000000001','OWNER','owner1@v2colive.local',
        '+919999888877','owner1@v2colive.local','$2a$10$changeme', TRUE, TRUE)
ON CONFLICT (login) DO NOTHING;

-- Rooms & beds
INSERT INTO rooms (id, org_id, room_number, floor, capacity, base_rent)
VALUES ('00000000-0000-0000-0000-00000000R101','00000000-0000-0000-0000-000000000001','101',1,3,8500.00)
ON CONFLICT (org_id, room_number) DO NOTHING;

INSERT INTO beds (id, room_id, bed_index, status) VALUES
 ('00000000-0000-0000-0000-00000000B101','00000000-0000-0000-0000-00000000R101',1,'AVAILABLE')
ON CONFLICT DO NOTHING;
INSERT INTO beds (id, room_id, bed_index, status) VALUES
 ('00000000-0000-0000-0000-00000000B102','00000000-0000-0000-0000-00000000R101',2,'AVAILABLE')
ON CONFLICT DO NOTHING;
INSERT INTO beds (id, room_id, bed_index, status) VALUES
 ('00000000-0000-0000-0000-00000000B103','00000000-0000-0000-0000-00000000R101',3,'AVAILABLE')
ON CONFLICT DO NOTHING;

-- Tenant + proofs
INSERT INTO tenants (id, org_id, full_name, phone, email, status, rent_amount, bed_id)
VALUES ('00000000-0000-0000-0000-00000000T001','00000000-0000-0000-0000-000000000001',
        'Karthik Chinni','+919876543210','karthik@example.com','ACTIVE',9500.00,'00000000-0000-0000-0000-00000000B101')
ON CONFLICT (id) DO NOTHING;

UPDATE beds SET status='OCCUPIED' WHERE id='00000000-0000-0000-0000-00000000B101';

INSERT INTO tenant_id_proofs (tenant_id, proof_type, proof_value, file_url)
VALUES ('00000000-0000-0000-0000-00000000T001','AADHAAR','1234-5678-9123','https://files.example.com/aadhaar.pdf')
ON CONFLICT DO NOTHING;

-- Bill (current month)
INSERT INTO bills (tenant_id, month, amount, status, notes)
VALUES ('00000000-0000-0000-0000-00000000T001', to_char(CURRENT_DATE,'YYYY-MM'), 9500.00, 'PENDING','Base rent')
ON CONFLICT (tenant_id, month) DO NOTHING;

-- Notice
INSERT INTO notices (tenant_id, start_date, requested_move_out, status)
VALUES ('00000000-0000-0000-0000-00000000T001', CURRENT_DATE, CURRENT_DATE + INTERVAL '30 days', 'PENDING')
ON CONFLICT DO NOTHING;

-- Dues
INSERT INTO dues (tenant_id, org_id, amount, reason, status)
VALUES ('00000000-0000-0000-0000-00000000T001','00000000-0000-0000-0000-000000000001',1500.00,'Unpaid utilities','OPEN')
ON CONFLICT DO NOTHING;

-- Wallet + referral
INSERT INTO wallets (owner_id, balance, currency)
VALUES ('00000000-0000-0000-0000-00000000O001', 0, 'INR')
ON CONFLICT (owner_id) DO NOTHING;

INSERT INTO referrals (referrer_owner_id, referred_phone, status)
VALUES ('00000000-0000-0000-0000-00000000O001', '+919000112233','PENDING')
ON CONFLICT DO NOTHING;

-- Payments placeholder: attach to created bill
INSERT INTO payments (bill_id, tenant_id, razorpay_order_id, amount_paise, currency, status, raw_payload)
SELECT b.id, '00000000-0000-0000-0000-00000000T001', 'order_demo_1', 950000, 'INR', 'created', '{"demo":true}'::jsonb
FROM bills b
WHERE b.tenant_id='00000000-0000-0000-0000-00000000T001'
  AND b.month = to_char(CURRENT_DATE, 'YYYY-MM')
ON CONFLICT DO NOTHING;