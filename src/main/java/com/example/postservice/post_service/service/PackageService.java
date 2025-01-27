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

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    public Package addPackage(Package pkg) {
        if (pkg.getPriority() == null || pkg.getPriority().isEmpty()) {
            pkg.setPriority("Standard"); // Default priority
        }

        if (pkg.getStatus() == null || pkg.getStatus().isEmpty()) {
            pkg.setStatus("Created");
        }

        if (pkg.getCreatedDate() == null) {
            pkg.setCreatedDate(new Date());
        }

        String currentUser = getCurrentUser();
        Long creatorId = userService.findByUsername(currentUser).getId();
        pkg.setCreatedBy(creatorId); // Set the creator ID

        try {
            byte[] barcodeImage = BarcodeUtil.generateBarcodeImage(pkg.getTrackingNumber(), 300, 100);
            pkg.setBarcode(barcodeImage);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate barcode: " + e.getMessage());
        }

        Package savedPackage = packageRepository.save(pkg);

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
            String status = updatedPackage.getStatus();

            if ("Delivered".equalsIgnoreCase(status)) {
                existingPackage.setDeliveredDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            }

            // Notify the creator of the package
            Long creatorId = existingPackage.getCreatedBy();
            if (creatorId != null) {
                String notificationMessage = "The status of the package with tracking number " +
                        existingPackage.getTrackingNumber() + " has been updated to " + status + ".";

                notificationService.createNotification(notificationMessage, "package-status", creatorId);
                notificationService.sendWebSocketNotification(notificationMessage, "package-status", creatorId);
            }
        }

        Package savedPackage = packageRepository.save(existingPackage);

        String currentUser = getCurrentUser();
        auditService.logAction("UPDATE", currentUser, "Updated package with ID: " + id);

        return savedPackage;
    }

    public void deletePackage(Long id) {
        packageRepository.deleteById(id);

        String currentUser = getCurrentUser();
        auditService.logAction("DELETE", currentUser, "Deleted package with ID: " + id);
    }

    public List<Package> bulkCreatePackages(MultipartFile file) {
        try {
            List<Package> packages = FileParserUtil.parseExcelFile(file.getInputStream());

            String currentUser = getCurrentUser();
            Long creatorId = userService.findByUsername(currentUser).getId();

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

                if (pkg.getPriority() == null || pkg.getPriority().isEmpty()) {
                    pkg.setPriority("Standard");
                }

                pkg.setCreatedBy(creatorId); // Set creator ID for each package
            }

            List<Package> savedPackages = packageRepository.saveAll(packages);

            String trackingNumbers = savedPackages.stream()
                    .map(Package::getTrackingNumber)
                    .collect(Collectors.joining(", "));

            auditService.logAction("BULK_CREATE", currentUser, "Bulk created packages: " + trackingNumbers);

            notificationService.createNotification(
                    "You have successfully created multiple packages. Tracking numbers: " + trackingNumbers,
                    "bulk-action",
                    creatorId
            );

            return savedPackages;
        } catch (Exception e) {
            throw new RuntimeException("Failed to process bulk create: " + e.getMessage());
        }
    }

    private String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}