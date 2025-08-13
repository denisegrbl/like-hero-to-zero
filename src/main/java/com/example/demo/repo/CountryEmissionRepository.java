package com.example.demo.repo;

import com.example.demo.entity.CountryEmission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CountryEmissionRepository extends JpaRepository<CountryEmission, Long> {
    List<CountryEmission> findByCountryOrderByYearAsc(String country);
    Optional<CountryEmission> findTopByCountryOrderByYearDesc(String country);
}
