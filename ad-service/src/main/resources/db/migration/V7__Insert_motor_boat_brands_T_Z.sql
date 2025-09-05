-- V3e__Insert_motor_boat_brands_T_Z.sql
-- Motor boat brands T-Z and custom option (Part 5 of 6)

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