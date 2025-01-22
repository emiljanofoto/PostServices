package com.example.postservice.post_service.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
public class DeliveryInsights {
    private int totalOrders;
    private double averageDeliveryTime;
    private Map<LocalDate, Long> packagesPerDay;
    private Map<String, Long> mostCommonRoutes; // senderAddress -> recipientAddress
    private Map<String, Long> topSenders;       // senderAddress -> count
    private Map<String, Long> topRecipients;    // recipientAddress -> count
}