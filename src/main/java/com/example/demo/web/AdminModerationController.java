package com.example.demo.web;

import com.example.demo.entity.CountryEmission;
import com.example.demo.repo.CountryEmissionRepository;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/moderation")
@PreAuthorize("hasRole('ADMIN')")
public class AdminModerationController {

    private final CountryEmissionRepository repo;

    public AdminModerationController(CountryEmissionRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0", name="pageP") int pagePending,
                       @RequestParam(defaultValue = "0", name="pageA") int pageApproved,
                       @RequestParam(defaultValue = "20") int size,
                       @RequestParam(name="q", required = false) String q,
                       Model model) {

        Sort sort = Sort.by("country").ascending().and(Sort.by("year").descending());
        Pageable pageableP = PageRequest.of(pagePending, size, sort);
        Pageable pageableA = PageRequest.of(pageApproved, size, sort);

        Page<CountryEmission> pending = (q == null || q.isBlank())
                ? repo.findByStatusOrderByCountryAscYearDesc("PENDING", pageableP)
                : repo.findByStatusAndCountryContainingIgnoreCase("PENDING", q.trim(), pageableP);

        Page<CountryEmission> approved = (q == null || q.isBlank())
                ? repo.findByStatusOrderByCountryAscYearDesc("APPROVED", pageableA)
                : repo.findByStatusAndCountryContainingIgnoreCase("APPROVED", q.trim(), pageableA);

        model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("pPending", pending);
        model.addAttribute("pApproved", approved);
        model.addAttribute("listPending", pending.getContent());
        model.addAttribute("listApproved", approved.getContent());
        model.addAttribute("size", size);
        model.addAttribute("pageP", pagePending);
        model.addAttribute("pageA", pageApproved);
        return "admin/moderation";
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Long id,
                          @RequestParam(defaultValue = "0", name="pageP") int pagePending,
                          @RequestParam(defaultValue = "0", name="pageA") int pageApproved,
                          @RequestParam(defaultValue = "20") int size,
                          @RequestParam(name="q", required = false) String q) {
        var e = repo.findById(id).orElseThrow();
        e.setStatus("APPROVED");
        repo.save(e);
        return "redirect:/admin/moderation?pageP="+pagePending+"&pageA="+pageApproved+"&size="+size+(q!=null? "&q="+q:"");
    }

    @PostMapping("/{id}/reject")
    public String reject(@PathVariable Long id,
                         @RequestParam(defaultValue = "0", name="pageP") int pagePending,
                         @RequestParam(defaultValue = "0", name="pageA") int pageApproved,
                         @RequestParam(defaultValue = "20") int size,
                         @RequestParam(name="q", required = false) String q) {
        var e = repo.findById(id).orElseThrow();
        e.setStatus("REJECTED");
        repo.save(e);
        return "redirect:/admin/moderation?pageP="+pagePending+"&pageA="+pageApproved+"&size="+size+(q!=null? "&q="+q:"");
    }
}
