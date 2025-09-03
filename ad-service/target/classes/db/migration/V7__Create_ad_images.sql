-- V7__Create_ad_images.sql
-- Ad images table for image management functionality

CREATE TABLE ad_images (
    id BIGSERIAL PRIMARY KEY,
    ad_id BIGINT NOT NULL REFERENCES ads(id) ON DELETE CASCADE,
    file_name VARCHAR(255) NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    s3_key VARCHAR(500) NOT NULL UNIQUE,
    s3_url VARCHAR(1000) NOT NULL,
    content_type VARCHAR(50) NOT NULL,
    file_size BIGINT NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,
    width INTEGER,
    height INTEGER,
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    uploaded_by VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,

    CONSTRAINT idx_ad_display_order UNIQUE (ad_id, display_order)
);

-- Primary indexes for ad_images table
CREATE INDEX idx_ad_images_ad_id ON ad_images(ad_id);
CREATE INDEX idx_ad_images_display_order ON ad_images(ad_id, display_order);
CREATE INDEX idx_ad_images_uploaded_by ON ad_images(uploaded_by);
CREATE INDEX idx_ad_images_active ON ad_images(active);

-- Additional indexes for new functionality
CREATE INDEX idx_ad_images_max_display_order ON ad_images(ad_id, display_order DESC) WHERE active = true;
CREATE INDEX idx_ad_images_user_ad ON ad_images(uploaded_by, ad_id) WHERE active = true;