package com.supplysync.controller;

import com.supplysync.dto.ApiResponse;
import com.supplysync.dto.PurchaseOrderDto;
import com.supplysync.entity.User;
import com.supplysync.enums.OrderStatus;
import com.supplysync.enums.Role;
import com.supplysync.repository.UserRepository;
import com.supplysync.security.CustomUserDetails;
import com.supplysync.service.PurchaseOrderService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;
    private final UserRepository userRepository;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService, UserRepository userRepository) {
        this.purchaseOrderService = purchaseOrderService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PurchaseOrderDto>>> getAllOrders(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) Long vendorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication) {

        // If vendor user, enforce filtering by their own vendorId
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User currentUser = userDetails.getUser();
        if (currentUser.getRole() == Role.VENDOR && currentUser.getVendor() != null) {
            vendorId = currentUser.getVendor().getId();
        }

        List<PurchaseOrderDto> list = purchaseOrderService.getAllPurchaseOrders(search, status, vendorId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Purchase orders retrieved successfully", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PurchaseOrderDto>> getOrderById(@PathVariable Long id, Authentication authentication) {
        PurchaseOrderDto order = purchaseOrderService.getPurchaseOrderById(id);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User currentUser = userDetails.getUser();
        if (currentUser.getRole() == Role.VENDOR && currentUser.getVendor() != null) {
            if (!order.getVendorId().equals(currentUser.getVendor().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("You are not authorized to view orders from other vendors."));
            }
        }

        return ResponseEntity.ok(ApiResponse.success("Purchase order details retrieved", order));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PurchaseOrderDto>> createOrder(
            @Valid @RequestBody PurchaseOrderDto dto,
            Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User currentUser = userDetails.getUser();
        if (currentUser.getRole() == Role.VENDOR && currentUser.getVendor() != null) {
            dto.setVendorId(currentUser.getVendor().getId());
        }
        PurchaseOrderDto created = purchaseOrderService.createPurchaseOrder(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Purchase order created successfully", created));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<PurchaseOrderDto>> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            Authentication authentication) {
        OrderStatus newStatus = OrderStatus.valueOf(body.get("status"));

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User currentUser = userDetails.getUser();
        if (currentUser.getRole() == Role.VENDOR) {
            // Vendors can only update orders assigned to them to PROCESSING or SHIPPED
            PurchaseOrderDto order = purchaseOrderService.getPurchaseOrderById(id);
            if (currentUser.getVendor() == null || !order.getVendorId().equals(currentUser.getVendor().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("Access denied."));
            }
        }

        PurchaseOrderDto updated = purchaseOrderService.updateOrderStatus(id, newStatus);
        return ResponseEntity.ok(ApiResponse.success("Purchase order status updated to " + newStatus, updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long id) {
        purchaseOrderService.deletePurchaseOrder(id);
        return ResponseEntity.ok(ApiResponse.success("Purchase order deleted successfully"));
    }
}
