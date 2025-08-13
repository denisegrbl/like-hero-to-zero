ALTER TABLE country_emissions
  ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'PENDING' AFTER created_by;

CREATE INDEX idx_status ON country_emissions(status);
CREATE INDEX idx_country_year_status ON country_emissions(country, year, status);
