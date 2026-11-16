package com.supplysync.controller;

import com.supplysync.dto.ApiResponse;
import com.supplysync.dto.InventoryDto;
import com.supplysync.dto.StockAdjustmentRequest;
import com.supplysync.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InventoryDto>>> getAllInventory(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean lowStockOnly) {
        List<InventoryDto> list = inventoryService.getAllInventory(search, categoryId, lowStockOnly);
        return ResponseEntity.ok(ApiResponse.success("Inventory items retrieved", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InventoryDto>> getInventoryById(@PathVariable Long id) {
        InventoryDto inventory = inventoryService.getInventoryById(id);
        return ResponseEntity.ok(ApiResponse.success("Inventory item retrieved", inventory));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<InventoryDto>> getInventoryByProductId(@PathVariable Long productId) {
        InventoryDto inventory = inventoryService.getInventoryByProductId(productId);
        return ResponseEntity.ok(ApiResponse.success("Product inventory retrieved", inventory));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<InventoryDto>>> getLowStockItems() {
        List<InventoryDto> items = inventoryService.getLowStockItems();
        return ResponseEntity.ok(ApiResponse.success("Low stock items retrieved", items));
    }

    @PostMapping("/adjust")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<InventoryDto>> adjustStock(
            @Valid @RequestBody StockAdjustmentRequest request,
            Authentication authentication) {
        InventoryDto updated = inventoryService.adjustStock(request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Stock adjusted successfully", updated));
    }

    @PatchMapping("/{id}/thresholds")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<InventoryDto>> updateThresholds(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> body) {
        Integer minLevel = body.get("minimumStockLevel");
        Integer maxLevel = body.get("maximumStockLevel");
        InventoryDto updated = inventoryService.updateThresholds(id, minLevel, maxLevel);
        return ResponseEntity.ok(ApiResponse.success("Thresholds updated successfully", updated));
    }
}
