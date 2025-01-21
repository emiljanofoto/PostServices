package com.example.postservice.post_service.controller;

import com.example.postservice.post_service.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExportController {

    @Autowired
    private ExportService exportService;

    @GetMapping("/api/packages/export")
    public ResponseEntity<byte[]> exportPackages(@RequestParam String format) {
        byte[] data = exportService.exportPackages(format);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=packages." + format);

        String contentType = switch (format) {
            case "csv" -> "text/csv";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "pdf" -> "application/pdf";
            default -> throw new IllegalArgumentException("Unsupported format: " + format);
        };

        headers.add("Content-Type", contentType);
        return ResponseEntity.ok().headers(headers).body(data);
    }
}