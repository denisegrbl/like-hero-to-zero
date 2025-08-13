package com.example.demo.repo;

import com.example.demo.entity.CountryEmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface CountryEmissionRepository extends JpaRepository<CountryEmission, Long> {

    // Für den Import (Upsert)
    Optional<CountryEmission> findByCountryAndYear(String country, Integer year);

    // Für MUST #1 (aktuellster Wert + Verlauf)
    Optional<CountryEmission> findTopByCountryOrderByYearDesc(String country);

    List<CountryEmission> findTop10ByCountryOrderByYearDesc(String country);

    List<CountryEmission> findTop5ByOrderByCreatedAtDesc();

    Page<CountryEmission> findByCreatedByOrderByCountryAscYearDesc(String createdBy, Pageable pageable);

    @Query("select distinct c.country from CountryEmission c order by c.country asc")
    List<String> findDistinctCountries();

}
