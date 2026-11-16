package com.supplysync.dto;

import com.supplysync.enums.DeliveryStatus;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DeliveryDto {
    private Long id;
    private String deliveryNumber;

    @NotNull(message = "Purchase order ID is required")
    private Long purchaseOrderId;
    private String purchaseOrderNumber;
    private Long vendorId;
    private String vendorCompanyName;

    private String trackingNumber;
    private String carrierName;
    private DeliveryStatus shipmentStatus = DeliveryStatus.PREPARING;
    private LocalDate expectedDeliveryDate;
    private LocalDateTime actualDeliveryDate;
    private String deliveryNotes;
    private String recipientName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DeliveryDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDeliveryNumber() { return deliveryNumber; }
    public void setDeliveryNumber(String deliveryNumber) { this.deliveryNumber = deliveryNumber; }

    public Long getPurchaseOrderId() { return purchaseOrderId; }
    public void setPurchaseOrderId(Long purchaseOrderId) { this.purchaseOrderId = purchaseOrderId; }

    public String getPurchaseOrderNumber() { return purchaseOrderNumber; }
    public void setPurchaseOrderNumber(String purchaseOrderNumber) { this.purchaseOrderNumber = purchaseOrderNumber; }

    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }

    public String getVendorCompanyName() { return vendorCompanyName; }
    public void setVendorCompanyName(String vendorCompanyName) { this.vendorCompanyName = vendorCompanyName; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public String getCarrierName() { return carrierName; }
    public void setCarrierName(String carrierName) { this.carrierName = carrierName; }

    public DeliveryStatus getShipmentStatus() { return shipmentStatus; }
    public void setShipmentStatus(DeliveryStatus shipmentStatus) { this.shipmentStatus = shipmentStatus; }

    public LocalDate getExpectedDeliveryDate() { return expectedDeliveryDate; }
    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) { this.expectedDeliveryDate = expectedDeliveryDate; }

    public LocalDateTime getActualDeliveryDate() { return actualDeliveryDate; }
    public void setActualDeliveryDate(LocalDateTime actualDeliveryDate) { this.actualDeliveryDate = actualDeliveryDate; }

    public String getDeliveryNotes() { return deliveryNotes; }
    public void setDeliveryNotes(String deliveryNotes) { this.deliveryNotes = deliveryNotes; }

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
