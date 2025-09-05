-- V3f__Insert_sailboat_brands.sql
-- All sailboat brands A-Z

-- A brands
INSERT INTO brands (name, category, display_order) VALUES
('Abeking & Rasmussen', 'SAILBOATS', 1),
('Albin', 'SAILBOATS', 2),
('Alden', 'SAILBOATS', 3),
('Alerion', 'SAILBOATS', 4),
('Alliaura', 'SAILBOATS', 5),
('Allied', 'SAILBOATS', 6),
('Alloy Yachts', 'SAILBOATS', 7),
('Allures', 'SAILBOATS', 8),
('Aloha', 'SAILBOATS', 9),
('Alubat', 'SAILBOATS', 10),
('Amel', 'SAILBOATS', 11),
('Archambault', 'SAILBOATS', 12),
('Arcona', 'SAILBOATS', 13),
('Astus', 'SAILBOATS', 14),
('Atlantic', 'SAILBOATS', 15),
('Aventura', 'SAILBOATS', 16)
ON CONFLICT (name, category) DO NOTHING;

-- B brands
INSERT INTO brands (name, category, display_order) VALUES
('Baba', 'SAILBOATS', 17),
('Balance', 'SAILBOATS', 18),
('Bali', 'SAILBOATS', 19),
('Baltic', 'SAILBOATS', 20),
('Bavaria', 'SAILBOATS', 21),
('Bayfield', 'SAILBOATS', 22),
('Belliure', 'SAILBOATS', 23),
('Beneteau', 'SAILBOATS', 24),
('Bente', 'SAILBOATS', 25),
('Bestevaer', 'SAILBOATS', 26),
('Black Pepper', 'SAILBOATS', 27),
('Bodrum', 'SAILBOATS', 28),
('Bowman', 'SAILBOATS', 29),
('Breehorn', 'SAILBOATS', 30),
('Brewer', 'SAILBOATS', 31),
('Bristol', 'SAILBOATS', 32),
('Bristol Channel', 'SAILBOATS', 33),
('Bruce Roberts', 'SAILBOATS', 34)
ON CONFLICT (name, category) DO NOTHING;

-- C brands
INSERT INTO brands (name, category, display_order) VALUES
('Cutter', 'SAILBOATS', 35),
('C&C', 'SAILBOATS', 36),
('CAL', 'SAILBOATS', 37),
('CNB', 'SAILBOATS', 38),
('CNSO', 'SAILBOATS', 39),
('CS', 'SAILBOATS', 40),
('CSY', 'SAILBOATS', 41),
('Cabo Rico', 'SAILBOATS', 42),
('Caliber', 'SAILBOATS', 43),
('Canadian Sailcraft', 'SAILBOATS', 44),
('Camper & Nicholsons', 'SAILBOATS', 45),
('Cape Dory', 'SAILBOATS', 46),
('Cape George', 'SAILBOATS', 47),
('Catalina', 'SAILBOATS', 48),
('Catamaran', 'SAILBOATS', 49),
('Catana', 'SAILBOATS', 50),
('Cheoy Lee', 'SAILBOATS', 51),
('Cherubini', 'SAILBOATS', 52),
('Class 40', 'SAILBOATS', 53),
('Classic', 'SAILBOATS', 54),
('Colin Archer', 'SAILBOATS', 55),
('Columbia', 'SAILBOATS', 56),
('Colvic', 'SAILBOATS', 57),
('Com-Pac', 'SAILBOATS', 58),
('Comar', 'SAILBOATS', 59),
('Comfortina', 'SAILBOATS', 60),
('Concordia', 'SAILBOATS', 61),
('Contessa', 'SAILBOATS', 62),
('Contest', 'SAILBOATS', 63),
('Cornish Crabbers', 'SAILBOATS', 64),
('Corsair', 'SAILBOATS', 65),
('Custom', 'SAILBOATS', 66)
ON CONFLICT (name, category) DO NOTHING;

-- D brands
INSERT INTO brands (name, category, display_order) VALUES
('Damarin', 'SAILBOATS', 67),
('Dehler', 'SAILBOATS', 68),
('Delphia', 'SAILBOATS', 69),
('Discovery', 'SAILBOATS', 70),
('Downeast', 'SAILBOATS', 71),
('Dragonfly', 'SAILBOATS', 72),
('Dufour', 'SAILBOATS', 73)
ON CONFLICT (name, category) DO NOTHING;

-- E brands
INSERT INTO brands (name, category, display_order) VALUES
('Elan', 'SAILBOATS', 74),
('Endeavour', 'SAILBOATS', 75),
('Endurance', 'SAILBOATS', 76),
('Ericson', 'SAILBOATS', 77),
('Etap', 'SAILBOATS', 78),
('Excess', 'SAILBOATS', 79)
ON CONFLICT (name, category) DO NOTHING;

-- F brands
INSERT INTO brands (name, category, display_order) VALUES
('FarEast', 'SAILBOATS', 80),
('Farr', 'SAILBOATS', 81),
('Feeling', 'SAILBOATS', 82),
('Finngulf', 'SAILBOATS', 83),
('Fisher', 'SAILBOATS', 84),
('Formosa', 'SAILBOATS', 85),
('Fountaine Pajot', 'SAILBOATS', 86),
('Franchini', 'SAILBOATS', 87),
('Freedom', 'SAILBOATS', 88),
('Friendship', 'SAILBOATS', 89),
('Furia', 'SAILBOATS', 90)
ON CONFLICT (name, category) DO NOTHING;

-- G brands
INSERT INTO brands (name, category, display_order) VALUES
('Garcia', 'SAILBOATS', 91),
('Gemini', 'SAILBOATS', 92),
('Gib Sea', 'SAILBOATS', 93),
('Grand Soleil', 'SAILBOATS', 94),
('Gulet', 'SAILBOATS', 95),
('Gulfstar', 'SAILBOATS', 96),
('Gunboat', 'SAILBOATS', 97)
ON CONFLICT (name, category) DO NOTHING;

-- H brands
INSERT INTO brands (name, category, display_order) VALUES
('HH Catamarans', 'SAILBOATS', 98),
('Hake / Seaward', 'SAILBOATS', 99),
('Hallberg-Rassy', 'SAILBOATS', 100),
('Hans Christian', 'SAILBOATS', 101),
('Hanse', 'SAILBOATS', 102),
('Herreshoff', 'SAILBOATS', 103),
('Hinckley', 'SAILBOATS', 104),
('Hinterhoeller', 'SAILBOATS', 105),
('Hobie Cat', 'SAILBOATS', 106),
('Hoek', 'SAILBOATS', 107),
('Hunter', 'SAILBOATS', 108),
('Hutting', 'SAILBOATS', 109),
('Hylas', 'SAILBOATS', 110)
ON CONFLICT (name, category) DO NOTHING;

-- I brands
INSERT INTO brands (name, category, display_order) VALUES
('Ice Yachts', 'SAILBOATS', 111),
('Irwin', 'SAILBOATS', 112),
('Island Packet', 'SAILBOATS', 113),
('Islander', 'SAILBOATS', 114),
('Italia Yachts', 'SAILBOATS', 115)
ON CONFLICT (name, category) DO NOTHING;

-- J brands
INSERT INTO brands (name, category, display_order) VALUES
('J Boats', 'SAILBOATS', 116),
('JPK', 'SAILBOATS', 117),
('Jeanneau', 'SAILBOATS', 118),
('Jongert', 'SAILBOATS', 119),
('Jouet', 'SAILBOATS', 120),
('Judel and Vrolijk', 'SAILBOATS', 121)
ON CONFLICT (name, category) DO NOTHING;

-- K brands
INSERT INTO brands (name, category, display_order) VALUES
('Kelsall', 'SAILBOATS', 122),
('Ker', 'SAILBOATS', 123),
('Ketch', 'SAILBOATS', 124),
('Kirie', 'SAILBOATS', 125),
('Knysna', 'SAILBOATS', 126),
('Koopmans', 'SAILBOATS', 127)
ON CONFLICT (name, category) DO NOTHING;

-- L brands
INSERT INTO brands (name, category, display_order) VALUES
('LM', 'SAILBOATS', 128),
('Lagoon', 'SAILBOATS', 129),
('Latitude 46', 'SAILBOATS', 130),
('Lemsteraak', 'SAILBOATS', 131),
('Leopard', 'SAILBOATS', 132),
('Little Harbor', 'SAILBOATS', 133)
ON CONFLICT (name, category) DO NOTHING;

-- M brands
INSERT INTO brands (name, category, display_order) VALUES
('M.A.T.', 'SAILBOATS', 134),
('MacGregor', 'SAILBOATS', 135),
('Malo', 'SAILBOATS', 136),
('Manta', 'SAILBOATS', 137),
('Mariner', 'SAILBOATS', 138),
('Marlow-Hunter', 'SAILBOATS', 139),
('Marsaudon Composites', 'SAILBOATS', 140),
('Mason', 'SAILBOATS', 141),
('Maxi', 'SAILBOATS', 142),
('Maxi Dolphin', 'SAILBOATS', 143),
('McConaghy', 'SAILBOATS', 144),
('Melges', 'SAILBOATS', 145),
('Meta', 'SAILBOATS', 146),
('Moody', 'SAILBOATS', 147),
('Moon', 'SAILBOATS', 148),
('Morgan', 'SAILBOATS', 149),
('Morris', 'SAILBOATS', 150),
('Motorsailer', 'SAILBOATS', 151),
('Mylius', 'SAILBOATS', 152)
ON CONFLICT (name, category) DO NOTHING;

-- N brands
INSERT INTO brands (name, category, display_order) VALUES
('NEEL', 'SAILBOATS', 153),
('Najad', 'SAILBOATS', 154),
('Nauticat', 'SAILBOATS', 155),
('Nautitech', 'SAILBOATS', 156),
('Nautor Swan', 'SAILBOATS', 157),
('Newport', 'SAILBOATS', 158),
('Niagara', 'SAILBOATS', 159),
('Nicholson', 'SAILBOATS', 160),
('Nonsuch', 'SAILBOATS', 161),
('Nordia', 'SAILBOATS', 162),
('Norseman', 'SAILBOATS', 163),
('North Wind', 'SAILBOATS', 164)
ON CONFLICT (name, category) DO NOTHING;

-- O brands
INSERT INTO brands (name, category, display_order) VALUES
('O Day', 'SAILBOATS', 165),
('Ohlson', 'SAILBOATS', 166),
('One Design', 'SAILBOATS', 167),
('Outbound', 'SAILBOATS', 168),
('Outremer', 'SAILBOATS', 169),
('Oyster', 'SAILBOATS', 170)
ON CONFLICT (name, category) DO NOTHING;

-- P brands
INSERT INTO brands (name, category, display_order) VALUES
('Pacific Seacraft', 'SAILBOATS', 171),
('Palmer Johnson', 'SAILBOATS', 172),
('Passport', 'SAILBOATS', 173),
('Pearson', 'SAILBOATS', 174),
('Perini Navi', 'SAILBOATS', 175),
('Pogo', 'SAILBOATS', 176),
('Precision', 'SAILBOATS', 177),
('Privilege', 'SAILBOATS', 178),
('Prout', 'SAILBOATS', 179),
('Puffin', 'SAILBOATS', 180),
('Puma', 'SAILBOATS', 181)
ON CONFLICT (name, category) DO NOTHING;

-- Q brands
INSERT INTO brands (name, category, display_order) VALUES
('Quarken', 'SAILBOATS', 182)
ON CONFLICT (name, category) DO NOTHING;

-- R brands
INSERT INTO brands (name, category, display_order) VALUES
('RM Yachts', 'SAILBOATS', 183),
('RS', 'SAILBOATS', 184),
('Rapido', 'SAILBOATS', 185),
('Rhodes', 'SAILBOATS', 186),
('Rival', 'SAILBOATS', 187),
('Robert Perry', 'SAILBOATS', 188),
('Royal Cape Catamarans', 'SAILBOATS', 189),
('Royal Huisman', 'SAILBOATS', 190),
('Rustler', 'SAILBOATS', 191)
ON CONFLICT (name, category) DO NOTHING;

-- S brands
INSERT INTO brands (name, category, display_order) VALUES
('S2', 'SAILBOATS', 192),
('Sabre', 'SAILBOATS', 193),
('Sadler', 'SAILBOATS', 194),
('Saffier', 'SAILBOATS', 195),
('Saga', 'SAILBOATS', 196),
('Sailboat', 'SAILBOATS', 197),
('Salona', 'SAILBOATS', 198),
('Sangermani', 'SAILBOATS', 199),
('Santa Cruz', 'SAILBOATS', 200),
('Schionning', 'SAILBOATS', 201),
('Schock', 'SAILBOATS', 202),
('Schooner', 'SAILBOATS', 203),
('Sea Sprite', 'SAILBOATS', 204),
('Seawind', 'SAILBOATS', 205),
('Shannon', 'SAILBOATS', 206),
('Shogun', 'SAILBOATS', 207),
('Sigma', 'SAILBOATS', 208),
('Skutsje', 'SAILBOATS', 209),
('Slocum', 'SAILBOATS', 210),
('Sloop', 'SAILBOATS', 211),
('Solaris', 'SAILBOATS', 212),
('Southerly', 'SAILBOATS', 213),
('Southern Ocean', 'SAILBOATS', 214),
('Sparkman & Stephens', 'SAILBOATS', 215),
('Spirit Yachts', 'SAILBOATS', 216),
('Standfast', 'SAILBOATS', 217),
('Sunbeam', 'SAILBOATS', 218),
('Sunreef', 'SAILBOATS', 219),
('Sweden Yachts', 'SAILBOATS', 220)
ON CONFLICT (name, category) DO NOTHING;

-- T brands
INSERT INTO brands (name, category, display_order) VALUES
('Ta Chiao', 'SAILBOATS', 221),
('Ta Shing', 'SAILBOATS', 222),
('Tartan', 'SAILBOATS', 223),
('Taswell', 'SAILBOATS', 224),
('Tayana', 'SAILBOATS', 225),
('Tjalk', 'SAILBOATS', 226),
('Tofinou', 'SAILBOATS', 227),
('Trident', 'SAILBOATS', 228),
('Trintella', 'SAILBOATS', 229)
ON CONFLICT (name, category) DO NOTHING;

-- V brands
INSERT INTO brands (name, category, display_order) VALUES
('Vagabond', 'SAILBOATS', 230),
('Valiant', 'SAILBOATS', 231),
('Van De Stadt', 'SAILBOATS', 232),
('Vancouver', 'SAILBOATS', 233),
('Victoire', 'SAILBOATS', 234),
('Victoria', 'SAILBOATS', 235),
('Viko', 'SAILBOATS', 236),
('Vindo', 'SAILBOATS', 237),
('Vismara', 'SAILBOATS', 238),
('Voyage', 'SAILBOATS', 239),
('Voyage Yachts', 'SAILBOATS', 240)
ON CONFLICT (name, category) DO NOTHING;

-- W brands
INSERT INTO brands (name, category, display_order) VALUES
('Waarschip', 'SAILBOATS', 241),
('Wally', 'SAILBOATS', 242),
('Wauquiez', 'SAILBOATS', 243),
('Westerly', 'SAILBOATS', 244),
('Westsail', 'SAILBOATS', 245),
('Whitby', 'SAILBOATS', 246),
('Windelo', 'SAILBOATS', 247)
ON CONFLICT (name, category) DO NOTHING;

-- X brands
INSERT INTO brands (name, category, display_order) VALUES
('X-Yachts', 'SAILBOATS', 248),
('Xquisite Yachts', 'SAILBOATS', 249)
ON CONFLICT (name, category) DO NOTHING;

-- Z brands
INSERT INTO brands (name, category, display_order) VALUES
('Zeeschouw', 'SAILBOATS', 250)
ON CONFLICT (name, category) DO NOTHING;

-- Custom production option (always last)
INSERT INTO brands (name, category, display_order) VALUES
('Собствено производство / Други', 'SAILBOATS', 9999)
ON CONFLICT (name, category) DO NOTHING;