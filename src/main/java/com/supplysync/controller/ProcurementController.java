package com.supplysync.controller;

import com.supplysync.dto.ApiResponse;
import com.supplysync.dto.ProcurementRequestDto;
import com.supplysync.dto.PurchaseOrderDto;
import com.supplysync.enums.RequestStatus;
import com.supplysync.service.ProcurementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/procurement-requests")
public class ProcurementController {

    private final ProcurementService procurementService;

    public ProcurementController(ProcurementService procurementService) {
        this.procurementService = procurementService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProcurementRequestDto>>> getAllRequests(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) RequestStatus status,
            @RequestParam(required = false) Long vendorId) {
        List<ProcurementRequestDto> list = procurementService.getAllRequests(search, status, vendorId);
        return ResponseEntity.ok(ApiResponse.success("Procurement requests retrieved", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProcurementRequestDto>> getRequestById(@PathVariable Long id) {
        ProcurementRequestDto request = procurementService.getRequestById(id);
        return ResponseEntity.ok(ApiResponse.success("Procurement request details retrieved", request));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProcurementRequestDto>> createRequest(
            @Valid @RequestBody ProcurementRequestDto dto,
            Authentication authentication) {
        com.supplysync.security.CustomUserDetails userDetails = (com.supplysync.security.CustomUserDetails) authentication.getPrincipal();
        com.supplysync.entity.User currentUser = userDetails.getUser();
        if (currentUser.getRole() == com.supplysync.enums.Role.VENDOR && currentUser.getVendor() != null) {
            dto.setVendorId(currentUser.getVendor().getId());
        }
        ProcurementRequestDto created = procurementService.createRequest(dto, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Procurement request created successfully", created));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProcurementRequestDto>> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        RequestStatus status = RequestStatus.valueOf(body.get("status"));
        ProcurementRequestDto updated = procurementService.updateStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Procurement request status updated to " + status, updated));
    }

    @PostMapping("/{id}/convert-to-po")
    public ResponseEntity<ApiResponse<PurchaseOrderDto>> convertToPurchaseOrder(
            @PathVariable Long id,
            Authentication authentication) {
        com.supplysync.security.CustomUserDetails userDetails = (com.supplysync.security.CustomUserDetails) authentication.getPrincipal();
        com.supplysync.entity.User currentUser = userDetails.getUser();
        if (currentUser.getRole() == com.supplysync.enums.Role.VENDOR && currentUser.getVendor() != null) {
            ProcurementRequestDto pr = procurementService.getRequestById(id);
            if (!pr.getVendorId().equals(currentUser.getVendor().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("You are not authorized to convert requests for other vendors."));
            }
        }
        PurchaseOrderDto po = procurementService.convertToPurchaseOrder(id);
        return ResponseEntity.ok(ApiResponse.success("Procurement request converted to Purchase Order " + po.getOrderNumber(), po));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteRequest(@PathVariable Long id) {
        procurementService.deleteRequest(id);
        return ResponseEntity.ok(ApiResponse.success("Procurement request deleted successfully"));
    }
}
