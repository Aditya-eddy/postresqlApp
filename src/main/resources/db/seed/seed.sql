-- Seed data for card_account_transactions.
-- Idempotent: deletes rows for the seeded accounts before reinserting.
-- Run manually (not part of the Postgres docker-entrypoint init).
--
-- Local run:
--   kubectl -n travelcard exec -i postgres-0 -- \
--     psql -U agoda_wallet -d agoda_wallet < src/main/resources/db/seed/seed.sql

BEGIN;

DELETE FROM card_account_transactions
 WHERE account_id IN ('acct_123', 'acct_456', 'acct_booking_001');

INSERT INTO card_account_transactions
    (account_id, card_id, whitelabel, type, status, amount_minor, currency, merchant_name, wise_reference, created_at)
VALUES
    -- acct_123 / AGODA — broad mix, most recent first
    ('acct_123', 'card_abc', 'AGODA', 'AUTH',    'COMPLETED', 12500,  'USD', 'Marriott Hotels',         'WR-20260420-000001', now() - interval '1 hour'),
    ('acct_123', 'card_abc', 'AGODA', 'CAPTURE', 'COMPLETED', 12500,  'USD', 'Marriott Hotels',         'WR-20260420-000002', now() - interval '50 minutes'),
    ('acct_123', 'card_abc', 'AGODA', 'AUTH',    'PENDING',    4599,  'USD', 'Uber',                    'WR-20260420-000003', now() - interval '3 hours'),
    ('acct_123', 'card_abc', 'AGODA', 'AUTH',    'DECLINED',  29900,  'USD', 'Airbnb',                   NULL,                now() - interval '5 hours'),
    ('acct_123', 'card_abc', 'AGODA', 'CAPTURE', 'COMPLETED', 18900,  'GBP', 'British Airways',         'WR-20260419-000014', now() - interval '1 day'),
    ('acct_123', 'card_abc', 'AGODA', 'REFUND',  'COMPLETED', -5900,  'GBP', 'British Airways',         'WR-20260419-000015', now() - interval '1 day 1 hour'),
    ('acct_123', 'card_abc', 'AGODA', 'FEE',     'COMPLETED',   150,  'GBP', 'FX conversion fee',        NULL,                now() - interval '1 day 2 hours'),
    ('acct_123', 'card_abc', 'AGODA', 'AUTH',    'COMPLETED',  7425,  'EUR', 'Deutsche Bahn',           'WR-20260418-000022', now() - interval '2 days'),
    ('acct_123', 'card_abc', 'AGODA', 'CAPTURE', 'COMPLETED',  7425,  'EUR', 'Deutsche Bahn',           'WR-20260418-000023', now() - interval '2 days 1 hour'),
    ('acct_123', 'card_abc', 'AGODA', 'AUTH',    'COMPLETED',  3299,  'EUR', 'Lidl',                    'WR-20260418-000030', now() - interval '2 days 4 hours'),
    ('acct_123', 'card_abc', 'AGODA', 'CAPTURE', 'COMPLETED',  3299,  'EUR', 'Lidl',                    'WR-20260418-000031', now() - interval '2 days 5 hours'),
    ('acct_123', 'card_abc', 'AGODA', 'AUTH',    'DECLINED',  99900,  'USD', 'Tesla Inc',                NULL,                now() - interval '3 days'),
    ('acct_123', 'card_abc', 'AGODA', 'AUTH',    'COMPLETED',  2100,  'JPY', 'JR East',                 'WR-20260417-000040', now() - interval '3 days 6 hours'),
    ('acct_123', 'card_abc', 'AGODA', 'CAPTURE', 'COMPLETED',  2100,  'JPY', 'JR East',                 'WR-20260417-000041', now() - interval '3 days 7 hours'),
    ('acct_123', 'card_abc', 'AGODA', 'FEE',     'COMPLETED',    25,  'JPY', 'ATM withdrawal fee',       NULL,                now() - interval '3 days 8 hours'),
    ('acct_123', 'card_abc', 'AGODA', 'AUTH',    'COMPLETED', 15000,  'SGD', 'Grab',                    'WR-20260416-000050', now() - interval '4 days'),
    ('acct_123', 'card_abc', 'AGODA', 'CAPTURE', 'COMPLETED', 15000,  'SGD', 'Grab',                    'WR-20260416-000051', now() - interval '4 days 1 hour'),
    ('acct_123', 'card_abc', 'AGODA', 'AUTH',    'COMPLETED',  8800,  'SGD', 'Starbucks',               'WR-20260416-000060', now() - interval '4 days 3 hours'),
    ('acct_123', 'card_abc', 'AGODA', 'CAPTURE', 'COMPLETED',  8800,  'SGD', 'Starbucks',               'WR-20260416-000061', now() - interval '4 days 4 hours'),
    ('acct_123', 'card_xyz', 'AGODA', 'AUTH',    'PENDING',   45000,  'USD', 'United Airlines',          NULL,                now() - interval '5 days'),
    ('acct_123', 'card_xyz', 'AGODA', 'AUTH',    'COMPLETED', 20500,  'USD', 'Hilton Hotels',           'WR-20260415-000070', now() - interval '5 days 4 hours'),
    ('acct_123', 'card_xyz', 'AGODA', 'CAPTURE', 'COMPLETED', 20500,  'USD', 'Hilton Hotels',           'WR-20260415-000071', now() - interval '5 days 5 hours'),
    ('acct_123', 'card_xyz', 'AGODA', 'REFUND',  'COMPLETED',-10250,  'USD', 'Hilton Hotels',           'WR-20260415-000072', now() - interval '5 days 6 hours'),
    ('acct_123', 'card_xyz', 'AGODA', 'AUTH',    'COMPLETED',  6499,  'USD', 'Amazon',                  'WR-20260414-000080', now() - interval '6 days'),
    ('acct_123', 'card_xyz', 'AGODA', 'CAPTURE', 'COMPLETED',  6499,  'USD', 'Amazon',                  'WR-20260414-000081', now() - interval '6 days 1 hour'),
    ('acct_123', 'card_xyz', 'AGODA', 'AUTH',    'COMPLETED',  3250,  'USD', 'Spotify',                 'WR-20260413-000090', now() - interval '7 days'),
    ('acct_123', 'card_xyz', 'AGODA', 'CAPTURE', 'COMPLETED',  3250,  'USD', 'Spotify',                 'WR-20260413-000091', now() - interval '7 days 1 hour'),
    ('acct_123', 'card_xyz', 'AGODA', 'AUTH',    'COMPLETED', 12999,  'USD', 'Apple Store',             'WR-20260412-000100', now() - interval '8 days'),
    ('acct_123', 'card_xyz', 'AGODA', 'CAPTURE', 'COMPLETED', 12999,  'USD', 'Apple Store',             'WR-20260412-000101', now() - interval '8 days 1 hour'),
    ('acct_123', 'card_xyz', 'AGODA', 'FEE',     'COMPLETED',   200,  'USD', 'Monthly card fee',         NULL,                now() - interval '9 days'),
    ('acct_123', 'card_xyz', 'AGODA', 'AUTH',    'COMPLETED', 34000,  'USD', 'Qatar Airways',           'WR-20260411-000110', now() - interval '10 days'),
    ('acct_123', 'card_xyz', 'AGODA', 'CAPTURE', 'COMPLETED', 34000,  'USD', 'Qatar Airways',           'WR-20260411-000111', now() - interval '10 days 1 hour'),
    ('acct_123', 'card_xyz', 'AGODA', 'AUTH',    'DECLINED',   4599,  'USD', 'Uber Eats',                NULL,                now() - interval '11 days'),
    ('acct_123', 'card_xyz', 'AGODA', 'AUTH',    'COMPLETED',  1899,  'USD', 'Netflix',                 'WR-20260409-000120', now() - interval '12 days'),
    ('acct_123', 'card_xyz', 'AGODA', 'CAPTURE', 'COMPLETED',  1899,  'USD', 'Netflix',                 'WR-20260409-000121', now() - interval '12 days 1 hour'),
    ('acct_123', 'card_xyz', 'AGODA', 'AUTH',    'COMPLETED',  5999,  'USD', 'Uber',                    'WR-20260408-000130', now() - interval '13 days'),
    ('acct_123', 'card_xyz', 'AGODA', 'CAPTURE', 'COMPLETED',  5999,  'USD', 'Uber',                    'WR-20260408-000131', now() - interval '13 days 1 hour'),
    ('acct_123', 'card_xyz', 'AGODA', 'AUTH',    'COMPLETED', 22500,  'USD', 'Delta Air Lines',         'WR-20260407-000140', now() - interval '14 days'),
    ('acct_123', 'card_xyz', 'AGODA', 'CAPTURE', 'COMPLETED', 22500,  'USD', 'Delta Air Lines',         'WR-20260407-000141', now() - interval '14 days 1 hour'),
    ('acct_123', 'card_xyz', 'AGODA', 'REFUND',  'PENDING',  -22500,  'USD', 'Delta Air Lines',          NULL,                now() - interval '14 days 3 hours'),
    ('acct_123', 'card_xyz', 'AGODA', 'AUTH',    'COMPLETED',  7750,  'USD', 'Shell',                   'WR-20260406-000150', now() - interval '15 days'),
    ('acct_123', 'card_xyz', 'AGODA', 'CAPTURE', 'COMPLETED',  7750,  'USD', 'Shell',                   'WR-20260406-000151', now() - interval '15 days 1 hour'),
    ('acct_123', 'card_xyz', 'AGODA', 'AUTH',    'COMPLETED',  4250,  'USD', 'Whole Foods',             'WR-20260405-000160', now() - interval '16 days'),
    ('acct_123', 'card_xyz', 'AGODA', 'CAPTURE', 'COMPLETED',  4250,  'USD', 'Whole Foods',             'WR-20260405-000161', now() - interval '16 days 1 hour'),
    ('acct_123', 'card_xyz', 'AGODA', 'AUTH',    'COMPLETED', 11500,  'USD', 'IKEA',                    'WR-20260404-000170', now() - interval '17 days'),
    ('acct_123', 'card_xyz', 'AGODA', 'CAPTURE', 'COMPLETED', 11500,  'USD', 'IKEA',                    'WR-20260404-000171', now() - interval '17 days 1 hour'),
    ('acct_123', 'card_xyz', 'AGODA', 'AUTH',    'COMPLETED',  9999,  'USD', 'Best Buy',                'WR-20260403-000180', now() - interval '18 days'),
    ('acct_123', 'card_xyz', 'AGODA', 'CAPTURE', 'COMPLETED',  9999,  'USD', 'Best Buy',                'WR-20260403-000181', now() - interval '18 days 1 hour'),
    ('acct_123', 'card_xyz', 'AGODA', 'AUTH',    'COMPLETED', 16500,  'USD', 'The Home Depot',          'WR-20260402-000190', now() - interval '19 days'),
    ('acct_123', 'card_xyz', 'AGODA', 'CAPTURE', 'COMPLETED', 16500,  'USD', 'The Home Depot',          'WR-20260402-000191', now() - interval '19 days 1 hour'),
    ('acct_123', 'card_xyz', 'AGODA', 'FEE',     'COMPLETED',   500,  'USD', 'Foreign transaction fee',  NULL,                now() - interval '20 days'),
    ('acct_123', 'card_xyz', 'AGODA', 'AUTH',    'COMPLETED', 28000,  'USD', 'Four Seasons',            'WR-20260401-000200', now() - interval '21 days'),
    ('acct_123', 'card_xyz', 'AGODA', 'CAPTURE', 'COMPLETED', 28000,  'USD', 'Four Seasons',            'WR-20260401-000201', now() - interval '21 days 1 hour'),
    ('acct_123', 'card_xyz', 'AGODA', 'AUTH',    'COMPLETED',  3199,  'USD', 'CVS Pharmacy',            'WR-20260331-000210', now() - interval '22 days'),
    ('acct_123', 'card_xyz', 'AGODA', 'CAPTURE', 'COMPLETED',  3199,  'USD', 'CVS Pharmacy',            'WR-20260331-000211', now() - interval '22 days 1 hour'),
    ('acct_123', 'card_xyz', 'AGODA', 'AUTH',    'COMPLETED', 49500,  'USD', 'Singapore Airlines',      'WR-20260330-000220', now() - interval '23 days'),
    ('acct_123', 'card_xyz', 'AGODA', 'CAPTURE', 'COMPLETED', 49500,  'USD', 'Singapore Airlines',      'WR-20260330-000221', now() - interval '23 days 1 hour'),
    ('acct_123',  NULL,      'AGODA', 'AUTH',    'COMPLETED',  2500,  'USD', 'ATM — Chase',             'WR-20260329-000230', now() - interval '24 days'),
    ('acct_123',  NULL,      'AGODA', 'FEE',     'COMPLETED',   300,  'USD', 'ATM withdrawal fee',       NULL,                now() - interval '24 days'),
    ('acct_123', 'card_xyz', 'AGODA', 'AUTH',    'COMPLETED',  6250,  'USD', 'Trader Joe''s',           'WR-20260328-000240', now() - interval '25 days'),
    ('acct_123', 'card_xyz', 'AGODA', 'CAPTURE', 'COMPLETED',  6250,  'USD', 'Trader Joe''s',           'WR-20260328-000241', now() - interval '25 days 1 hour'),

    -- acct_456 / AGODA — smaller set
    ('acct_456', 'card_def', 'AGODA', 'AUTH',    'COMPLETED', 19900,  'USD', 'Marriott Hotels',         'WR-20260420-000301', now() - interval '2 hours'),
    ('acct_456', 'card_def', 'AGODA', 'CAPTURE', 'COMPLETED', 19900,  'USD', 'Marriott Hotels',         'WR-20260420-000302', now() - interval '3 hours'),
    ('acct_456', 'card_def', 'AGODA', 'AUTH',    'PENDING',   13500,  'EUR', 'Booking.com',              NULL,                now() - interval '6 hours'),
    ('acct_456', 'card_def', 'AGODA', 'AUTH',    'COMPLETED',  9500,  'USD', 'Starbucks',               'WR-20260419-000310', now() - interval '1 day'),
    ('acct_456', 'card_def', 'AGODA', 'CAPTURE', 'COMPLETED',  9500,  'USD', 'Starbucks',               'WR-20260419-000311', now() - interval '1 day 1 hour'),
    ('acct_456', 'card_def', 'AGODA', 'AUTH',    'DECLINED',  85000,  'USD', 'Rolex Boutique',           NULL,                now() - interval '2 days'),
    ('acct_456', 'card_def', 'AGODA', 'AUTH',    'COMPLETED',  4200,  'GBP', 'Pret A Manger',           'WR-20260418-000320', now() - interval '3 days'),
    ('acct_456', 'card_def', 'AGODA', 'CAPTURE', 'COMPLETED',  4200,  'GBP', 'Pret A Manger',           'WR-20260418-000321', now() - interval '3 days 1 hour'),
    ('acct_456', 'card_def', 'AGODA', 'REFUND',  'COMPLETED', -4200,  'GBP', 'Pret A Manger',           'WR-20260418-000322', now() - interval '3 days 2 hours'),
    ('acct_456', 'card_def', 'AGODA', 'FEE',     'COMPLETED',   100,  'USD', 'Monthly card fee',         NULL,                now() - interval '4 days'),
    ('acct_456', 'card_def', 'AGODA', 'AUTH',    'COMPLETED', 27500,  'USD', 'Emirates',                'WR-20260415-000330', now() - interval '5 days'),
    ('acct_456', 'card_def', 'AGODA', 'CAPTURE', 'COMPLETED', 27500,  'USD', 'Emirates',                'WR-20260415-000331', now() - interval '5 days 1 hour'),
    ('acct_456', 'card_def', 'AGODA', 'AUTH',    'COMPLETED',  2199,  'SGD', 'McDonald''s',             'WR-20260414-000340', now() - interval '6 days'),
    ('acct_456', 'card_def', 'AGODA', 'CAPTURE', 'COMPLETED',  2199,  'SGD', 'McDonald''s',             'WR-20260414-000341', now() - interval '6 days 1 hour'),
    ('acct_456', 'card_def', 'AGODA', 'AUTH',    'COMPLETED', 15900,  'USD', 'Hyatt Regency',           'WR-20260413-000350', now() - interval '7 days'),
    ('acct_456', 'card_def', 'AGODA', 'CAPTURE', 'COMPLETED', 15900,  'USD', 'Hyatt Regency',           'WR-20260413-000351', now() - interval '7 days 1 hour'),

    -- Different whitelabel — proves X-Agoda-Whitelabel filter is honored
    ('acct_booking_001', 'card_bkg', 'BOOKING', 'AUTH',    'COMPLETED', 22000, 'EUR', 'Booking.com',    'WR-20260420-000401', now() - interval '2 hours'),
    ('acct_booking_001', 'card_bkg', 'BOOKING', 'CAPTURE', 'COMPLETED', 22000, 'EUR', 'Booking.com',    'WR-20260420-000402', now() - interval '3 hours'),
    ('acct_booking_001', 'card_bkg', 'BOOKING', 'AUTH',    'PENDING',    8750, 'EUR', 'Ryanair',         NULL,                now() - interval '1 day');

-- Wallets for members 19..23 referenced by the Postman pre-request script.
DELETE FROM wallets WHERE member_id IN ('19', '20', '21', '22', '23');
INSERT INTO wallets (member_id, wallet_id, currency) VALUES
    ('19', 'wlt_000000019', 'THB'),
    ('20', 'wlt_000000020', 'USD'),
    ('21', 'wlt_000000021', 'SGD'),
    ('22', 'wlt_000000022', 'JPY'),
    ('23', 'wlt_000000023', 'EUR');

-- A couple of pre-existing travel cards so POST /card/topup|freeze work out of the box.
DELETE FROM travel_cards WHERE account_id IN ('acct_123', 'acct_456');
INSERT INTO travel_cards (id, account_id, whitelabel, card_type, currency, status, balance_minor, masked_pan) VALUES
    (1, 'acct_123', 'AGODA', 'VIRTUAL',  'THB', 'ACTIVE', 250000, '**** **** **** 4242'),
    (2, 'acct_123', 'AGODA', 'PHYSICAL', 'USD', 'ACTIVE', 150000, '**** **** **** 0007'),
    (3, 'acct_456', 'AGODA', 'VIRTUAL',  'SGD', 'FROZEN',  50000, '**** **** **** 1337');
-- Keep the sequence ahead of the manual ids above.
SELECT setval('travel_cards_id_seq', (SELECT COALESCE(MAX(id), 0) FROM travel_cards));

-- Sample bookings so POST /booking/cancel has refs to target.
DELETE FROM bookings WHERE booking_ref IN ('HTL-SEED0001', 'HTL-SEED0002', 'FLT-SEED0001');
INSERT INTO bookings
    (booking_ref, account_id, whitelabel, booking_type, status, total_amount_minor, currency,
     hotel_name, destination, check_in, check_out, rooms, guests)
VALUES
    ('HTL-SEED0001', 'acct_123', 'AGODA', 'HOTEL', 'CONFIRMED', 450000, 'THB',
     'Grand Hyatt Erawan Bangkok', 'Bangkok, Thailand', DATE '2026-05-15', DATE '2026-05-18', 1, 2),
    ('HTL-SEED0002', 'acct_456', 'AGODA', 'HOTEL', 'CONFIRMED', 15000000, 'JPY',
     'Aman Tokyo', 'Tokyo, Japan', DATE '2026-07-10', DATE '2026-07-12', 1, 1);
INSERT INTO bookings
    (booking_ref, account_id, whitelabel, booking_type, status, total_amount_minor, currency,
     airline, flight_number, origin, flight_destination, departure_date, return_date, passengers, cabin_class)
VALUES
    ('FLT-SEED0001', 'acct_123', 'AGODA', 'FLIGHT', 'CONFIRMED', 3500000, 'THB',
     'Thai Airways', 'TG660', 'BKK', 'NRT', DATE '2026-05-15', DATE '2026-05-22', 2, 'ECONOMY');

-- Sample payment so POST /payment/refund has a ref to refund.
DELETE FROM payments WHERE payment_ref IN ('PAY-SEED0001');
INSERT INTO payments
    (payment_ref, account_id, whitelabel, card_id, booking_ref, amount_minor, currency, payment_method, status)
VALUES
    ('PAY-SEED0001', 'acct_123', 'AGODA', 1, 'HTL-SEED0001', 450000, 'THB', 'TRAVEL_CARD', 'COMPLETED');

COMMIT;
