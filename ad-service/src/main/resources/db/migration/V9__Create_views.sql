-- V9__Create_views.sql
-- Views for common queries

-- View for active ads
CREATE OR REPLACE VIEW active_ads AS
SELECT * FROM ads WHERE active = true;

-- View for featured ads
CREATE OR REPLACE VIEW featured_ads AS
SELECT * FROM ads WHERE active = true AND featured = true;