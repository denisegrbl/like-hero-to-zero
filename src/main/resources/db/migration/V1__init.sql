CREATE TABLE country_emissions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  country VARCHAR(120) NOT NULL,
  year INT NOT NULL,
  emissions_kt DOUBLE NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_country_year UNIQUE (country, year)
);
