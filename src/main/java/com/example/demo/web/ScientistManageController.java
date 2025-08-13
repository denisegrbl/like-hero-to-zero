package com.example.demo.web;

import com.example.demo.entity.CountryEmission;
import com.example.demo.repo.CountryEmissionRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/manage/emissions")
@PreAuthorize("hasRole('SCIENTIST')")
public class ScientistManageController {

    private final CountryEmissionRepository repo;

    public ScientistManageController(CountryEmissionRepository repo) {
        this.repo = repo;
    }

    // LISTE: eigene Pending + eigene Approved (mit Suche/Paging)
    @GetMapping
    public String list(@RequestParam(defaultValue = "0", name = "pageP") int pageP,
                       @RequestParam(defaultValue = "0", name = "pageA") int pageA,
                       @RequestParam(defaultValue = "20") int size,
                       @RequestParam(name = "q", required = false) String q,
                       Authentication auth,
                       Model model) {

        String user = auth.getName();
        Sort sort = Sort.by("country").ascending().and(Sort.by("year").descending());
        Pageable pageableP = PageRequest.of(pageP, size, sort);
        Pageable pageableA = PageRequest.of(pageA, size, sort);

        Page<CountryEmission> pPending = (q == null || q.isBlank())
                ? repo.findByCreatedByAndStatusOrderByCountryAscYearDesc(user, "PENDING", pageableP)
                : repo.findByCreatedByAndStatusAndCountryContainingIgnoreCase(user, "PENDING", q.trim(), pageableP);

        Page<CountryEmission> pApproved = (q == null || q.isBlank())
                ? repo.findByCreatedByAndStatusOrderByCountryAscYearDesc(user, "APPROVED", pageableA)
                : repo.findByCreatedByAndStatusAndCountryContainingIgnoreCase(user, "APPROVED", q.trim(), pageableA);

        model.addAttribute("pPending", pPending);
        model.addAttribute("pApproved", pApproved);
        model.addAttribute("listPending", pPending.getContent());
        model.addAttribute("listApproved", pApproved.getContent());
        model.addAttribute("pageP", pageP);
        model.addAttribute("pageA", pageA);
        model.addAttribute("size", size);
        model.addAttribute("q", q == null ? "" : q.trim());
        model.addAttribute("isAdmin", false);
        return "manage/list";
    }

    // Formular neu
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("emission", new CountryEmission());
        model.addAttribute("title", "Neuer Datensatz");
        return "manage/form";
    }

    // Anlegen
    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("emission") CountryEmission form,
                         BindingResult br,
                         Authentication auth,
                         Model model) {
        if (br.hasErrors()) return "manage/form";

        String user = auth.getName();
        var existingOpt = repo.findByCountryAndYear(form.getCountry(), form.getYear());

        if (existingOpt.isPresent()) {
            var existing = existingOpt.get();
            // Falls der Eintrag bereits DIR gehört → Update statt Insert
            if (user.equals(existing.getCreatedBy())) {
                existing.setEmissionsKt(form.getEmissionsKt());
                existing.setStatus("PENDING"); // jede Änderung → wieder prüfen
                repo.save(existing);
                return "redirect:/manage/emissions";
            }
            // Andernfalls (CSV/Admin/anderer Benutzer) → sauberer Formularfehler statt 500
            br.rejectValue("year", "duplicate",
                    "Für dieses Land und Jahr existiert bereits ein Datensatz.");
            model.addAttribute("title", "Neuer Datensatz");
            return "manage/form";
        }

        // Neu und frei → anlegen
        var e = new CountryEmission();
        e.setCountry(form.getCountry());
        e.setYear(form.getYear());
        e.setEmissionsKt(form.getEmissionsKt());
        e.setCreatedBy(user);
        e.setStatus("PENDING");
        repo.save(e);

        return "redirect:/manage/emissions";
    }

    // Formular bearbeiten (nur eigene)
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Authentication auth, Model model) {
        var e = repo.findById(id).orElseThrow();
        if (!auth.getName().equals(e.getCreatedBy())) {
            return "redirect:/manage/emissions";
        }
        model.addAttribute("emission", e);
        model.addAttribute("title", "Datensatz bearbeiten");
        return "manage/form";
    }

    // Update (nur eigene) -> zurück auf PENDING
    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("emission") CountryEmission form,
                         BindingResult br,
                         Authentication auth) {
        if (br.hasErrors()) return "manage/form";
        var e = repo.findById(id).orElseThrow();
        if (!auth.getName().equals(e.getCreatedBy())) {
            // fremder Datensatz → zurück
            return "redirect:/manage/emissions";
        }
        e.setCountry(form.getCountry());
        e.setYear(form.getYear());
        e.setEmissionsKt(form.getEmissionsKt());
        e.setStatus("PENDING");
        repo.save(e);
        return "redirect:/manage/emissions";
    }

    // Löschen (nur eigene)
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Authentication auth) {
        var e = repo.findById(id).orElseThrow();
        if (auth.getName().equals(e.getCreatedBy())) {
            repo.delete(e);
        }
        return "redirect:/manage/emissions";
    }
}
