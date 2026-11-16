package com.supplysync.controller;

import com.supplysync.dto.ApiResponse;
import com.supplysync.dto.StockTransactionDto;
import com.supplysync.enums.TransactionType;
import com.supplysync.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final InventoryService inventoryService;

    public TransactionController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StockTransactionDto>>> getTransactions(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) TransactionType type) {
        List<StockTransactionDto> list = inventoryService.getTransactions(search, type);
        return ResponseEntity.ok(ApiResponse.success("Stock transactions retrieved", list));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<StockTransactionDto>>> getProductTransactions(@PathVariable Long productId) {
        List<StockTransactionDto> list = inventoryService.getProductTransactions(productId);
        return ResponseEntity.ok(ApiResponse.success("Product transaction history retrieved", list));
    }
}
