package com.example.postservice.post_service.repository;

import com.example.postservice.post_service.entity.Package;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackageRepository extends JpaRepository<Package, Long> {
    Package findByTrackingNumber(String trackingNumber);
}