package com.example.postservice.post_service.dto;

import lombok.Data;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

@Data
public class PersonalInsights {
    private int totalUserOrders;
    private double averageDeliveryTime;
    private Map<String, Long> userRoutes;
}
