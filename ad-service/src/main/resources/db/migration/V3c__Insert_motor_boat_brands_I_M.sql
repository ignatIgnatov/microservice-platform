-- V3c__Insert_motor_boat_brands_I_M.sql
-- Motor boat brands I-M (Part 3 of 6)

-- I brands
INSERT INTO brands (name, category, display_order) VALUES
('Intrepid', 'MOTOR_BOATS', 174),
('Invincible', 'MOTOR_BOATS', 175),
('Invictus', 'MOTOR_BOATS', 176),
('IKon', 'MOTOR_BOATS', 177),
('ISA', 'MOTOR_BOATS', 178),
('Iron', 'MOTOR_BOATS', 179),
('Innovazione e Progetti', 'MOTOR_BOATS', 180)
ON CONFLICT (name, category) DO NOTHING;

-- J brands
INSERT INTO brands (name, category, display_order) VALUES
('Jeanneau', 'MOTOR_BOATS', 181),
('Jupiter', 'MOTOR_BOATS', 182),
('JC', 'MOTOR_BOATS', 183),
('Joker Boat', 'MOTOR_BOATS', 184),
('Jefferson', 'MOTOR_BOATS', 185),
('Johnson', 'MOTOR_BOATS', 186),
('Jarper Marine', 'MOTOR_BOATS', 187)
ON CONFLICT (name, category) DO NOTHING;

-- K brands
INSERT INTO brands (name, category, display_order) VALUES
('Key West', 'MOTOR_BOATS', 188),
('Kawasaki', 'MOTOR_BOATS', 189),
('Karnic', 'MOTOR_BOATS', 190),
('KenCraft', 'MOTOR_BOATS', 191),
('Kadey-Krogen', 'MOTOR_BOATS', 192),
('Key Largo', 'MOTOR_BOATS', 193),
('KingFisher', 'MOTOR_BOATS', 194)
ON CONFLICT (name, category) DO NOTHING;

-- L brands
INSERT INTO brands (name, category, display_order) VALUES
('Ladia', 'MOTOR_BOATS', 195),
('Landau', 'MOTOR_BOATS', 196),
('Lagoon', 'MOTOR_BOATS', 197),
('Larson', 'MOTOR_BOATS', 198),
('Lazzara', 'MOTOR_BOATS', 199),
('Legacy', 'MOTOR_BOATS', 200),
('Leopard', 'MOTOR_BOATS', 201),
('Lilybaeum', 'MOTOR_BOATS', 202),
('Linssen', 'MOTOR_BOATS', 203),
('Lomac', 'MOTOR_BOATS', 204),
('Lowe', 'MOTOR_BOATS', 205),
('Luhrs', 'MOTOR_BOATS', 206),
('Lund', 'MOTOR_BOATS', 207),
('Luxe-Motor', 'MOTOR_BOATS', 208)
ON CONFLICT (name, category) DO NOTHING;

-- M brands
INSERT INTO brands (name, category, display_order) VALUES
('MB', 'MOTOR_BOATS', 209),
('MJM', 'MOTOR_BOATS', 210),
('MTI', 'MOTOR_BOATS', 211),
('Mag Bay', 'MOTOR_BOATS', 212),
('Magnum', 'MOTOR_BOATS', 213),
('Mainship', 'MOTOR_BOATS', 214),
('Maiora', 'MOTOR_BOATS', 215),
('Majek', 'MOTOR_BOATS', 216),
('Majesty', 'MOTOR_BOATS', 217),
('Mako', 'MOTOR_BOATS', 218),
('Malibu', 'MOTOR_BOATS', 219),
('Mangusta', 'MOTOR_BOATS', 220),
('Manitou', 'MOTOR_BOATS', 221),
('Marex', 'MOTOR_BOATS', 222),
('Marine Trader', 'MOTOR_BOATS', 223),
('Maritimo', 'MOTOR_BOATS', 224),
('Marlago', 'MOTOR_BOATS', 225),
('Marlow', 'MOTOR_BOATS', 226),
('Marquis', 'MOTOR_BOATS', 227),
('MasterCraft', 'MOTOR_BOATS', 228),
('Maverick', 'MOTOR_BOATS', 229),
('Maxima', 'MOTOR_BOATS', 230),
('Maxum', 'MOTOR_BOATS', 231),
('May-Craft', 'MOTOR_BOATS', 232),
('Mazu Yachts', 'MOTOR_BOATS', 233),
('McKinna', 'MOTOR_BOATS', 234),
('Menorquin', 'MOTOR_BOATS', 235),
('Meggacraft', 'MOTOR_BOATS', 236),
('Meridian', 'MOTOR_BOATS', 237),
('Midnight Express', 'MOTOR_BOATS', 238),
('Mikelson', 'MOTOR_BOATS', 239),
('Mimi', 'MOTOR_BOATS', 240),
('Mochi Craft', 'MOTOR_BOATS', 241),
('Monk', 'MOTOR_BOATS', 242),
('Montara', 'MOTOR_BOATS', 243),
('Monte Carlo Yachts', 'MOTOR_BOATS', 244),
('Monte Fino', 'MOTOR_BOATS', 245),
('Monterey', 'MOTOR_BOATS', 246),
('Moomba', 'MOTOR_BOATS', 247),
('Moonen', 'MOTOR_BOATS', 248),
('Motor Yacht', 'MOTOR_BOATS', 249),
('Mystic Powerboats', 'MOTOR_BOATS', 250)
ON CONFLICT (name, category) DO NOTHING;