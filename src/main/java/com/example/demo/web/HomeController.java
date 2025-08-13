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
        model.addAttribute("latest5", repo.findTop5ByOrderByCreatedAtDesc());
        return "index";
    }
}
