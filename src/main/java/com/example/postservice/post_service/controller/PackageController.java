package com.example.postservice.post_service.controller;

import com.example.postservice.post_service.entity.Package;
import com.example.postservice.post_service.repository.PackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/packages")
public class PackageController {

    @Autowired
    private PackageRepository packageRepository;

    @PostMapping
    public Package addPackage(@RequestBody Package pkg) {
        return packageRepository.save(pkg);
    }

    @GetMapping
    public List<Package> getAllPackages() {
        return packageRepository.findAll();
    }

    @GetMapping("/{trackingNumber}")
    public Package getPackage(@PathVariable String trackingNumber) {
        return packageRepository.findByTrackingNumber(trackingNumber);
    }
}