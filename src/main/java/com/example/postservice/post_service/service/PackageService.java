package com.example.postservice.post_service.service;

import com.example.postservice.post_service.entity.Package;
import com.example.postservice.post_service.repository.PackageRepository;
import com.example.postservice.post_service.util.BarcodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PackageService {

    @Autowired
    private PackageRepository packageRepository;

    public Package addPackage(Package pkg) {
        // Generate a barcode for the tracking number
        try {
            String barcodePath = "barcodes/" + pkg.getTrackingNumber() + ".png";
            byte[] barcodeImage = BarcodeUtil.generateBarcodeImage(pkg.getTrackingNumber(), 300, 100);
            pkg.setBarcode(barcodeImage);
            System.out.println("Barcode saved at: " + barcodePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate barcode: " + e.getMessage());
        }

        // Save the package
        return packageRepository.save(pkg);
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
        return packageRepository.save(existingPackage);
    }

    public void deletePackage(Long id) {
        packageRepository.deleteById(id);
    }
}