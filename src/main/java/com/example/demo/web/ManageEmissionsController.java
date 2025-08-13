package com.example.demo.web;

import com.example.demo.entity.CountryEmission;
import com.example.demo.repo.CountryEmissionRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/manage/emissions")
public class ManageEmissionsController {

    private final CountryEmissionRepository repo;

    public ManageEmissionsController(CountryEmissionRepository repo) {
        this.repo = repo;
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       Model model) {
        var pageable = PageRequest.of(page, size,
                Sort.by("country").ascending().and(Sort.by("year").descending()));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Page<CountryEmission> p = isAdmin(auth)
                ? repo.findAll(pageable) // Admin sieht alles
                : repo.findByCreatedByOrderByCountryAscYearDesc(auth.getName(), pageable); // Scientist: nur eigene

        model.addAttribute("page", p);
        model.addAttribute("content", p.getContent());
        model.addAttribute("owner", auth.getName());
        model.addAttribute("isAdmin", isAdmin(auth));
        return "manage/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("emission", new CountryEmission());
        return "manage/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("emission") CountryEmission emission,
                         BindingResult binding) {
        if (binding.hasErrors()) return "manage/form";
        var auth = SecurityContextHolder.getContext().getAuthentication();
        emission.setCreatedBy(auth.getName());          // ← Owner setzen
        repo.save(emission);
        return "redirect:/manage/emissions";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        var e = repo.findById(id).orElseThrow();
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (!isAdmin(auth) && (e.getCreatedBy() == null || !e.getCreatedBy().equals(auth.getName()))) {
            throw new AccessDeniedException("Kein Zugriff auf fremde Datensätze");
        }
        model.addAttribute("emission", e);
        return "manage/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("emission") CountryEmission form,
                         BindingResult binding) {
        if (binding.hasErrors()) return "manage/form";
        var e = repo.findById(id).orElseThrow();
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (!isAdmin(auth) && (e.getCreatedBy() == null || !e.getCreatedBy().equals(auth.getName()))) {
            throw new AccessDeniedException("Kein Zugriff auf fremde Datensätze");
        }
        e.setCountry(form.getCountry());
        e.setYear(form.getYear());
        e.setEmissionsKt(form.getEmissionsKt());
        repo.save(e);
        return "redirect:/manage/emissions";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        var e = repo.findById(id).orElseThrow();
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (!isAdmin(auth) && (e.getCreatedBy() == null || !e.getCreatedBy().equals(auth.getName()))) {
            throw new AccessDeniedException("Kein Zugriff auf fremde Datensätze");
        }
        repo.delete(e);
        return "redirect:/manage/emissions";
    }
}
