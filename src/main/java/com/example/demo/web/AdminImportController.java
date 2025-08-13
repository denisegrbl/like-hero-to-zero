package com.example.demo.web;

import com.example.demo.service.OwidImportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AdminImportController {

    private final OwidImportService importService;

    public AdminImportController(OwidImportService importService) {
        this.importService = importService;
    }

    @GetMapping("/admin")
    public String adminPage() { return "admin"; }

    @PostMapping("/admin/import-owid")
    public String importOwid(Model model) {
        try {
            int count = importService.importLocalCsv();
            model.addAttribute("message", "Import abgeschlossen: " + count + " Zeilen verarbeitet.");
        } catch (Exception e) {
            model.addAttribute("message", "Import-Fehler: " + e.getMessage());
        }
        return "admin";
    }
}
