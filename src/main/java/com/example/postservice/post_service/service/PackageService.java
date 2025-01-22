package com.example.postservice.post_service.service;

import com.example.postservice.post_service.entity.Package;
import com.example.postservice.post_service.repository.PackageRepository;
import com.example.postservice.post_service.util.BarcodeUtil;
import com.example.postservice.post_service.util.FileParserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PackageService {

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private AuditService auditService;

    public Package addPackage(Package pkg) {
        // Validate priority
        if (pkg.getPriority() == null || pkg.getPriority().isEmpty()) {
            pkg.setPriority("Standard"); // Default priority
        }

        // Set created date if not already set
        if (pkg.getCreatedDate() == null) {
            pkg.setCreatedDate(new Date()); // Use current date and time
        }

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

    public List<Package> searchByTrackingNumber(String trackingNumber) {
        return packageRepository.findByTrackingNumberContaining(trackingNumber);
    }

    public Package getPackageByTrackingNumber(String trackingNumber) {
        return packageRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new RuntimeException("Package not found"));
    }

    public List<Package> advancedSearch(String sender, String recipient, String status, LocalDateTime startDate, LocalDateTime endDate) {
        return packageRepository.advancedSearch(sender, recipient, status, startDate, endDate);
    }

    public List<Package> getPackagesByPriority(String priority) {
        return packageRepository.findByPriority(priority);
    }

    public Package updatePackage(Long id, Package updatedPackage) {
        Package existingPackage = packageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Package not found"));

        if (updatedPackage.getSender() != null) {
            existingPackage.setSender(updatedPackage.getSender());
        }
        if (updatedPackage.getRecipient() != null) {
            existingPackage.setRecipient(updatedPackage.getRecipient());
        }
        if (updatedPackage.getSenderAddress() != null) {
            existingPackage.setSenderAddress(updatedPackage.getSenderAddress());
        }
        if (updatedPackage.getRecipientAddress() != null) {
            existingPackage.setRecipientAddress(updatedPackage.getRecipientAddress());
        }
        if (updatedPackage.getStatus() != null) {
            existingPackage.setStatus(updatedPackage.getStatus());
            if ("Delivered".equalsIgnoreCase(updatedPackage.getStatus())) {
                existingPackage.setDeliveredDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            }
        }

        // Do not update priority if it is not provided in the request
        if (updatedPackage.getPriority() != null) {
            existingPackage.setPriority(updatedPackage.getPriority());
        }

        // Save the updated package
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

    public List<Package> bulkCreatePackages(MultipartFile file) {
        try {
            List<Package> packages = FileParserUtil.parseExcelFile(file.getInputStream());

            // Generate barcodes and set default priority for each package
            for (Package pkg : packages) {
                if (pkg.getTrackingNumber() != null && !pkg.getTrackingNumber().isEmpty()) {
                    try {
                        byte[] barcodeImage = BarcodeUtil.generateBarcodeImage(pkg.getTrackingNumber(), 300, 100);
                        pkg.setBarcode(barcodeImage);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to generate barcode for tracking number: " + pkg.getTrackingNumber());
                    }
                } else {
                    throw new RuntimeException("Tracking number cannot be null or empty for package: " + pkg);
                }

                // Set a default priority if not provided
                if (pkg.getPriority() == null || pkg.getPriority().isEmpty()) {
                    pkg.setPriority("Standard");
                }
            }

            // Save all packages to the database
            List<Package> savedPackages = packageRepository.saveAll(packages);

            // Log actions for audit
            String currentUser = getCurrentUser();
            String trackingNumbers = savedPackages.stream()
                    .map(Package::getTrackingNumber)
                    .collect(Collectors.joining(", "));
            auditService.logAction("BULK_CREATE", currentUser, "Bulk created packages: " + trackingNumbers);

            return savedPackages;
        } catch (Exception e) {
            throw new RuntimeException("Failed to process bulk create: " + e.getMessage());
        }
    }

    // Utility method to get the username of the logged-in user
    private String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
