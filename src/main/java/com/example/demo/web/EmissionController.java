package com.example.demo.web;

import com.example.demo.repo.CountryEmissionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EmissionController {

    private final CountryEmissionRepository repo;

    public EmissionController(CountryEmissionRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/emissions")
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       Model model) {
        var pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 200),
                Sort.by("country").ascending().and(Sort.by("year").descending())
        );
        Page<?> p = repo.findAll(pageable); // oder: repo.findAllByOrderByCountryAscYearDesc(pageable)
        model.addAttribute("page", p);
        model.addAttribute("emissions", p.getContent());
        return "emissions";
    }
}
