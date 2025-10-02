
-- Sample Dataset for PGMS (PostgreSQL)

-- Insert Admin User
INSERT INTO users (role, email, phone, password_hash, status)
VALUES ('ADMIN', 'admin@pgms.com', '9999999999', 'hashed_admin_pw', 'ACTIVE');

-- Insert PG Owner User
INSERT INTO users (role, email, phone, password_hash, status)
VALUES ('OWNER', 'owner@pgms.com', '8888888888', 'hashed_owner_pw', 'ACTIVE');

-- Insert Tenant User
INSERT INTO users (role, email, phone, password_hash, status)
VALUES ('TENANT', 'tenant1@pgms.com', '7777777777', 'hashed_tenant_pw', 'ACTIVE');

-- Insert PG
INSERT INTO pgs (owner_id, name, code, address, logo_url, subscription_status)
VALUES (2, 'V2-Colive', 'V2COLIVE', 'Electronic City, Bangalore', 'http://logo.url/v2.png', 'ACTIVE');

-- Insert Rooms
INSERT INTO rooms (pg_id, room_number, floor_number, capacity, type, price)
VALUES (1, '101', 1, 2, 'DOUBLE', 12000.00),
       (1, '102', 1, 3, 'TRIPLE', 15000.00);

-- Insert Beds
INSERT INTO beds (room_id, bed_number, is_occupied)
VALUES (1, '101A', FALSE),
       (1, '101B', FALSE),
       (2, '102A', FALSE),
       (2, '102B', FALSE),
       (2, '102C', FALSE);

-- Insert Tenant
INSERT INTO tenants (user_id, bed_id, name, phone, father_phone, vehicle_number, id_proofs, status)
VALUES (3, 1, 'Ramesh Kumar', '7777777777', '6666666666', 'KA05AB1234',
        '[{"type":"AADHAAR","value":"XXXX-YYYY-ZZZZ"},{"type":"PAN","value":"ABCDE1234F"}]', 'ACTIVE');

-- Insert Bill
INSERT INTO bills (tenant_id, amount, due_date, status)
VALUES (1, 12000.00, '2025-10-05', 'PENDING');

-- Insert Receipt
INSERT INTO receipts (bill_id, paid_amount, payment_method)
VALUES (1, 12000.00, 'Cash');

-- Insert Notice
INSERT INTO notices (tenant_id, requested_date, status, early_exit_fee)
VALUES (1, '2025-11-01', 'PENDING', 2000.00);

-- Insert Dues
INSERT INTO dues_registry (tenant_id, pg_id, amount, reason, status)
VALUES (1, 1, 5000.00, 'Left without paying full rent', 'UNRESOLVED');

-- Insert Referral
INSERT INTO referrals (referrer_owner_id, bonus_amount, status)
VALUES (2, 150.00, 'APPROVED');

-- Insert Wallet
INSERT INTO wallets (owner_id, balance)
VALUES (2, 150.00);

-- Insert Reminder
INSERT INTO reminders (tenant_id, message, status)
VALUES (1, 'Rent due reminder for October 2025', 'PENDING');

-- Insert Payment (Razorpay)
INSERT INTO payments (tenant_id, order_id, payment_status, amount, method)
VALUES (1, 'order_123456789', 'SUCCESS', 12000.00, 'Razorpay-UPI');
