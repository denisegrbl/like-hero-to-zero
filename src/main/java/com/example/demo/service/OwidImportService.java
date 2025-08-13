package com.example.demo.service;

import com.example.demo.entity.CountryEmission;
import com.example.demo.repo.CountryEmissionRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class OwidImportService {

    private final CountryEmissionRepository repo;

    public OwidImportService(CountryEmissionRepository repo) {
        this.repo = repo;
    }

    private boolean currentUserIsAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    public int importFromMultipart(MultipartFile file) throws Exception {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        final boolean isAdmin = currentUserIsAdmin(auth);
        final String username = auth != null ? auth.getName() : "system";

        var csv = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setTrim(true)
                .build();

        int processed = 0;

        try (var reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)) {
            for (CSVRecord r : csv.parse(reader)) {
                String iso = get(r, "iso_code");
                String country = get(r, "country");
                String yearStr = get(r, "year");
                String co2MtStr = get(r, "co2");

                // Minimalvalidierung
                if (country == null || country.isBlank() ||
                        yearStr == null || yearStr.isBlank() ||
                        co2MtStr == null || co2MtStr.isBlank()) {
                    continue;
                }
                // ISO optional, aber wenn vorhanden würde ich nur 3-letter akzeptieren
                if (iso != null && !iso.isBlank() && iso.length() != 3) {
                    continue;
                }

                int year;
                double co2Mt;
                try {
                    year = Integer.parseInt(yearStr);
                    co2Mt = Double.parseDouble(co2MtStr);
                } catch (NumberFormatException ex) {
                    continue;
                }

                double co2Kt = co2Mt * 1000.0;

                Optional<CountryEmission> existing = repo.findByCountryAndYear(country, year);
                CountryEmission e = existing.orElseGet(() -> new CountryEmission(country, year, co2Kt));
                e.setCountry(country);
                e.setYear(year);
                e.setEmissionsKt(co2Kt);

                if (isAdmin) {
                    e.setStatus("APPROVED");
                    if (e.getCreatedBy() == null) e.setCreatedBy("admin"); // optional
                } else {
                    // Scientist → PENDING und Owner setzen (falls neu)
                    if (existing.isEmpty()) {
                        e.setStatus("PENDING");
                        e.setCreatedBy(username);
                    } else {
                        // bestehende offizielle Zahl NICHT automatisch unsichtbar machen
                        // bei bestehenden Datensätzen nichts am Status ändern
                    }
                }

                repo.save(e);
                processed++;
            }
        }
        return processed;
    }

    private static String get(CSVRecord r, String header) {
        try {
            return r.isMapped(header) ? r.get(header) : null;
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
