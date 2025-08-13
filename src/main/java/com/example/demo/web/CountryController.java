package com.example.demo.web;

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

    // Formular zur Auswahl des Landes: GET /country?name=Germany
    @GetMapping
    public String chooseCountry(@RequestParam(value = "name", required = false) String country,
                                Model model) {
        List<String> countries = repo.findDistinctCountries();
        model.addAttribute("countries", countries);

        if (country != null && !country.isBlank()) {
            var latest = repo.findTopByCountryAndStatusOrderByYearDesc(country, "APPROVED");
            var recent = repo.findTop10ByCountryAndStatusOrderByYearDesc(country, "APPROVED");

            model.addAttribute("selected", country);
            model.addAttribute("latest", latest.orElse(null));
            model.addAttribute("recent", recent);
        }
        return "country";
    }

    // Schöne URL: /country/Germany (Spring decodiert automatisch)
    @GetMapping("/{country}")
    public String countryDetail(@PathVariable String country, Model model) {
        List<String> countries = repo.findDistinctCountries();
        model.addAttribute("countries", countries);
        model.addAttribute("selected", country);
        model.addAttribute("latest",
                repo.findTopByCountryAndStatusOrderByYearDesc(country, "APPROVED").orElse(null));
        model.addAttribute("recent",
                repo.findTop10ByCountryAndStatusOrderByYearDesc(country, "APPROVED"));
        return "country";
    }
}
