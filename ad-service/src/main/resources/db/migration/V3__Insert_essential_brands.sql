-- V3__Insert_essential_brands.sql
-- Essential brand data for all categories

-- =============================================================================
-- MOTOR BOAT BRANDS (Top 50 most popular)
-- =============================================================================
INSERT INTO brands (name, category, display_order) VALUES
-- A-brands (most popular)
('Azimut', 'MOTOR_BOATS', 1),
('Absolute', 'MOTOR_BOATS', 2),
('Aquila', 'MOTOR_BOATS', 3),
('Avalon', 'MOTOR_BOATS', 4),
('Axopar', 'MOTOR_BOATS', 5),

-- B-brands (most popular)
('Boston Whaler', 'MOTOR_BOATS', 10),
('Bayliner', 'MOTOR_BOATS', 11),
('Beneteau', 'MOTOR_BOATS', 12),
('Bavaria', 'MOTOR_BOATS', 13),
('Bennington', 'MOTOR_BOATS', 14),
('Bertram', 'MOTOR_BOATS', 15),

-- C-brands (most popular)
('Chaparral', 'MOTOR_BOATS', 20),
('Cobalt', 'MOTOR_BOATS', 21),
('Chris-Craft', 'MOTOR_BOATS', 22),
('Carver', 'MOTOR_BOATS', 23),
('Cranchi', 'MOTOR_BOATS', 24),
('Crestliner', 'MOTOR_BOATS', 25),

-- D-brands
('De Antonio Yachts', 'MOTOR_BOATS', 30),
('Donzi', 'MOTOR_BOATS', 31),

-- F-brands (most popular)
('Four Winns', 'MOTOR_BOATS', 40),
('Formula', 'MOTOR_BOATS', 41),
('Fairline', 'MOTOR_BOATS', 42),
('Ferretti Yachts', 'MOTOR_BOATS', 43),

-- G-brands
('Grady-White', 'MOTOR_BOATS', 50),
('Galeon', 'MOTOR_BOATS', 51),

-- H-brands
('Harris', 'MOTOR_BOATS', 60),
('Hurricane', 'MOTOR_BOATS', 61),
('Hatteras', 'MOTOR_BOATS', 62),

-- J-brands
('Jeanneau', 'MOTOR_BOATS', 70),
('Jupiter', 'MOTOR_BOATS', 71),

-- L-brands
('Larson', 'MOTOR_BOATS', 80),
('Lund', 'MOTOR_BOATS', 81),

-- M-brands (most popular)
('MasterCraft', 'MOTOR_BOATS', 90),
('Mako', 'MOTOR_BOATS', 91),
('Malibu', 'MOTOR_BOATS', 92),
('Monterey', 'MOTOR_BOATS', 93),

-- P-brands
('Princess', 'MOTOR_BOATS', 100),
('Prestige', 'MOTOR_BOATS', 101),
('Pro-Line', 'MOTOR_BOATS', 102),

-- R-brands
('Regal', 'MOTOR_BOATS', 110),
('Robalo', 'MOTOR_BOATS', 111),
('Riva', 'MOTOR_BOATS', 112),

-- S-brands (most popular)
('Sea Ray', 'MOTOR_BOATS', 120),
('Sea Hunt', 'MOTOR_BOATS', 121),
('Sunseeker', 'MOTOR_BOATS', 122),
('Scout', 'MOTOR_BOATS', 123),

-- W-brands
('Wellcraft', 'MOTOR_BOATS', 130),

-- Y-brands
('Yamaha Boats', 'MOTOR_BOATS', 140),

-- Custom option
('Собствено производство / Други', 'MOTOR_BOATS', 9999)
ON CONFLICT (name, category) DO NOTHING;

-- =============================================================================
-- SAILBOAT BRANDS (Top 30 most popular)
-- =============================================================================
INSERT INTO brands (name, category, display_order) VALUES
-- Popular sailboat brands
('Beneteau', 'SAILBOATS', 1),
('Bavaria', 'SAILBOATS', 2),
('Jeanneau', 'SAILBOATS', 3),
('Catalina', 'SAILBOATS', 4),
('Hunter', 'SAILBOATS', 5),
('Hanse', 'SAILBOATS', 6),
('Dufour', 'SAILBOATS', 7),
('Elan', 'SAILBOATS', 8),
('Lagoon', 'SAILBOATS', 9),
('Fountaine Pajot', 'SAILBOATS', 10),
('Dehler', 'SAILBOATS', 11),
('Hallberg-Rassy', 'SAILBOATS', 12),
('Oyster', 'SAILBOATS', 13),
('Contest', 'SAILBOATS', 14),
('X-Yachts', 'SAILBOATS', 15),
('Leopard', 'SAILBOATS', 16),
('Grand Soleil', 'SAILBOATS', 17),
('Moody', 'SAILBOATS', 18),
('Najad', 'SAILBOATS', 19),
('Nautor Swan', 'SAILBOATS', 20),
('Pearson', 'SAILBOATS', 21),
('C&C', 'SAILBOATS', 22),
('J Boats', 'SAILBOATS', 23),
('Sunbeam', 'SAILBOATS', 24),
('Solaris', 'SAILBOATS', 25),

-- Custom option
('Собствено производство / Други', 'SAILBOATS', 9999)
ON CONFLICT (name, category) DO NOTHING;

-- =============================================================================
-- KAYAK/CANOE BRANDS
-- =============================================================================
INSERT INTO brands (name, category, display_order) VALUES
('Hobie', 'KAYAKS', 1),
('Ocean Kayak', 'KAYAKS', 2),
('Old Town', 'KAYAKS', 3),
('Native Watercraft', 'KAYAKS', 4),
('Aqua Marina', 'KAYAKS', 5),
('Tracker', 'KAYAKS', 6),
('Sea Ray', 'KAYAKS', 7),
('AB Inflatables', 'KAYAKS', 8),
('Capelli', 'KAYAKS', 9),
('Highfield', 'KAYAKS', 10),

-- Custom option
('Собствено производство / Други', 'KAYAKS', 9999)
ON CONFLICT (name, category) DO NOTHING;

-- =============================================================================
-- JET SKI BRANDS
-- =============================================================================
INSERT INTO brands (name, category, display_order) VALUES
('Sea-Doo (BRP)', 'JET_SKIS', 1),
('Yamaha WaveRunner', 'JET_SKIS', 2),
('Kawasaki Jet Ski', 'JET_SKIS', 3),
('Krash Industries', 'JET_SKIS', 4),
('Belassi', 'JET_SKIS', 5),
('Hison', 'JET_SKIS', 6),
('Taiga Motors (Electric PWC)', 'JET_SKIS', 7),

-- Custom option
('Собствено производство / Други', 'JET_SKIS', 9999)
ON CONFLICT (name, category) DO NOTHING;

-- =============================================================================
-- TRAILER BRANDS
-- =============================================================================
INSERT INTO brands (name, category, display_order) VALUES
('RESPO', 'TRAILERS', 1),
('TRIGANO', 'TRAILERS', 2),
('BRENDERUP', 'TRAILERS', 3),
('VENITRAILERS', 'TRAILERS', 4),
('THOMAS', 'TRAILERS', 5),
('NIEWIADOW', 'TRAILERS', 6),
('LORRIES', 'TRAILERS', 7),

-- Custom option
('Собствено производство / Други', 'TRAILERS', 9999)
ON CONFLICT (name, category) DO NOTHING;

-- =============================================================================
-- MARINE ELECTRONICS BRANDS (Top brands)
-- =============================================================================
INSERT INTO brands (name, category, display_order) VALUES
-- Sonar/GPS brands
('Lowrance', 'MARINE_ELECTRONICS', 1),
('Garmin', 'MARINE_ELECTRONICS', 2),
('Humminbird', 'MARINE_ELECTRONICS', 3),
('Furuno', 'MARINE_ELECTRONICS', 4),
('Simrad', 'MARINE_ELECTRONICS', 5),
('Raymarine', 'MARINE_ELECTRONICS', 6),
('B&G', 'MARINE_ELECTRONICS', 7),

-- Trolling motor brands
('Minn Kota', 'MARINE_ELECTRONICS', 10),
('Motorguide', 'MARINE_ELECTRONICS', 11),

-- Battery brands
('Optima Batteries', 'MARINE_ELECTRONICS', 15),
('Mastervolt', 'MARINE_ELECTRONICS', 16),

-- VHF/AIS brands
('ICOM', 'MARINE_ELECTRONICS', 20),
('Entel', 'MARINE_ELECTRONICS', 21),

-- Custom option
('Собствено производство / Други', 'MARINE_ELECTRONICS', 9999)
ON CONFLICT (name, category) DO NOTHING;

-- =============================================================================
-- ENGINE BRANDS (Most popular)
-- =============================================================================
INSERT INTO brands (name, category, display_order) VALUES
-- Outboard engines
('Mercury Marine', 'ENGINES', 1),
('Yamaha Marine', 'ENGINES', 2),
('Suzuki Marine', 'ENGINES', 3),
('Honda Marine', 'ENGINES', 4),
('Tohatsu', 'ENGINES', 5),
('Evinrude', 'ENGINES', 6),

-- Inboard engines
('Volvo Penta', 'ENGINES', 10),
('MerCruiser', 'ENGINES', 11),
('Yanmar Marine', 'ENGINES', 12),
('Caterpillar Marine', 'ENGINES', 13),
('Cummins Marine', 'ENGINES', 14),

-- Custom option
('Собствено производство / Други', 'ENGINES', 9999)
ON CONFLICT (name, category) DO NOTHING;

-- =============================================================================
-- FISHING BRANDS
-- =============================================================================
INSERT INTO brands (name, category, display_order) VALUES
('Shimano', 'FISHING', 1),
('Daiwa', 'FISHING', 2),
('Penn', 'FISHING', 3),
('Abu Garcia', 'FISHING', 4),
('Okuma', 'FISHING', 5),
('Rapala', 'FISHING', 6),
('Berkley', 'FISHING', 7),
('Spro', 'FISHING', 8),
('Savage Gear', 'FISHING', 9),
('Fox', 'FISHING', 10),

-- Custom option
('Собствено производство / Други', 'FISHING', 9999)
ON CONFLICT (name, category) DO NOTHING;

-- =============================================================================
-- WATER SPORTS BRANDS
-- =============================================================================
INSERT INTO brands (name, category, display_order) VALUES
('Aqua Marina', 'WATER_SPORTS', 1),
('Red Paddle Co', 'WATER_SPORTS', 2),
('BOTE', 'WATER_SPORTS', 3),
('Fanatic', 'WATER_SPORTS', 4),
('Starboard', 'WATER_SPORTS', 5),
('Naish', 'WATER_SPORTS', 6),
('O''Neill', 'WATER_SPORTS', 7),
('Rip Curl', 'WATER_SPORTS', 8),
('Billabong', 'WATER_SPORTS', 9),
('Quiksilver', 'WATER_SPORTS', 10),

-- Custom option
('Собствено производство / Други', 'WATER_SPORTS', 9999)
ON CONFLICT (name, category) DO NOTHING;

-- =============================================================================
-- MARINE ACCESSORIES BRANDS
-- =============================================================================
INSERT INTO brands (name, category, display_order) VALUES
('Plastimo', 'MARINE_ACCESSORIES', 1),
('Osculati', 'MARINE_ACCESSORIES', 2),
('Lalizas', 'MARINE_ACCESSORIES', 3),
('Attwood', 'MARINE_ACCESSORIES', 4),
('Vetus', 'MARINE_ACCESSORIES', 5),
('Lewmar', 'MARINE_ACCESSORIES', 6),
('Harken', 'MARINE_ACCESSORIES', 7),
('Jabsco', 'MARINE_ACCESSORIES', 8),

-- Custom option
('Собствено производство / Други', 'MARINE_ACCESSORIES', 9999)
ON CONFLICT (name, category) DO NOTHING;

-- =============================================================================
-- PARTS BRANDS
-- =============================================================================
INSERT INTO brands (name, category, display_order) VALUES
('Mercury', 'PARTS', 1),
('Yamaha', 'PARTS', 2),
('Suzuki', 'PARTS', 3),
('Honda', 'PARTS', 4),
('Volvo Penta', 'PARTS', 5),
('Quicksilver', 'PARTS', 6),

-- Custom option
('Собствено производство / Други', 'PARTS', 9999)
ON CONFLICT (name, category) DO NOTHING;