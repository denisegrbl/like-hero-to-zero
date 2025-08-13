package com.example.demo.repo;

import com.example.demo.entity.CountryEmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CountryEmissionRepository extends JpaRepository<CountryEmission, Long> {

    // ===== Öffentliche Abfragen (nur freigegebene Datensätze) =====
    Page<CountryEmission> findByStatus(String status, Pageable pageable);
    Optional<CountryEmission> findTopByCountryAndStatusOrderByYearDesc(String country, String status);
    List<CountryEmission> findTop10ByCountryAndStatusOrderByYearDesc(String country, String status);
    List<CountryEmission> findTop5ByStatusOrderByCreatedAtDesc(String status);
    Page<CountryEmission> findByStatusOrderByCountryAscYearDesc(String status, Pageable pageable);
    Page<CountryEmission> findByCreatedByAndStatusOrderByCountryAscYearDesc(String createdBy, String status, Pageable pageable);
    Page<CountryEmission> findByCreatedByAndStatusAndCountryContainingIgnoreCase(String createdBy, String status, String q, Pageable pageable);
    // ===== Scientist-Ansicht (nur eigene Datensätze) =====
    Page<CountryEmission> findByCreatedByOrderByCountryAscYearDesc(String createdBy, Pageable pageable);

    // ===== Admin-Suche/Filter =====
    Page<CountryEmission> findByStatusAndCountryContainingIgnoreCase(String status, String countryQuery, Pageable pageable);
    Page<CountryEmission> findByCountryContainingIgnoreCase(String countryQuery, Pageable pageable);

    // ===== Allgemein/Helfer =====
    Optional<CountryEmission> findByCountryAndYear(String country, Integer year); // für Upsert/Import
    @Query("select distinct c.country from CountryEmission c order by c.country asc")
    List<String> findDistinctCountries();

    // ===== Optional: interne Queries ohne Status (nur falls irgendwo gebraucht) =====
    Optional<CountryEmission> findTopByCountryOrderByYearDesc(String country);
    List<CountryEmission> findTop10ByCountryOrderByYearDesc(String country);
    Page<CountryEmission> findAllByOrderByCountryAscYearDesc(Pageable pageable);
}
