package com.example.demo.web;

import com.example.demo.service.OwidImportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/manage/import")
public class ManageImportController {

    private final OwidImportService importService;

    public ManageImportController(OwidImportService importService) {
        this.importService = importService;
    }

    @GetMapping
    public String form() { return "manage/upload"; }

    @PostMapping
    public String handleUpload(@RequestParam("file") MultipartFile file, Model model) {
        try {
            int count = importService.importFromMultipart(file);
            model.addAttribute("message", "Import erfolgreich: " + count + " Zeilen verarbeitet.");
        } catch (Exception e) {
            model.addAttribute("message", "Fehler beim Import: " + e.getMessage());
        }
        return "manage/upload";
    }
}
