package com.example.demo.web;

import com.example.demo.entity.CountryEmission;
import com.example.demo.repo.CountryEmissionRepository;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/emissions")
@PreAuthorize("hasRole('ADMIN')")
public class AdminEmissionController {

    private final CountryEmissionRepository repo;

    public AdminEmissionController(CountryEmissionRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        CountryEmission e = repo.findById(id).orElseThrow();
        model.addAttribute("emission", e);
        model.addAttribute("title", "Admin: Datensatz bearbeiten");
        return "admin/form";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("emission") CountryEmission form,
                         BindingResult br,
                         Model model) {

        // Duplikate abfangen (gleiches Land+Jahr bei anderem Datensatz)
        if (repo.existsByCountryAndYearAndIdNot(form.getCountry(), form.getYear(), id)) {
            br.reject("duplicate", "Für dieses Land existiert bereits ein Wert für dieses Jahr.");
        }

        if (br.hasErrors()) {
            model.addAttribute("title", "Admin: Datensatz bearbeiten");
            return "admin/form";
        }

        CountryEmission e = repo.findById(id).orElseThrow();
        e.setCountry(form.getCountry());
        e.setYear(form.getYear());
        e.setEmissionsKt(form.getEmissionsKt());
        if (form.getStatus() != null) { // Admin darf Status setzen
            e.setStatus(form.getStatus());
        }
        repo.saveAndFlush(e);

        return "redirect:/admin/moderation";
    }
}
