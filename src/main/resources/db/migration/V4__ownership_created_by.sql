ALTER TABLE country_emissions
  ADD COLUMN created_by VARCHAR(100) NULL AFTER emissions_kt;

CREATE INDEX idx_country_created_by ON country_emissions(created_by);
