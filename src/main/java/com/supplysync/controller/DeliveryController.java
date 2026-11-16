package com.supplysync.controller;

import com.supplysync.dto.ApiResponse;
import com.supplysync.dto.DeliveryDto;
import com.supplysync.entity.User;
import com.supplysync.enums.DeliveryStatus;
import com.supplysync.enums.Role;
import com.supplysync.security.CustomUserDetails;
import com.supplysync.service.DeliveryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DeliveryDto>>> getAllDeliveries(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) DeliveryStatus status,
            @RequestParam(required = false) Long vendorId,
            Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User currentUser = userDetails.getUser();
        if (currentUser.getRole() == Role.VENDOR && currentUser.getVendor() != null) {
            vendorId = currentUser.getVendor().getId();
        }

        List<DeliveryDto> list = deliveryService.getAllDeliveries(search, status, vendorId);
        return ResponseEntity.ok(ApiResponse.success("Deliveries retrieved successfully", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DeliveryDto>> getDeliveryById(@PathVariable Long id, Authentication authentication) {
        DeliveryDto delivery = deliveryService.getDeliveryById(id);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User currentUser = userDetails.getUser();
        if (currentUser.getRole() == Role.VENDOR && currentUser.getVendor() != null) {
            if (!delivery.getVendorId().equals(currentUser.getVendor().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("Access denied."));
            }
        }

        return ResponseEntity.ok(ApiResponse.success("Delivery details retrieved", delivery));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<DeliveryDto>> getDeliveryByOrderId(@PathVariable Long orderId) {
        DeliveryDto delivery = deliveryService.getDeliveryByOrderId(orderId);
        return ResponseEntity.ok(ApiResponse.success("Order delivery tracking retrieved", delivery));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DeliveryDto>> createDelivery(@Valid @RequestBody DeliveryDto dto) {
        DeliveryDto created = deliveryService.createDelivery(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Delivery tracking initiated successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DeliveryDto>> updateDelivery(
            @PathVariable Long id,
            @Valid @RequestBody DeliveryDto dto,
            Authentication authentication) {
        DeliveryDto updated = deliveryService.updateDelivery(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Delivery updated successfully", updated));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<DeliveryDto>> updateShipmentStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        DeliveryStatus status = DeliveryStatus.valueOf(body.get("status"));
        DeliveryDto updated = deliveryService.updateShipmentStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Shipment status updated to " + status, updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteDelivery(@PathVariable Long id) {
        deliveryService.deleteDelivery(id);
        return ResponseEntity.ok(ApiResponse.success("Delivery record deleted successfully"));
    }
}
