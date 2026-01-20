
ALTER TABLE trial_registrations
ADD COLUMN gender TEXT NOT NULL DEFAULT '';

-- Add current club column (optional)
ALTER TABLE trial_registrations
ADD COLUMN current_club TEXT;

-- Add years in club column (optional)
ALTER TABLE trial_registrations
ADD COLUMN years_in_club INTEGER;