-- V5__Add_contact_fields_to_ads.sql
-- Add missing contact fields to match the form

ALTER TABLE ads
ADD COLUMN IF NOT EXISTS contact_person_name VARCHAR(100),
ADD COLUMN IF NOT EXISTS contact_phone VARCHAR(20),
ADD COLUMN IF NOT EXISTS website VARCHAR(200);

-- Add indexes for new fields
CREATE INDEX IF NOT EXISTS idx_ads_contact_phone ON ads(contact_phone);
CREATE INDEX IF NOT EXISTS idx_ads_website ON ads(website) WHERE website IS NOT NULL;

-- Add comments for documentation
COMMENT ON COLUMN ads.contact_person_name IS 'Contact person name from the ad form (ЛИЦЕ ЗА КОНТАКТ)';
COMMENT ON COLUMN ads.contact_phone IS 'Contact phone number from the ad form (ТЕЛЕФОНЕН НОМЕР)';
COMMENT ON COLUMN ads.website IS 'Website URL from the ad form (САЙТ)';

-- Add constraint for phone number format (optional)
ALTER TABLE ads ADD CONSTRAINT check_contact_phone_format
CHECK (contact_phone IS NULL OR contact_phone ~ '^\\+?[0-9\\s\\-\\(\\)]{7,20}$');