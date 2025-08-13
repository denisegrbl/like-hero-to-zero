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

    // LISTE: eigene PENDING + eigene APPROVED (jeweils paginiert + Suche)
    @GetMapping
    public String list(@RequestParam(defaultValue="0", name="pageP") int pageP,
                       @RequestParam(defaultValue="0", name="pageA") int pageA,
                       @RequestParam(defaultValue="20") int size,
                       @RequestParam(name="q", required=false) String q,
                       Authentication auth,
                       Model model) {

        String user = auth.getName();
        var sort = Sort.by("country").ascending().and(Sort.by("year").descending());
        var pageableP = PageRequest.of(pageP, size, sort);
        var pageableA = PageRequest.of(pageA, size, sort);

        Page<CountryEmission> pPending =
                (q == null || q.isBlank())
                        ? repo.findByCreatedByAndStatusOrderByCountryAscYearDesc(user, "PENDING", pageableP)
                        : repo.findByCreatedByAndStatusAndCountryContainingIgnoreCase(user, "PENDING", q.trim(), pageableP);

        Page<CountryEmission> pApproved =
                (q == null || q.isBlank())
                        ? repo.findByCreatedByAndStatusOrderByCountryAscYearDesc(user, "APPROVED", pageableA)
                        : repo.findByCreatedByAndStatusAndCountryContainingIgnoreCase(user, "APPROVED", q.trim(), pageableA);

        model.addAttribute("pPending", pPending);
        model.addAttribute("pApproved", pApproved);
        model.addAttribute("listPending", pPending.getContent());
        model.addAttribute("listApproved", pApproved.getContent());
        model.addAttribute("pageP", pageP);
        model.addAttribute("pageA", pageA);
        model.addAttribute("size", size);
        model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("isAdmin", false); // für Template-Bedingungen
        return "manage/list";
    }

    // NEU
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("emission", new CountryEmission());
        model.addAttribute("title", "Neuer Datensatz");
        return "manage/form";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("emission") CountryEmission e,
                         BindingResult br,
                         Authentication auth) {
        if (br.hasErrors()) return "manage/form";
        e.setId(null);
        e.setCreatedBy(auth.getName());
        e.setStatus("PENDING"); // neu => PENDING
        repo.save(e);
        return "redirect:/manage/emissions";
    }

    // EDIT (nur eigene!)
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Authentication auth, Model model) {
        var e = repo.findById(id).orElseThrow();
        if (!auth.getName().equals(e.getCreatedBy())) {
            // optional: 403 oder Redirect
            return "redirect:/manage/emissions";
        }
        model.addAttribute("emission", e);
        model.addAttribute("title", "Datensatz bearbeiten");
        return "manage/form";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("emission") CountryEmission form,
                         BindingResult br,
                         Authentication auth) {
        if (br.hasErrors()) return "manage/form";
        var e = repo.findById(id).orElseThrow();
        if (!auth.getName().equals(e.getCreatedBy())) {
            return "redirect:/manage/emissions";
        }
        e.setCountry(form.getCountry());
        e.setYear(form.getYear());
        e.setEmissionsKt(form.getEmissionsKt());
        e.setStatus("PENDING"); // jede Änderung => wieder PENDING
        repo.save(e);
        return "redirect:/manage/emissions";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Authentication auth) {
        var e = repo.findById(id).orElseThrow();
        if (auth.getName().equals(e.getCreatedBy())) {
            repo.delete(e);
        }
        return "redirect:/manage/emissions";
    }
}
