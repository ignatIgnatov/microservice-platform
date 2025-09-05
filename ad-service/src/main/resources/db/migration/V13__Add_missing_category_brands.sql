-- V13__Add_missing_category_brands.sql
-- Add brands for categories that were missing from V10 migration

-- JET SKI BRANDS (from specification document)
INSERT INTO brands (name, category, display_order) VALUES
-- Sea-Doo (BRP)
('Sea-Doo (BRP)', 'JET_SKIS', 1),
-- Yamaha WaveRunner
('Yamaha WaveRunner', 'JET_SKIS', 2),
-- Kawasaki Jet Ski
('Kawasaki Jet Ski', 'JET_SKIS', 3),
-- Other brands
('Krash Industries', 'JET_SKIS', 4),
('Belassi', 'JET_SKIS', 5),
('Hison', 'JET_SKIS', 6),
('Taiga Motors (Electric PWC)', 'JET_SKIS', 7),
('Собствено производство', 'JET_SKIS', 9999)
ON CONFLICT (name, category) DO NOTHING;

-- TRAILER BRANDS (from specification document)
INSERT INTO brands (name, category, display_order) VALUES
('RESPO', 'TRAILERS', 1),
('TRIGANO', 'TRAILERS', 2),
('BRENDERUP', 'TRAILERS', 3),
('VENITRAILERS', 'TRAILERS', 4),
('THOMAS', 'TRAILERS', 5),
('NIEWIADOW', 'TRAILERS', 6),
('LORRIES', 'TRAILERS', 7),
('Собствено производство', 'TRAILERS', 9999)
ON CONFLICT (name, category) DO NOTHING;

-- MARINE ELECTRONICS BRANDS (from specification document)
INSERT INTO brands (name, category, display_order) VALUES
-- Sonar brands
('Lowrance', 'MARINE_ELECTRONICS', 1),
('Garmin', 'MARINE_ELECTRONICS', 2),
('Humminbird', 'MARINE_ELECTRONICS', 3),
('Furuno', 'MARINE_ELECTRONICS', 4),
('Simrad', 'MARINE_ELECTRONICS', 5),
('B&G', 'MARINE_ELECTRONICS', 6),
('Raymarine', 'MARINE_ELECTRONICS', 7),
('Deeper', 'MARINE_ELECTRONICS', 8),
('Koden', 'MARINE_ELECTRONICS', 9),
('SI-TEX', 'MARINE_ELECTRONICS', 10),
-- Probe brands
('Airmar', 'MARINE_ELECTRONICS', 11),
-- Trolling motor brands
('Motorguide', 'MARINE_ELECTRONICS', 12),
('Minn Kota', 'MARINE_ELECTRONICS', 13),
('PowerPole-Move', 'MARINE_ELECTRONICS', 14),
('Haswing', 'MARINE_ELECTRONICS', 15),
('Ghost', 'MARINE_ELECTRONICS', 16),
('Recon', 'MARINE_ELECTRONICS', 17),
-- Music system brands
('Fusion', 'MARINE_ELECTRONICS', 18),
('Pioneer', 'MARINE_ELECTRONICS', 19),
('JL Audio', 'MARINE_ELECTRONICS', 20),
('Kicker', 'MARINE_ELECTRONICS', 21),
('Wet Sounds', 'MARINE_ELECTRONICS', 22),
('Rockford Fosgate', 'MARINE_ELECTRONICS', 23),
('Alpine Electronics', 'MARINE_ELECTRONICS', 24),
('Kenwood', 'MARINE_ELECTRONICS', 25),
-- Battery brands
('Mastervolt', 'MARINE_ELECTRONICS', 26),
('MarineMaster Deka', 'MARINE_ELECTRONICS', 27),
('Fullriver', 'MARINE_ELECTRONICS', 28),
('Lifeline Batteries', 'MARINE_ELECTRONICS', 29),
('Optima Batteries', 'MARINE_ELECTRONICS', 30),
('Odyssey Battery', 'MARINE_ELECTRONICS', 31),
('NorthStar', 'MARINE_ELECTRONICS', 32),
('RollsBattery', 'MARINE_ELECTRONICS', 33),
('Relion Battery', 'MARINE_ELECTRONICS', 34),
('Total Battery', 'MARINE_ELECTRONICS', 35),
-- AIS/DSC brands
('ICOM', 'MARINE_ELECTRONICS', 36),
('Vesper', 'MARINE_ELECTRONICS', 37),
('Digital Yacht', 'MARINE_ELECTRONICS', 38),
-- VHF brands
('McMurdo', 'MARINE_ELECTRONICS', 39),
('Entel', 'MARINE_ELECTRONICS', 40),
('Sailor', 'MARINE_ELECTRONICS', 41),
-- Cable brands
('Ancor', 'MARINE_ELECTRONICS', 42),
('Marinco', 'MARINE_ELECTRONICS', 43),
('Собствено производство', 'MARINE_ELECTRONICS', 9999)
ON CONFLICT (name, category) DO NOTHING;

-- ENGINE BRANDS (major outboard and inboard engine manufacturers)
INSERT INTO brands (name, category, display_order) VALUES
-- Outboard engine brands
('Mercury Marine', 'ENGINES', 1),
('Suzuki Marine', 'ENGINES', 2),
('Yamaha Marine', 'ENGINES', 3),
('Honda Marine', 'ENGINES', 4),
('Tohatsu', 'ENGINES', 5),
('Torqeedo', 'ENGINES', 6),
('Evinrude', 'ENGINES', 7),
('Haswing', 'ENGINES', 8),
('Nissan Marine', 'ENGINES', 9),
('Selva Marine', 'ENGINES', 10),
('Parsun', 'ENGINES', 11),
('Hidea', 'ENGINES', 12),
('Mariner', 'ENGINES', 13),
-- Inboard engine brands
('Volvo Penta', 'ENGINES', 14),
('MerCruiser', 'ENGINES', 15),
('Yanmar Marine', 'ENGINES', 16),
('Caterpillar Marine', 'ENGINES', 17),
('Cummins Marine', 'ENGINES', 18),
('MAN Marine Engines', 'ENGINES', 19),
('Scania Marine', 'ENGINES', 20),
('MTU (Rolls-Royce Power Systems)', 'ENGINES', 21),
('Volvo Marine', 'ENGINES', 22),
('Ilmor Marine', 'ENGINES', 23),
('Indmar Marine Engines', 'ENGINES', 24),
('PCM (Pleasurecraft Marine Engines)', 'ENGINES', 25),
('Crusader Engines', 'ENGINES', 26),
('Marine Power', 'ENGINES', 27),
('Steyr Motors', 'ENGINES', 28),
('Nanni Diesel', 'ENGINES', 29),
('Perkins Marine', 'ENGINES', 30),
('Beta Marine', 'ENGINES', 31),
('Lombardini Marine', 'ENGINES', 32),
('FPT (Fiat Powertrain Technologies)', 'ENGINES', 33),
('Собствено производство', 'ENGINES', 9999)
ON CONFLICT (name, category) DO NOTHING;

-- FISHING BRANDS (general fishing equipment brands)
INSERT INTO brands (name, category, display_order) VALUES
('Shimano', 'FISHING', 1),
('Daiwa', 'FISHING', 2),
('Penn', 'FISHING', 3),
('Abu Garcia', 'FISHING', 4),
('Okuma', 'FISHING', 5),
('Rapala', 'FISHING', 6),
('Berkley', 'FISHING', 7),
('Spro', 'FISHING', 8),
('Owner', 'FISHING', 9),
('Gamakatsu', 'FISHING', 10),
('Savage Gear', 'FISHING', 11),
('Fox', 'FISHING', 12),
('Carp Pro', 'FISHING', 13),
('Dragon', 'FISHING', 14),
('Jaxon', 'FISHING', 15),
('Собствено производство', 'FISHING', 9999)
ON CONFLICT (name, category) DO NOTHING;

-- PARTS BRANDS (marine parts manufacturers)
INSERT INTO brands (name, category, display_order) VALUES
('Quicksilver', 'PARTS', 1),
('Mercury', 'PARTS', 2),
('Yamaha', 'PARTS', 3),
('Suzuki', 'PARTS', 4),
('Honda', 'PARTS', 5),
('Tohatsu', 'PARTS', 6),
('Volvo Penta', 'PARTS', 7),
('MerCruiser', 'PARTS', 8),
('Yanmar', 'PARTS', 9),
('Perkins', 'PARTS', 10),
('Собствено производство', 'PARTS', 9999)
ON CONFLICT (name, category) DO NOTHING;

-- WATER SPORTS BRANDS (water sports equipment)
INSERT INTO brands (name, category, display_order) VALUES
('Aqua Marina', 'WATER_SPORTS', 1),
('Red Paddle Co', 'WATER_SPORTS', 2),
('BOTE', 'WATER_SPORTS', 3),
('Fanatic', 'WATER_SPORTS', 4),
('Starboard', 'WATER_SPORTS', 5),
('JP Australia', 'WATER_SPORTS', 6),
('Naish', 'WATER_SPORTS', 7),
('Cabrinha', 'WATER_SPORTS', 8),
('Duotone', 'WATER_SPORTS', 9),
('Core', 'WATER_SPORTS', 10),
('Mystic', 'WATER_SPORTS', 11),
('ION', 'WATER_SPORTS', 12),
('Prolimit', 'WATER_SPORTS', 13),
('O''Neill', 'WATER_SPORTS', 14),
('Rip Curl', 'WATER_SPORTS', 15),
('Billabong', 'WATER_SPORTS', 16),
('Quiksilver', 'WATER_SPORTS', 17),
('Собствено производство', 'WATER_SPORTS', 9999)
ON CONFLICT (name, category) DO NOTHING;

-- MARINE ACCESSORIES BRANDS (marine accessories)
INSERT INTO brands (name, category, display_order) VALUES
('Plastimo', 'MARINE_ACCESSORIES', 1),
('Osculati', 'MARINE_ACCESSORIES', 2),
('Eval', 'MARINE_ACCESSORIES', 3),
('Lalizas', 'MARINE_ACCESSORIES', 4),
('Foresti & Suardi', 'MARINE_ACCESSORIES', 5),
('Attwood', 'MARINE_ACCESSORIES', 6),
('Seachoice', 'MARINE_ACCESSORIES', 7),
('Talamex', 'MARINE_ACCESSORIES', 8),
('Vetus', 'MARINE_ACCESSORIES', 9),
('Whale', 'MARINE_ACCESSORIES', 10),
('Rule', 'MARINE_ACCESSORIES', 11),
('Johnson Pump', 'MARINE_ACCESSORIES', 12),
('Jabsco', 'MARINE_ACCESSORIES', 13),
('Lewmar', 'MARINE_ACCESSORIES', 14),
('Harken', 'MARINE_ACCESSORIES', 15),
('Spinlock', 'MARINE_ACCESSORIES', 16),
('Собствено производство', 'MARINE_ACCESSORIES', 9999)
ON CONFLICT (name, category) DO NOTHING;