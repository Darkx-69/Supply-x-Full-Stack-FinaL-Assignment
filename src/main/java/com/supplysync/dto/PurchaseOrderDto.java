package com.supplysync.dto;

import com.supplysync.enums.OrderStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderDto {
    private Long id;
    private String orderNumber;

    @NotNull(message = "Vendor ID is required")
    private Long vendorId;
    private String vendorCompanyName;
    private String vendorEmail;
    private String vendorPhone;
    private String vendorAddress;

    private Long procurementRequestId;
    private String procurementRequestNumber;

    private LocalDate orderDate;
    private LocalDate expectedDeliveryDate;
    private OrderStatus status = OrderStatus.PENDING;

    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal taxRate = new BigDecimal("5.00");
    private BigDecimal taxAmount = BigDecimal.ZERO;
    private BigDecimal totalAmount = BigDecimal.ZERO;

    private String notes;
    private Boolean inventoryReceived = false;

    @NotEmpty(message = "Order must have at least one item")
    private List<PurchaseOrderItemDto> items = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PurchaseOrderDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }

    public String getVendorCompanyName() { return vendorCompanyName; }
    public void setVendorCompanyName(String vendorCompanyName) { this.vendorCompanyName = vendorCompanyName; }

    public String getVendorEmail() { return vendorEmail; }
    public void setVendorEmail(String vendorEmail) { this.vendorEmail = vendorEmail; }

    public String getVendorPhone() { return vendorPhone; }
    public void setVendorPhone(String vendorPhone) { this.vendorPhone = vendorPhone; }

    public String getVendorAddress() { return vendorAddress; }
    public void setVendorAddress(String vendorAddress) { this.vendorAddress = vendorAddress; }

    public Long getProcurementRequestId() { return procurementRequestId; }
    public void setProcurementRequestId(Long procurementRequestId) { this.procurementRequestId = procurementRequestId; }

    public String getProcurementRequestNumber() { return procurementRequestNumber; }
    public void setProcurementRequestNumber(String procurementRequestNumber) { this.procurementRequestNumber = procurementRequestNumber; }

    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }

    public LocalDate getExpectedDeliveryDate() { return expectedDeliveryDate; }
    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) { this.expectedDeliveryDate = expectedDeliveryDate; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getTaxRate() { return taxRate; }
    public void setTaxRate(BigDecimal taxRate) { this.taxRate = taxRate; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Boolean getInventoryReceived() { return inventoryReceived; }
    public void setInventoryReceived(Boolean inventoryReceived) { this.inventoryReceived = inventoryReceived; }

    public List<PurchaseOrderItemDto> getItems() { return items; }
    public void setItems(List<PurchaseOrderItemDto> items) { this.items = items; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
