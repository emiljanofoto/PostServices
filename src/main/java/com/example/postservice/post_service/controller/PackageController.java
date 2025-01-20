package com.example.postservice.post_service.controller;

import com.example.postservice.post_service.entity.Package;
import com.example.postservice.post_service.service.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/packages")
public class PackageController {

    @Autowired
    private PackageService packageService;

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Package> addPackage(@RequestBody Package pkg) {
        return ResponseEntity.ok(packageService.addPackage(pkg));
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