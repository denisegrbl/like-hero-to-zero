package com.example.demo.service;

import com.example.demo.entity.CountryEmission;
import com.example.demo.repo.CountryEmissionRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

@Service
public class OwidImportService {

    private final CountryEmissionRepository repo;

    public OwidImportService(CountryEmissionRepository repo) {
        this.repo = repo;
    }

    /** Liest /import/owid-co2-data.csv, nimmt Spalten country, year, co2 (Mt) und speichert als kt */
    @Transactional
    public int importLocalCsv() throws Exception {
        InputStream is = getClass().getResourceAsStream("/import/owid-co2-data.csv");
        if (is == null) {
            throw new IllegalStateException("CSV nicht gefunden: /import/owid-co2-data.csv (liegt sie unter src/main/resources/import/ ?)");
        }

        try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setHeader()              // Header aus der ersten Zeile nutzen
                     .setSkipHeaderRecord(true) // Header nicht als Datensatz interpretieren
                     .build()
                     .parse(reader)) {

            int processed = 0;

            for (CSVRecord r : parser) {
                // Nur echte Länderzeilen (iso_code mit 3 Zeichen; Aggregate wie OWID_WRL ignorieren)
                String iso = r.get("iso_code");
                if (iso == null || iso.length() != 3) continue;

                String country = r.get("country");
                String yearStr = r.get("year");
                String co2MtStr = r.get("co2"); // CO₂ in Millionen Tonnen (Mt)

                if (country == null || country.isBlank()
                        || yearStr == null || yearStr.isBlank()
                        || co2MtStr == null || co2MtStr.isBlank()) {
                    continue;
                }

                int year;
                double co2Mt;
                try {
                    year = Integer.parseInt(yearStr);
                    co2Mt = Double.parseDouble(co2MtStr);
                } catch (NumberFormatException ex) {
                    continue; // Zeile mit „NA“ etc. überspringen
                }

                double co2Kt = co2Mt * 1000.0; // Mt → kt

                var existing = repo.findByCountryAndYear(country, year);
                if (existing.isPresent()) {
                    var e = existing.get();
                    e.setEmissionsKt(co2Kt);
                    repo.save(e);
                } else {
                    repo.save(new CountryEmission(country, year, co2Kt));
                }
                processed++;
            }
            return processed;
        }
    }
}
