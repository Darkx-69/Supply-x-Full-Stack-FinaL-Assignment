package com.supplysync.controller;

import com.supplysync.dto.ApiResponse;
import com.supplysync.dto.DashboardStatsDto;
import com.supplysync.entity.User;
import com.supplysync.enums.Role;
import com.supplysync.security.CustomUserDetails;
import com.supplysync.service.DashboardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getAdminDashboard() {
        DashboardStatsDto stats = dashboardService.getAdminDashboardStats();
        return ResponseEntity.ok(ApiResponse.success("Admin dashboard statistics retrieved", stats));
    }

    @GetMapping("/vendor")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getVendorDashboard(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User currentUser = userDetails.getUser();

        if (currentUser.getRole() != Role.VENDOR || currentUser.getVendor() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Only vendors linked to a vendor profile can access the vendor dashboard"));
        }

        Map<String, Object> stats = dashboardService.getVendorDashboardStats(currentUser.getVendor().getId());
        return ResponseEntity.ok(ApiResponse.success("Vendor dashboard statistics retrieved", stats));
    }
}
