package com.example.postservice.post_service.repository;

import com.example.postservice.post_service.entity.Package;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PackageRepository extends JpaRepository<Package, Long> {
    Optional<Package> findByTrackingNumber(String trackingNumber);
}