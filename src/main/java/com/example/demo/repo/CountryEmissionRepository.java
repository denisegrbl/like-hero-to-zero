package com.example.demo.repo;

import com.example.demo.entity.CountryEmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CountryEmissionRepository extends JpaRepository<CountryEmission, Long> {

    // öffentlich
    Page<CountryEmission> findByStatusOrderByCountryAscYearDesc(String status, Pageable pageable);
    Optional<CountryEmission> findTopByCountryAndStatusOrderByYearDesc(String country, String status);
    List<CountryEmission> findTop10ByCountryAndStatusOrderByYearDesc(String country, String status);
    List<CountryEmission> findTop5ByStatusOrderByCreatedAtDesc(String status);
    Page<CountryEmission> findByStatus(String status, Pageable pageable);
    // scientist (eigene)
    Page<CountryEmission> findByCreatedByAndStatusOrderByCountryAscYearDesc(String createdBy, String status, Pageable pageable);
    Page<CountryEmission> findByCreatedByAndStatusAndCountryContainingIgnoreCase(String createdBy, String status, String q, Pageable pageable);

    // admin (alle)
    Page<CountryEmission> findByStatusAndCountryContainingIgnoreCase(String status, String q, Pageable pageable);
    Page<CountryEmission> findByCountryContainingIgnoreCase(String q, Pageable pageable);
    boolean existsByCountryAndYear(String country, Integer year);
    // wichtig fürs Update: gleiche (country,year), aber NICHT diese id
    boolean existsByCountryAndYearAndIdNot(String country, Integer year, Long id);
    // helper
    Optional<CountryEmission> findByCountryAndYear(String country, Integer year);
    @Query("select distinct c.country from CountryEmission c order by c.country asc")
    List<String> findDistinctCountries();

    // optional
    Optional<CountryEmission> findTopByCountryOrderByYearDesc(String country);
    List<CountryEmission> findTop10ByCountryOrderByYearDesc(String country);
    Page<CountryEmission> findAllByOrderByCountryAscYearDesc(Pageable pageable);
}
