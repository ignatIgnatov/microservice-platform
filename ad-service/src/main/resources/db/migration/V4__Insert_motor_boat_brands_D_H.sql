-- V3b__Insert_motor_boat_brands_D_H.sql
-- Motor boat brands D-H (Part 2 of 6)

-- D brands
INSERT INTO brands (name, category, display_order) VALUES
('De Antonio Yachts', 'MOTOR_BOATS', 109),
('Donzi', 'MOTOR_BOATS', 110),
('Doral', 'MOTOR_BOATS', 111),
('Defiance', 'MOTOR_BOATS', 112),
('Delta Powerboats', 'MOTOR_BOATS', 113),
('Duffy', 'MOTOR_BOATS', 114),
('Duckworth', 'MOTOR_BOATS', 115),
('Delphia', 'MOTOR_BOATS', 116),
('DeFever', 'MOTOR_BOATS', 117),
('Deep Impact', 'MOTOR_BOATS', 118),
('Dominator', 'MOTOR_BOATS', 119),
('Dellapasqua', 'MOTOR_BOATS', 120),
('Dusky', 'MOTOR_BOATS', 121)
ON CONFLICT (name, category) DO NOTHING;

-- E brands
INSERT INTO brands (name, category, display_order) VALUES
('Everglades', 'MOTOR_BOATS', 122),
('Edgewater', 'MOTOR_BOATS', 123),
('Excel', 'MOTOR_BOATS', 124),
('Edge Duck Boats', 'MOTOR_BOATS', 125),
('Explorer', 'MOTOR_BOATS', 126),
('Elegance', 'MOTOR_BOATS', 127),
('Ebbtide', 'MOTOR_BOATS', 128),
('East Cape', 'MOTOR_BOATS', 129)
ON CONFLICT (name, category) DO NOTHING;

-- F brands
INSERT INTO brands (name, category, display_order) VALUES
('Four Winns', 'MOTOR_BOATS', 130),
('Formula', 'MOTOR_BOATS', 131),
('Fairline', 'MOTOR_BOATS', 132),
('Ferretti Yachts', 'MOTOR_BOATS', 133),
('Fountain', 'MOTOR_BOATS', 134),
('Fjord', 'MOTOR_BOATS', 135),
('Fountaine Pajot', 'MOTOR_BOATS', 136),
('Freeman', 'MOTOR_BOATS', 137),
('Falcon', 'MOTOR_BOATS', 138),
('Frauscher', 'MOTOR_BOATS', 139),
('Fiart', 'MOTOR_BOATS', 140),
('Front Runner', 'MOTOR_BOATS', 141),
('Feadship', 'MOTOR_BOATS', 142),
('Faeton', 'MOTOR_BOATS', 143),
('Finnmaster', 'MOTOR_BOATS', 144),
('Focus', 'MOTOR_BOATS', 145),
('FIM', 'MOTOR_BOATS', 146),
('Freedom', 'MOTOR_BOATS', 147),
('Fleming', 'MOTOR_BOATS', 148)
ON CONFLICT (name, category) DO NOTHING;

-- G brands
INSERT INTO brands (name, category, display_order) VALUES
('Grady-White', 'MOTOR_BOATS', 149),
('G3', 'MOTOR_BOATS', 150),
('Godfrey', 'MOTOR_BOATS', 151),
('Galeon', 'MOTOR_BOATS', 152),
('Glastron', 'MOTOR_BOATS', 153),
('Godfrey Marine', 'MOTOR_BOATS', 154),
('Grand', 'MOTOR_BOATS', 155),
('Greenline', 'MOTOR_BOATS', 156),
('Guy Couach', 'MOTOR_BOATS', 157)
ON CONFLICT (name, category) DO NOTHING;

-- H brands
INSERT INTO brands (name, category, display_order) VALUES
('Harris', 'MOTOR_BOATS', 158),
('Highfield', 'MOTOR_BOATS', 159),
('Hurricane', 'MOTOR_BOATS', 160),
('Hatteras', 'MOTOR_BOATS', 161),
('HCB', 'MOTOR_BOATS', 162),
('Hewescraft', 'MOTOR_BOATS', 163),
('Hydra-Sports', 'MOTOR_BOATS', 164),
('Hinckley', 'MOTOR_BOATS', 165),
('Honda Marine', 'MOTOR_BOATS', 166),
('Horizon', 'MOTOR_BOATS', 167),
('Heyday', 'MOTOR_BOATS', 168),
('Havoc', 'MOTOR_BOATS', 169),
('Hanover', 'MOTOR_BOATS', 170),
('Harris FloteBote', 'MOTOR_BOATS', 171),
('Houseboat', 'MOTOR_BOATS', 172),
('Hunt Yachts', 'MOTOR_BOATS', 173)
ON CONFLICT (name, category) DO NOTHING;