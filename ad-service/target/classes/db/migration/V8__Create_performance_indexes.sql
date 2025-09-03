-- V8__Create_performance_indexes.sql
-- Additional performance indexes for query optimization

-- Additional ads table indexes
CREATE INDEX idx_ads_price_type ON ads(price_type);
CREATE INDEX idx_ads_price_amount ON ads(price_amount) WHERE price_type = 'FIXED_PRICE';
CREATE INDEX idx_ads_location ON ads(location);
CREATE INDEX idx_ads_views_count ON ads(views_count DESC);
CREATE INDEX idx_ads_featured ON ads(featured);
CREATE INDEX idx_ads_archived ON ads(archived);
CREATE INDEX idx_ads_user_archived ON ads(user_id, archived);
CREATE INDEX idx_ads_archived_at ON ads(archived_at);
CREATE INDEX idx_ads_last_edited ON ads(last_edited_at);
CREATE INDEX idx_ads_approval_status ON ads(approval_status);
CREATE INDEX idx_ads_approved_by ON ads(approved_by_user_id);
CREATE INDEX idx_ads_approved_at ON ads(approved_at);

-- Composite indexes for common queries
CREATE INDEX idx_ads_category_active_created ON ads(category, active, created_at DESC);
CREATE INDEX idx_ads_location_active_created ON ads(location, active, created_at DESC);
CREATE INDEX idx_ads_user_active_created ON ads(user_email, active, created_at DESC);

-- Cascade delete performance indexes for specification updates
CREATE INDEX idx_boat_specifications_ad_id_cascade ON boat_specifications(ad_id);
CREATE INDEX idx_jetski_specifications_ad_id_cascade ON jetski_specifications(ad_id);
CREATE INDEX idx_trailer_specifications_ad_id_cascade ON trailer_specifications(ad_id);
CREATE INDEX idx_engine_specifications_ad_id_cascade ON engine_specifications(ad_id);
CREATE INDEX idx_marine_electronics_specifications_ad_id_cascade ON marine_electronics_specifications(ad_id);
CREATE INDEX idx_fishing_specifications_ad_id_cascade ON fishing_specifications(ad_id);
CREATE INDEX idx_parts_specifications_ad_id_cascade ON parts_specifications(ad_id);
CREATE INDEX idx_services_specifications_ad_id_cascade ON services_specifications(ad_id);

-- Text search indexes (if using PostgreSQL full-text search)
CREATE INDEX idx_ads_text_search ON ads USING gin(to_tsvector('english', title || ' ' || description));