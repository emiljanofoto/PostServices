package com.example.postservice.post_service.controller;

import com.example.postservice.post_service.entity.Package;
import com.example.postservice.post_service.service.PackageService;
import com.example.postservice.post_service.util.BarcodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/packages")
public class PackageController {

    @Autowired
    private PackageService packageService;

    @GetMapping("/priority")
    public ResponseEntity<List<Package>> getPackagesByPriority(@RequestParam String priority) {
        return ResponseEntity.ok(packageService.getPackagesByPriority(priority));
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Map<String, Object>> addPackage(@RequestBody Package pkg) {
        Package savedPackage = packageService.addPackage(pkg);

        // Generate the barcode as a byte array
        byte[] barcodeImage;
        try {
            barcodeImage = BarcodeUtil.generateBarcodeImage(pkg.getTrackingNumber(), 300, 100);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to generate barcode"));
        }

        return ResponseEntity.ok(Map.of(
                "package", savedPackage,
                "barcode", barcodeImage
        ));
    }

    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<Package>> getAllPackages() {
        return ResponseEntity.ok(packageService.getAllPackages());
    }

    @GetMapping("/{trackingNumber}")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('MANAGER')")
    public ResponseEntity<Package> getPackage(@PathVariable String trackingNumber) {
        return ResponseEntity.ok(packageService.getPackageByTrackingNumber(trackingNumber));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Package> updatePackage(@PathVariable Long id, @RequestBody Package updatedPackage) {
        return ResponseEntity.ok(packageService.updatePackage(id, updatedPackage));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> deletePackage(@PathVariable Long id) {
        packageService.deletePackage(id);
        return ResponseEntity.noContent().build();
    }
}