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
    public String list(@RequestParam(defaultValue = "0", name="pageP") int pagePending,
                       @RequestParam(defaultValue = "0", name="pageA") int pageApproved,
                       @RequestParam(defaultValue = "20") int size,
                       @RequestParam(name="q", required = false) String q,
                       Model model) {

        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String owner = auth.getName();
        String query = (q == null) ? "" : q.trim();

        var sort = org.springframework.data.domain.Sort.by("country").ascending()
                .and(org.springframework.data.domain.Sort.by("year").descending());
        var pageableP = org.springframework.data.domain.PageRequest.of(pagePending, size, sort);
        var pageableA = org.springframework.data.domain.PageRequest.of(pageApproved, size, sort);

        org.springframework.data.domain.Page<com.example.demo.entity.CountryEmission> pPending, pApproved;

        if (isAdmin) {
            pPending = query.isEmpty()
                    ? repo.findByStatusOrderByCountryAscYearDesc("PENDING", pageableP)
                    : repo.findByStatusAndCountryContainingIgnoreCase("PENDING", query, pageableP);

            pApproved = query.isEmpty()
                    ? repo.findByStatusOrderByCountryAscYearDesc("APPROVED", pageableA)
                    : repo.findByStatusAndCountryContainingIgnoreCase("APPROVED", query, pageableA);
        } else {
            pPending = query.isEmpty()
                    ? repo.findByCreatedByAndStatusOrderByCountryAscYearDesc(owner, "PENDING", pageableP)
                    : repo.findByCreatedByAndStatusAndCountryContainingIgnoreCase(owner, "PENDING", query, pageableP);

            pApproved = query.isEmpty()
                    ? repo.findByCreatedByAndStatusOrderByCountryAscYearDesc(owner, "APPROVED", pageableA)
                    : repo.findByCreatedByAndStatusAndCountryContainingIgnoreCase(owner, "APPROVED", query, pageableA);
        }

        model.addAttribute("q", q);
        model.addAttribute("size", size);
        model.addAttribute("pageP", pagePending);
        model.addAttribute("pageA", pageApproved);
        model.addAttribute("isAdmin", isAdmin);

        model.addAttribute("pPending", pPending);
        model.addAttribute("pApproved", pApproved);
        model.addAttribute("listPending", pPending.getContent());
        model.addAttribute("listApproved", pApproved.getContent());

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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        emission.setCreatedBy(auth.getName());
        emission.setStatus("PENDING");   // NEU immer PENDING
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
                         org.springframework.validation.BindingResult binding) {
        if (binding.hasErrors()) return "manage/form";
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        boolean admin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        var e = repo.findById(id).orElseThrow();

        if (!admin) {
            if (e.getCreatedBy() == null || !e.getCreatedBy().equals(auth.getName()))
                throw new org.springframework.security.access.AccessDeniedException("Kein Zugriff");
        }

        e.setCountry(form.getCountry());
        e.setYear(form.getYear());
        e.setEmissionsKt(form.getEmissionsKt());

        if (!admin) {
            e.setStatus("PENDING");  // Scientist-Edit ⇒ wieder PENDING
        }
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
