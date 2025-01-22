package com.example.postservice.post_service.controller;

import com.example.postservice.post_service.dto.DeliveryInsights;
import com.example.postservice.post_service.dto.PersonalInsights;
import com.example.postservice.post_service.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/insights/overall")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<DeliveryInsights> getOverallInsights() {
        return ResponseEntity.ok(analyticsService.getOverallInsights());
    }

    @GetMapping("/insights/personal")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PersonalInsights> getPersonalInsights() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(analyticsService.getPersonalInsights(currentUsername));
    }
}