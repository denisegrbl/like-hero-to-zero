package com.example.demo.web;

import com.example.demo.entity.CountryEmission;
import com.example.demo.repo.CountryEmissionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/country")
public class CountryController {

    private final CountryEmissionRepository repo;

    public CountryController(CountryEmissionRepository repo) {
        this.repo = repo;
    }

    // Formular zur Auswahl des Landes
    @GetMapping
    public String chooseCountry(@RequestParam(value = "name", required = false) String country, Model model) {
        List<String> countries = repo.findDistinctCountries();
        model.addAttribute("countries", countries);

        if (country != null && !country.isBlank()) {
            var latestOpt = repo.findTopByCountryOrderByYearDesc(country);
            model.addAttribute("selected", country);
            model.addAttribute("latest", latestOpt.orElse(null));
            model.addAttribute("recent", repo.findTop10ByCountryOrderByYearDesc(country)); // kleiner Verlauf
        }
        return "country";
    }

    // Schöne URL: /country/Deutschland  (URL-encoded)
    @GetMapping("/{country}")
    public String countryDetail(@PathVariable String country, Model model) {
        List<String> countries = repo.findDistinctCountries();
        model.addAttribute("countries", countries);
        model.addAttribute("selected", country);
        model.addAttribute("latest", repo.findTopByCountryOrderByYearDesc(country).orElse(null));
        model.addAttribute("recent", repo.findTop10ByCountryOrderByYearDesc(country));
        return "country";
    }
}
