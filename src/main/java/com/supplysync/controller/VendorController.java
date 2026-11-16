package com.supplysync.controller;

import com.supplysync.dto.ApiResponse;
import com.supplysync.dto.PurchaseOrderDto;
import com.supplysync.dto.VendorDto;
import com.supplysync.enums.VendorStatus;
import com.supplysync.service.VendorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    private final VendorService vendorService;

    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VendorDto>>> getAllVendors(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) VendorStatus status,
            @RequestParam(required = false) String category) {
        List<VendorDto> vendors = vendorService.getAllVendors(search, status, category);
        return ResponseEntity.ok(ApiResponse.success("Vendors retrieved successfully", vendors));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VendorDto>> getVendorById(@PathVariable Long id) {
        VendorDto vendor = vendorService.getVendorById(id);
        return ResponseEntity.ok(ApiResponse.success("Vendor details retrieved", vendor));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VendorDto>> createVendor(@Valid @RequestBody VendorDto vendorDto) {
        VendorDto created = vendorService.createVendor(vendorDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Vendor created successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VendorDto>> updateVendor(@PathVariable Long id,
                                                               @Valid @RequestBody VendorDto vendorDto) {
        VendorDto updated = vendorService.updateVendor(id, vendorDto);
        return ResponseEntity.ok(ApiResponse.success("Vendor updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteVendor(@PathVariable Long id) {
        vendorService.deleteVendor(id);
        return ResponseEntity.ok(ApiResponse.success("Vendor deleted successfully"));
    }

    @GetMapping("/{id}/orders")
    public ResponseEntity<ApiResponse<List<PurchaseOrderDto>>> getVendorOrders(@PathVariable Long id) {
        List<PurchaseOrderDto> orders = vendorService.getVendorOrders(id);
        return ResponseEntity.ok(ApiResponse.success("Vendor order history retrieved", orders));
    }
}
