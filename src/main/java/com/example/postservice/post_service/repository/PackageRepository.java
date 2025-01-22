package com.example.postservice.post_service.repository;

import com.example.postservice.post_service.entity.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PackageRepository extends JpaRepository<Package, Long> {

    // Find by exact tracking number (unique search)
    Optional<Package> findByTrackingNumber(String trackingNumber);

    // Find by tracking number containing (partial match, case-insensitive)
    List<Package> findByTrackingNumberContaining(String trackingNumber);

    List<Package> findByPriority(String priority);

    // Advanced search using filters
    @Query("SELECT p FROM Package p WHERE " +
            "(:sender IS NULL OR p.sender LIKE %:sender%) AND " +
            "(:recipient IS NULL OR p.recipient LIKE %:recipient%) AND " +
            "(:status IS NULL OR p.status = :status) AND " +
            "(:startDate IS NULL OR p.createdDate >= :startDate) AND " +
            "(:endDate IS NULL OR p.createdDate <= :endDate)")
    List<Package> advancedSearch(
            @Param("sender") String sender,
            @Param("recipient") String recipient,
            @Param("status") String status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT p FROM Package p WHERE p.sender = :username OR p.recipient = :username")
    List<Package> findBySenderOrRecipient(@Param("username") String username);

}