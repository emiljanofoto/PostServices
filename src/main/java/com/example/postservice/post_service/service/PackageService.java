package com.example.postservice.post_service.service;

import com.example.postservice.post_service.entity.Package;
import com.example.postservice.post_service.repository.PackageRepository;
import com.example.postservice.post_service.util.BarcodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PackageService {

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private AuditService auditService;

    public Package addPackage(Package pkg) {
        // Generate a barcode for the tracking number
        try {
            byte[] barcodeImage = BarcodeUtil.generateBarcodeImage(pkg.getTrackingNumber(), 300, 100);
            pkg.setBarcode(barcodeImage);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate barcode: " + e.getMessage());
        }

        // Save the package
        Package savedPackage = packageRepository.save(pkg);

        // Log the CREATE action
        String currentUser = getCurrentUser();
        auditService.logAction("CREATE", currentUser, "Created package with trackingNumber: " + pkg.getTrackingNumber());

        return savedPackage;
    }

    public List<Package> getAllPackages() {
        return packageRepository.findAll();
    }

    public Package getPackageByTrackingNumber(String trackingNumber) {
        return packageRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new RuntimeException("Package not found"));
    }

    public Package updatePackage(Long id, Package updatedPackage) {
        Package existingPackage = packageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Package not found"));

        existingPackage.setSender(updatedPackage.getSender());
        existingPackage.setRecipient(updatedPackage.getRecipient());
        existingPackage.setStatus(updatedPackage.getStatus());

        Package savedPackage = packageRepository.save(existingPackage);

        // Log the UPDATE action
        String currentUser = getCurrentUser();
        auditService.logAction("UPDATE", currentUser, "Updated package with ID: " + id);

        return savedPackage;
    }

    public void deletePackage(Long id) {
        packageRepository.deleteById(id);

        // Log the DELETE action
        String currentUser = getCurrentUser();
        auditService.logAction("DELETE", currentUser, "Deleted package with ID: " + id);
    }

    // Utility method to get the username of the logged-in user
    private String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
