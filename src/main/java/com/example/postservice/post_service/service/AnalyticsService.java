package com.example.postservice.post_service.service;

import com.example.postservice.post_service.entity.Package;
import com.example.postservice.post_service.repository.PackageRepository;
import com.example.postservice.post_service.dto.DeliveryInsights;
import com.example.postservice.post_service.dto.PersonalInsights;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired
    private PackageRepository packageRepository;

    public DeliveryInsights getOverallInsights() {
        return calculateInsights(packageRepository.findAll());
    }

    public PersonalInsights getPersonalInsights(String username) {
        // Fetch packages sent or received by the user
        List<Package> userPackages = packageRepository.findBySenderOrRecipient(username);
        DeliveryInsights deliveryInsights = calculateInsights(userPackages);

        // Convert overall insights to personal insights
        PersonalInsights insights = new PersonalInsights();
        insights.setTotalUserOrders(deliveryInsights.getTotalOrders());
        insights.setAverageDeliveryTime(deliveryInsights.getAverageDeliveryTime());
        insights.setUserRoutes(deliveryInsights.getMostCommonRoutes());

        return insights;
    }

    private DeliveryInsights calculateInsights(List<Package> packages) {
        DeliveryInsights insights = new DeliveryInsights();

        // Total orders
        insights.setTotalOrders(packages.size());

        // Average delivery time
        double totalDeliveryTime = packages.stream()
                .filter(pkg -> "Delivered".equalsIgnoreCase(pkg.getStatus()) && pkg.getCreatedDate() != null && pkg.getDeliveredDate() != null)
                .mapToDouble(pkg -> {
                    LocalDateTime createdDateTime = pkg.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    LocalDateTime deliveredDateTime = pkg.getDeliveredDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    long diff = java.time.Duration.between(createdDateTime, deliveredDateTime).toMillis();
                    return diff / (1000 * 60 * 60 * 24.0); // Convert milliseconds to days
                })
                .sum();

        long deliveredCount = packages.stream()
                .filter(pkg -> "Delivered".equalsIgnoreCase(pkg.getStatus()) && pkg.getCreatedDate() != null && pkg.getDeliveredDate() != null)
                .count();

        insights.setAverageDeliveryTime(deliveredCount > 0 ? totalDeliveryTime / deliveredCount : 0);

        // Packages per day
        Map<LocalDate, Long> dailyCount = packages.stream()
                .filter(pkg -> pkg.getCreatedDate() != null)
                .collect(Collectors.groupingBy(pkg ->
                                pkg.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                        Collectors.counting()));
        insights.setPackagesPerDay(dailyCount);

        // Most common routes by address
        Map<String, Long> routeCounts = packages.stream()
                .collect(Collectors.groupingBy(pkg -> pkg.getSenderAddress() + " -> " + pkg.getRecipientAddress(), Collectors.counting()));
        insights.setMostCommonRoutes(routeCounts);

        // Top senders and recipients
        Map<String, Long> senderCounts = packages.stream()
                .collect(Collectors.groupingBy(Package::getSenderAddress, Collectors.counting()));

        Map<String, Long> recipientCounts = packages.stream()
                .collect(Collectors.groupingBy(Package::getRecipientAddress, Collectors.counting()));

        insights.setTopSenders(senderCounts);
        insights.setTopRecipients(recipientCounts);

        return insights;
    }
}