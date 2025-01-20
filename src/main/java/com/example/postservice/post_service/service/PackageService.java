package com.example.postservice.post_service.service;

import com.example.postservice.post_service.entity.Package;
import com.example.postservice.post_service.repository.PackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PackageService {

    @Autowired
    private PackageRepository packageRepository;

    public Package addPackage(Package pkg) {
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