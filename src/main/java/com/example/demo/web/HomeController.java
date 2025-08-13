package com.example.demo.web;

import com.example.demo.repo.CountryEmissionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final CountryEmissionRepository repo;

    public HomeController(CountryEmissionRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/")
    public String index(Model model) {
        // Neueste 10 nach Jahr (und Land als Zweitsortierung, damit die Reihenfolge stabil ist)
        var p = repo.findByStatus(
                "APPROVED",
                org.springframework.data.domain.PageRequest.of(
                        0, 10,
                        org.springframework.data.domain.Sort.by("year").descending()
                                .and(org.springframework.data.domain.Sort.by("country").ascending())
                )
        );
        model.addAttribute("latest10", p.getContent());
        return "index";
    }

}
