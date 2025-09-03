-- Complete Brand Database Insert Script

-- Insert ALL Motor Boats/Yachts brands (A-Z) - Part 1
-- A brands
INSERT INTO brands (name, category, display_order) VALUES
('Azimut', 'MOTOR_BOATS', 1),
('Avalon', 'MOTOR_BOATS', 2),
('Axopar', 'MOTOR_BOATS', 3),
('Absolute', 'MOTOR_BOATS', 4),
('Alumacraft', 'MOTOR_BOATS', 5),
('Aquila', 'MOTOR_BOATS', 6),
('AB Inflatables', 'MOTOR_BOATS', 7),
('Avid', 'MOTOR_BOATS', 8),
('Axis', 'MOTOR_BOATS', 9),
('Aviara', 'MOTOR_BOATS', 10),
('Albemarle', 'MOTOR_BOATS', 11),
('Aquasport', 'MOTOR_BOATS', 12),
('Apreamare', 'MOTOR_BOATS', 13),
('ATX Surf Boats', 'MOTOR_BOATS', 14),
('Albin', 'MOTOR_BOATS', 15),
('Allroundmarin', 'MOTOR_BOATS', 16),
('Aicon', 'MOTOR_BOATS', 17),
('Airon', 'MOTOR_BOATS', 18),
('Alfastreet', 'MOTOR_BOATS', 19),
('ALK2 Powerboats', 'MOTOR_BOATS', 20),
('Admiral', 'MOTOR_BOATS', 21),
('Alweld', 'MOTOR_BOATS', 22),
('Anvera', 'MOTOR_BOATS', 23),
('Antaris', 'MOTOR_BOATS', 24),
('AB', 'MOTOR_BOATS', 25),
('Aquanaut', 'MOTOR_BOATS', 26),
('Aquador', 'MOTOR_BOATS', 27),
('Angler', 'MOTOR_BOATS', 28),
('Alm', 'MOTOR_BOATS', 29),
('American Tug', 'MOTOR_BOATS', 30),
('Andros', 'MOTOR_BOATS', 31),
('Arcadia Yachts', 'MOTOR_BOATS', 32),
('ACM', 'MOTOR_BOATS', 33),
('Atlantis', 'MOTOR_BOATS', 34)
ON CONFLICT (name, category) DO NOTHING;

-- B brands
INSERT INTO brands (name, category, display_order) VALUES
('Boston Whaler', 'MOTOR_BOATS', 35),
('Bayliner', 'MOTOR_BOATS', 36),
('Bennington', 'MOTOR_BOATS', 37),
('Beneteau', 'MOTOR_BOATS', 38),
('Barletta', 'MOTOR_BOATS', 39),
('Blackfin', 'MOTOR_BOATS', 40),
('Bavaria', 'MOTOR_BOATS', 41),
('Bentley Pontoons', 'MOTOR_BOATS', 42),
('Bertram', 'MOTOR_BOATS', 43),
('Brig', 'MOTOR_BOATS', 44),
('BRABUS', 'MOTOR_BOATS', 45),
('BlackJack', 'MOTOR_BOATS', 46),
('Bryant', 'MOTOR_BOATS', 47),
('Bass Tracker', 'MOTOR_BOATS', 48),
('Bombard', 'MOTOR_BOATS', 49),
('Blue Wave', 'MOTOR_BOATS', 50),
('Bluegame', 'MOTOR_BOATS', 51),
('Baia', 'MOTOR_BOATS', 52),
('Bass Cat', 'MOTOR_BOATS', 53),
('Baja', 'MOTOR_BOATS', 54),
('Back Cove', 'MOTOR_BOATS', 55),
('Blazer', 'MOTOR_BOATS', 56),
('Bluewater', 'MOTOR_BOATS', 57),
('Baglietto', 'MOTOR_BOATS', 58),
('Bali', 'MOTOR_BOATS', 59),
('Broward', 'MOTOR_BOATS', 60),
('Bay Rider', 'MOTOR_BOATS', 61),
('Birchwood', 'MOTOR_BOATS', 62),
('Benetti', 'MOTOR_BOATS', 63),
('Bulls Bay', 'MOTOR_BOATS', 64),
('Buddy Davis', 'MOTOR_BOATS', 65),
('Bahama', 'MOTOR_BOATS', 66),
('Burger', 'MOTOR_BOATS', 67),
('Boarncruiser', 'MOTOR_BOATS', 68),
('Botnia', 'MOTOR_BOATS', 69),
('Broom', 'MOTOR_BOATS', 70),
('Berkshire', 'MOTOR_BOATS', 71)
ON CONFLICT (name, category) DO NOTHING;

-- C brands
INSERT INTO brands (name, category, display_order) VALUES
('Chaparral', 'MOTOR_BOATS', 72),
('Cobalt', 'MOTOR_BOATS', 73),
('Chris-Craft', 'MOTOR_BOATS', 74),
('Cobia', 'MOTOR_BOATS', 75),
('Carver', 'MOTOR_BOATS', 76),
('Carolina Skiff', 'MOTOR_BOATS', 77),
('Cranchi', 'MOTOR_BOATS', 78),
('Crestliner', 'MOTOR_BOATS', 79),
('Crest', 'MOTOR_BOATS', 80),
('Contender', 'MOTOR_BOATS', 81),
('Custom', 'MOTOR_BOATS', 82),
('Cruisers Yachts', 'MOTOR_BOATS', 83),
('Crownline', 'MOTOR_BOATS', 84),
('Capelli', 'MOTOR_BOATS', 85),
('Caymas', 'MOTOR_BOATS', 86),
('Century', 'MOTOR_BOATS', 87),
('Cape Horn', 'MOTOR_BOATS', 88),
('Cigarette', 'MOTOR_BOATS', 89),
('Centurion', 'MOTOR_BOATS', 90),
('Cutwater', 'MOTOR_BOATS', 91),
('Canados', 'MOTOR_BOATS', 92),
('Carolina Classic', 'MOTOR_BOATS', 93),
('Cheoy Lee', 'MOTOR_BOATS', 94),
('Clearwater', 'MOTOR_BOATS', 95),
('Californian', 'MOTOR_BOATS', 96),
('Cobra Ribs', 'MOTOR_BOATS', 97),
('Concept', 'MOTOR_BOATS', 98),
('CHB', 'MOTOR_BOATS', 99),
('Corsiva', 'MOTOR_BOATS', 100),
('Cormate', 'MOTOR_BOATS', 101),
('Cypress Cay', 'MOTOR_BOATS', 102),
('Caravelle', 'MOTOR_BOATS', 103),
('Classic', 'MOTOR_BOATS', 104),
('Champion', 'MOTOR_BOATS', 105),
('Cantieri di Pisa', 'MOTOR_BOATS', 106),
('Cayman Yachts', 'MOTOR_BOATS', 107),
('Colombo', 'MOTOR_BOATS', 108)
ON CONFLICT (name, category) DO NOTHING;

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

-- Continue with G-Z Motor Boats
INSERT INTO brands (name, category, display_order) VALUES
-- G brands
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

-- N brands
INSERT INTO brands (name, category, display_order) VALUES
('NX Boats', 'MOTOR_BOATS', 251),
('Native Watercraft', 'MOTOR_BOATS', 252),
('NauticStar', 'MOTOR_BOATS', 253),
('Nautique', 'MOTOR_BOATS', 254),
('Navan', 'MOTOR_BOATS', 255),
('Navigator', 'MOTOR_BOATS', 256),
('Neptunus', 'MOTOR_BOATS', 257),
('Nimbus', 'MOTOR_BOATS', 258),
('Nireus', 'MOTOR_BOATS', 259),
('Nitro', 'MOTOR_BOATS', 260),
('Nor-Tech', 'MOTOR_BOATS', 261),
('Nord Star', 'MOTOR_BOATS', 262),
('Nordhavn', 'MOTOR_BOATS', 263),
('Nordic Tug', 'MOTOR_BOATS', 264),
('Nordkapp', 'MOTOR_BOATS', 265),
('North River', 'MOTOR_BOATS', 266),
('NorthCoast', 'MOTOR_BOATS', 267),
('Northstar', 'MOTOR_BOATS', 268),
('Novamarine', 'MOTOR_BOATS', 269),
('Novurania', 'MOTOR_BOATS', 270),
('Numarine', 'MOTOR_BOATS', 271),
('Nuova Jolly', 'MOTOR_BOATS', 272)
ON CONFLICT (name, category) DO NOTHING;

-- Continue with O-Z Motor Boats
INSERT INTO brands (name, category, display_order) VALUES
-- O brands
('Ocean Alexander', 'MOTOR_BOATS', 273),
('Ocean Craft Marine', 'MOTOR_BOATS', 274),
('Ocean Yachts', 'MOTOR_BOATS', 275),
('Ocqueteau', 'MOTOR_BOATS', 276),
('Omega', 'MOTOR_BOATS', 277),
('Offshore Yachts', 'MOTOR_BOATS', 278),
('Outer Reef Yachts', 'MOTOR_BOATS', 279)
ON CONFLICT (name, category) DO NOTHING;

-- P brands
INSERT INTO brands (name, category, display_order) VALUES
('Pacific', 'MOTOR_BOATS', 280),
('Pacific Craft', 'MOTOR_BOATS', 281),
('Pair Customs', 'MOTOR_BOATS', 282),
('Palm Beach Motor Yachts', 'MOTOR_BOATS', 283),
('Pardo Yachts', 'MOTOR_BOATS', 284),
('Parker', 'MOTOR_BOATS', 285),
('Parker Poland', 'MOTOR_BOATS', 286),
('Pathfinder', 'MOTOR_BOATS', 287),
('Pearl', 'MOTOR_BOATS', 288),
('Pedro', 'MOTOR_BOATS', 289),
('Pershing', 'MOTOR_BOATS', 290),
('Phoenix', 'MOTOR_BOATS', 291),
('Pioneer', 'MOTOR_BOATS', 292),
('Pirelli', 'MOTOR_BOATS', 293),
('Polar Kraft', 'MOTOR_BOATS', 294),
('Posillipo', 'MOTOR_BOATS', 295),
('Post', 'MOTOR_BOATS', 296),
('Premier', 'MOTOR_BOATS', 297),
('President', 'MOTOR_BOATS', 298),
('Prestige', 'MOTOR_BOATS', 299),
('Princecraft', 'MOTOR_BOATS', 300),
('Princess', 'MOTOR_BOATS', 301),
('Pro-Line', 'MOTOR_BOATS', 302),
('Pro Marine', 'MOTOR_BOATS', 303),
('Protector', 'MOTOR_BOATS', 304),
('Pursuit', 'MOTOR_BOATS', 305)
ON CONFLICT (name, category) DO NOTHING;

-- Q brands
INSERT INTO brands (name, category, display_order) VALUES
('Qwest', 'MOTOR_BOATS', 306),
('Quarken', 'MOTOR_BOATS', 307),
('Quicksilver', 'MOTOR_BOATS', 308)
ON CONFLICT (name, category) DO NOTHING;

-- R brands
INSERT INTO brands (name, category, display_order) VALUES
('RIO', 'MOTOR_BOATS', 309),
('RYCK', 'MOTOR_BOATS', 310),
('Rampage', 'MOTOR_BOATS', 311),
('Rand', 'MOTOR_BOATS', 312),
('Ranger', 'MOTOR_BOATS', 313),
('Ranger Tugs', 'MOTOR_BOATS', 314),
('Ranieri', 'MOTOR_BOATS', 315),
('Reaper Boats', 'MOTOR_BOATS', 316),
('Regal', 'MOTOR_BOATS', 317),
('Regency', 'MOTOR_BOATS', 318),
('Regulator', 'MOTOR_BOATS', 319),
('Release', 'MOTOR_BOATS', 320),
('Rhea', 'MOTOR_BOATS', 321),
('Ribco', 'MOTOR_BOATS', 322),
('Ribeye', 'MOTOR_BOATS', 323),
('Rinker', 'MOTOR_BOATS', 324),
('Rio Yachts', 'MOTOR_BOATS', 325),
('Riva', 'MOTOR_BOATS', 326),
('Riviera', 'MOTOR_BOATS', 327),
('Rizzardi', 'MOTOR_BOATS', 328),
('Robalo', 'MOTOR_BOATS', 329),
('Rodman', 'MOTOR_BOATS', 330),
('Rybovich', 'MOTOR_BOATS', 331)
ON CONFLICT (name, category) DO NOTHING;

-- S brands
INSERT INTO brands (name, category, display_order) VALUES
('SACS', 'MOTOR_BOATS', 332),
('Sabre', 'MOTOR_BOATS', 333),
('Saga', 'MOTOR_BOATS', 334),
('Sailfish', 'MOTOR_BOATS', 335),
('Salpa', 'MOTOR_BOATS', 336),
('Sanger', 'MOTOR_BOATS', 337),
('Sanlorenzo', 'MOTOR_BOATS', 338),
('Sargo', 'MOTOR_BOATS', 339),
('Sasga Yachts', 'MOTOR_BOATS', 340),
('Savannah', 'MOTOR_BOATS', 341),
('Saver', 'MOTOR_BOATS', 342),
('Saxdor', 'MOTOR_BOATS', 343),
('Scanner', 'MOTOR_BOATS', 344),
('Scarab', 'MOTOR_BOATS', 345),
('Schaefer', 'MOTOR_BOATS', 346),
('Scorpion', 'MOTOR_BOATS', 347),
('Scout', 'MOTOR_BOATS', 348),
('Sea Ark', 'MOTOR_BOATS', 349),
('Sea Born', 'MOTOR_BOATS', 350),
('Sea Cat', 'MOTOR_BOATS', 351),
('Sea Chaser', 'MOTOR_BOATS', 352),
('Sea Fox', 'MOTOR_BOATS', 353),
('Sea Hunt', 'MOTOR_BOATS', 354),
('Sea Pro', 'MOTOR_BOATS', 355),
('Sea Ray', 'MOTOR_BOATS', 356),
('Sea Water', 'MOTOR_BOATS', 357),
('Sea-Doo Sport Boats', 'MOTOR_BOATS', 358),
('SeaCraft', 'MOTOR_BOATS', 359),
('SeaHunter', 'MOTOR_BOATS', 360),
('SeaVee', 'MOTOR_BOATS', 361),
('Sealine', 'MOTOR_BOATS', 362),
('Seaswirl', 'MOTOR_BOATS', 363),
('Seaswirl Striper', 'MOTOR_BOATS', 364),
('Sessa Marine', 'MOTOR_BOATS', 365),
('Shallow Sport', 'MOTOR_BOATS', 366),
('Shamrock', 'MOTOR_BOATS', 367),
('ShearWater', 'MOTOR_BOATS', 368),
('Shoalwater', 'MOTOR_BOATS', 369),
('Silverton', 'MOTOR_BOATS', 370),
('Sirena', 'MOTOR_BOATS', 371),
('Skeeter', 'MOTOR_BOATS', 372),
('Skipper-BSK', 'MOTOR_BOATS', 373),
('Sloep', 'MOTOR_BOATS', 374),
('Smartliner', 'MOTOR_BOATS', 375),
('Smoker Craft', 'MOTOR_BOATS', 376),
('Solace', 'MOTOR_BOATS', 377),
('Solara', 'MOTOR_BOATS', 378),
('Solaris Power', 'MOTOR_BOATS', 379),
('South Bay', 'MOTOR_BOATS', 380),
('SouthWind', 'MOTOR_BOATS', 381),
('Southport', 'MOTOR_BOATS', 382),
('Spartan', 'MOTOR_BOATS', 383),
('Sportsman', 'MOTOR_BOATS', 384),
('Stabicraft', 'MOTOR_BOATS', 385),
('Stamas', 'MOTOR_BOATS', 386),
('Starcraft', 'MOTOR_BOATS', 387),
('Stardust Cruisers', 'MOTOR_BOATS', 388),
('Starfisher', 'MOTOR_BOATS', 389),
('Starweld', 'MOTOR_BOATS', 390),
('Statement', 'MOTOR_BOATS', 391),
('Steiger Craft', 'MOTOR_BOATS', 392),
('Stingray', 'MOTOR_BOATS', 393),
('Storebro', 'MOTOR_BOATS', 394),
('Stratos', 'MOTOR_BOATS', 395),
('Sublue', 'MOTOR_BOATS', 396),
('Sumerset', 'MOTOR_BOATS', 397),
('Sun Tracker', 'MOTOR_BOATS', 398),
('SunCatcher', 'MOTOR_BOATS', 399),
('SunChaser', 'MOTOR_BOATS', 400),
('Sundance', 'MOTOR_BOATS', 401),
('Sunreef', 'MOTOR_BOATS', 402),
('Sunsation', 'MOTOR_BOATS', 403),
('Sunseeker', 'MOTOR_BOATS', 404),
('Super Van Craft', 'MOTOR_BOATS', 405),
('Supra', 'MOTOR_BOATS', 406),
('Supreme', 'MOTOR_BOATS', 407),
('Sweetwater', 'MOTOR_BOATS', 408),
('Sylvan', 'MOTOR_BOATS', 409),
('Symbol', 'MOTOR_BOATS', 410),
('Suzuki Suzumar', 'MOTOR_BOATS', 411)
ON CONFLICT (name, category) DO NOTHING;

-- T brands
INSERT INTO brands (name, category, display_order) VALUES
('Tahoe', 'MOTOR_BOATS', 412),
('Tahoe Pontoon', 'MOTOR_BOATS', 413),
('Talamex', 'MOTOR_BOATS', 414),
('Targa', 'MOTOR_BOATS', 415),
('Technohull', 'MOTOR_BOATS', 416),
('Tecnomar', 'MOTOR_BOATS', 417),
('Terhni', 'MOTOR_BOATS', 418),
('Tesoro', 'MOTOR_BOATS', 419),
('Thor', 'MOTOR_BOATS', 420),
('Thunder Jet', 'MOTOR_BOATS', 421),
('Tiara Sport', 'MOTOR_BOATS', 422),
('Tiara Yachts', 'MOTOR_BOATS', 423),
('Tidewater', 'MOTOR_BOATS', 424),
('Tiger Marine', 'MOTOR_BOATS', 425),
('Tigé', 'MOTOR_BOATS', 426),
('Tohamaran', 'MOTOR_BOATS', 427),
('Tollycraft', 'MOTOR_BOATS', 428),
('Tracker', 'MOTOR_BOATS', 429),
('Trader', 'MOTOR_BOATS', 430),
('Trifecta', 'MOTOR_BOATS', 431),
('Triton', 'MOTOR_BOATS', 432),
('Trojan', 'MOTOR_BOATS', 433),
('Trophy', 'MOTOR_BOATS', 434),
('True North', 'MOTOR_BOATS', 435),
('Twin Vee', 'MOTOR_BOATS', 436)
ON CONFLICT (name, category) DO NOTHING;

-- U brands
INSERT INTO brands (name, category, display_order) VALUES
('Uniesse', 'MOTOR_BOATS', 437),
('Uniflite', 'MOTOR_BOATS', 438)
ON CONFLICT (name, category) DO NOTHING;

-- V brands
INSERT INTO brands (name, category, display_order) VALUES
('Valhalla Boatworks', 'MOTOR_BOATS', 439),
('Valliant', 'MOTOR_BOATS', 440),
('Van der Heijden', 'MOTOR_BOATS', 441),
('Van der Valk', 'MOTOR_BOATS', 442),
('VanDutch', 'MOTOR_BOATS', 443),
('Vanquish Yachts', 'MOTOR_BOATS', 444),
('Velocity', 'MOTOR_BOATS', 445),
('Venture', 'MOTOR_BOATS', 446),
('Veranda', 'MOTOR_BOATS', 447),
('Vexus', 'MOTOR_BOATS', 448),
('Viaggio', 'MOTOR_BOATS', 449),
('Vicem', 'MOTOR_BOATS', 450),
('Viking', 'MOTOR_BOATS', 451),
('Viking Princess', 'MOTOR_BOATS', 452),
('Vision Marine Technologies', 'MOTOR_BOATS', 453),
('Vri-Jon', 'MOTOR_BOATS', 454)
ON CONFLICT (name, category) DO NOTHING;

-- W brands
INSERT INTO brands (name, category, display_order) VALUES
('Wajer', 'MOTOR_BOATS', 455),
('Walker Bay', 'MOTOR_BOATS', 456),
('Wally', 'MOTOR_BOATS', 457),
('War Eagle', 'MOTOR_BOATS', 458),
('Weldcraft', 'MOTOR_BOATS', 459),
('Wellcraft', 'MOTOR_BOATS', 460),
('Westport', 'MOTOR_BOATS', 461),
('Whaly', 'MOTOR_BOATS', 462),
('White Shark', 'MOTOR_BOATS', 463),
('Williams Jet Tenders', 'MOTOR_BOATS', 464),
('Windy', 'MOTOR_BOATS', 465),
('World Cat', 'MOTOR_BOATS', 466)
ON CONFLICT (name, category) DO NOTHING;

-- Y brands
INSERT INTO brands (name, category, display_order) VALUES
('Yamaha Boats', 'MOTOR_BOATS', 467),
('Yamarin', 'MOTOR_BOATS', 468),
('Yellowfin', 'MOTOR_BOATS', 469)
ON CONFLICT (name, category) DO NOTHING;

-- Z brands
INSERT INTO brands (name, category, display_order) VALUES
('ZAR Mini', 'MOTOR_BOATS', 470),
('Zar', 'MOTOR_BOATS', 471),
('Zar Formenti', 'MOTOR_BOATS', 472),
('Zander', 'MOTOR_BOATS', 473),
('Zodiac', 'MOTOR_BOATS', 474)
ON CONFLICT (name, category) DO NOTHING;

-- Custom production option (always last)
INSERT INTO brands (name, category, display_order) VALUES
('Собствено производство / Други', 'MOTOR_BOATS', 9999)
ON CONFLICT (name, category) DO NOTHING;

-- Insert ALL Sailboat brands (A-Z)
INSERT INTO brands (name, category, display_order) VALUES
-- A brands
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
('Quarken', 'SAILBOATS', 182),

-- R brands
('RM Yachts', 'SAILBOATS', 183),
('RS', 'SAILBOATS', 184),
('Rapido', 'SAILBOATS', 185),
('Rhodes', 'SAILBOATS', 186),
('Rival', 'SAILBOATS', 187),
('Robert Perry', 'SAILBOATS', 188),
('Royal Cape Catamarans', 'SAILBOATS', 189),
('Royal Huisman', 'SAILBOATS', 190),
('Rustler', 'SAILBOATS', 191),

-- S brands
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
('Trintella', 'SAILBOATS', 229),

-- V brands
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
('Voyage Yachts', 'SAILBOATS', 240),

-- W brands
('Waarschip', 'SAILBOATS', 241),
('Wally', 'SAILBOATS', 242),
('Wauquiez', 'SAILBOATS', 243),
('Westerly', 'SAILBOATS', 244),
('Westsail', 'SAILBOATS', 245),
('Whitby', 'SAILBOATS', 246),
('Windelo', 'SAILBOATS', 247),
-- X brands
('X-Yachts', 'SAILBOATS', 248),
('Xquisite Yachts', 'SAILBOATS', 249),
-- Z brands
('Zeeschouw', 'SAILBOATS', 250)
ON CONFLICT (name, category) DO NOTHING;

-- Custom production option (always last)
INSERT INTO brands (name, category, display_order) VALUES
('Собствено производство / Други', 'SAILBOATS', 9999)
ON CONFLICT (name, category) DO NOTHING;

-- Insert ALL Kayak/Canoe brands
INSERT INTO brands (name, category, display_order) VALUES
-- A brands
('AB Inflatables', 'KAYAKS', 1),
('Aquasport', 'KAYAKS', 2),
('Aqua Marina', 'KAYAKS', 3),
('Ascend', 'KAYAKS', 4),
('Avid', 'KAYAKS', 5),

-- C brands
('Capelli', 'KAYAKS', 6),
('Crystal Kayak', 'KAYAKS', 7),
('Custom', 'KAYAKS', 8),

-- G brands
('G3', 'KAYAKS', 9),
('Gala', 'KAYAKS', 10),

-- H brands
('Highfield', 'KAYAKS', 11),
('Hobie', 'KAYAKS', 12),
('Hobie Cat', 'KAYAKS', 13),
('Honwave', 'KAYAKS', 14),

-- I brands
('Inmar', 'KAYAKS', 15),

-- K brands
('Kolibri', 'KAYAKS', 16),

-- L brands
('Linder', 'KAYAKS', 17),

-- N brands
('Native Watercraft', 'KAYAKS', 18),
('Nautique', 'KAYAKS', 19),

-- O brands
('Ocean Kayak', 'KAYAKS', 20),
('Old Town', 'KAYAKS', 21),

-- P brands
('Paddle King', 'KAYAKS', 22),
('Polar Kraft', 'KAYAKS', 23),

-- R brands
('RIO', 'KAYAKS', 24),

-- S brands
('Salpa', 'KAYAKS', 25),
('Sea Ray', 'KAYAKS', 26),
('Starcraft', 'KAYAKS', 27),

-- T brands
('Takacat', 'KAYAKS', 28),
('Tigé', 'KAYAKS', 29),
('TomCat', 'KAYAKS', 30),
('Tracker', 'KAYAKS', 31),
('Trailer', 'KAYAKS', 32),

-- W brands
('Williams Jet Tenders', 'KAYAKS', 33),

-- Y brands
('YAM', 'KAYAKS', 34)
ON CONFLICT (name, category) DO NOTHING;

-- Custom production option (always last)
INSERT INTO brands (name, category, display_order) VALUES
('Собствено производство / Други', 'KAYAKS', 9999)
ON CONFLICT (name, category) DO NOTHING;