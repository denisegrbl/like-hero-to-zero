package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "country_emissions"
        // Falls du global Eindeutigkeit willst, kommentiere die nächste Zeile ein:
        // , uniqueConstraints = @UniqueConstraint(name="uq_country_year", columnNames={"country","year"})
)
public class CountryEmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 120)
    @Column(name = "country", nullable = false, length = 120)
    private String country;

    @NotNull
    @Min(1900) @Max(2100)
    @Column(name = "year", nullable = false)
    private Integer year;

    @NotNull
    @PositiveOrZero
    @Column(name = "emissions_kt", nullable = false)
    private Double emissionsKt;

    // wird beim Anlegen vom Controller gesetzt (Owner)
    @Column(name = "created_by", length = 100)
    private String createdBy;

    // Moderationsstatus: PENDING / APPROVED / REJECTED
    @Column(name = "status", nullable = false, length = 20)
    private String status = "PENDING";

    // von MySQL per DEFAULT CURRENT_TIMESTAMP gesetzt
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;


    // --- Konstruktoren ---
    public CountryEmission() { }

    public CountryEmission(String country, Integer year, Double emissionsKt) {
        this.country = country;
        this.year = year;
        this.emissionsKt = emissionsKt;
    }

    // --- Getter/Setter ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Double getEmissionsKt() { return emissionsKt; }
    public void setEmissionsKt(Double emissionsKt) { this.emissionsKt = emissionsKt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
