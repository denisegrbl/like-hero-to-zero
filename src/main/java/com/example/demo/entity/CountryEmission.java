package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "country_emissions",
        uniqueConstraints = @UniqueConstraint(name = "uq_country_year",
                columnNames = {"country","year"}))
public class CountryEmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=120)
    private String country;

    @Column(nullable=false)
    private Integer year;

    @Column(name="emissions_kt", nullable=false)
    private Double emissionsKt; // CO₂ in Kilotonnen

    public CountryEmission() {}
    public CountryEmission(String country, Integer year, Double emissionsKt) {
        this.country = country; this.year = year; this.emissionsKt = emissionsKt;
    }

    public Long getId() { return id; }
    public String getCountry() { return country; }
    public Integer getYear() { return year; }
    public Double getEmissionsKt() { return emissionsKt; }

    public void setId(Long id) { this.id = id; }
    public void setCountry(String country) { this.country = country; }
    public void setYear(Integer year) { this.year = year; }
    public void setEmissionsKt(Double emissionsKt) { this.emissionsKt = emissionsKt; }
}
