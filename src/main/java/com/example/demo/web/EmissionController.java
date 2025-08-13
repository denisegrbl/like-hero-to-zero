package com.example.demo.web;

import com.example.demo.repo.CountryEmissionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EmissionController {

    private final CountryEmissionRepository repo;

    public EmissionController(CountryEmissionRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/emissions")
    public String list(Model model) {
        model.addAttribute("emissions", repo.findAll());
        return "emissions";
    }
}
