package com.example.demo.web;

import com.example.demo.repo.CountryEmissionRepository;
import com.example.demo.entity.CountryEmission;
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
    public String list(@RequestParam(defaultValue="0") int page,
                       @RequestParam(defaultValue="20") int size,
                       @RequestParam(name="q", required=false) String q,
                       Model model) {
        var sort = org.springframework.data.domain.Sort.by("country").ascending()
                .and(org.springframework.data.domain.Sort.by("year").descending());
        var pageable = org.springframework.data.domain.PageRequest.of(page, size, sort);

        org.springframework.data.domain.Page<com.example.demo.entity.CountryEmission> p =
                (q == null || q.isBlank())
                        ? repo.findByStatusOrderByCountryAscYearDesc("APPROVED", pageable)
                        : repo.findByStatusAndCountryContainingIgnoreCase("APPROVED", q.trim(), pageable);

        model.addAttribute("page", p);
        model.addAttribute("emissions", p.getContent());
        model.addAttribute("q", q == null ? "" : q.trim());
        model.addAttribute("size", size);
        return "emissions";
    }


}
