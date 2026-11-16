package com.supplysync.dto;

import com.supplysync.enums.TransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class StockAdjustmentRequest {
    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Transaction type is required")
    private TransactionType transactionType; // STOCK_ADDED, STOCK_REMOVED, MANUAL_ADJUSTMENT

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private String referenceOrder;
    private String description;

    public StockAdjustmentRequest() {}

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public TransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getReferenceOrder() { return referenceOrder; }
    public void setReferenceOrder(String referenceOrder) { this.referenceOrder = referenceOrder; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
