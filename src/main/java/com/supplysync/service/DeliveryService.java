package com.supplysync.service;

import com.supplysync.dto.DeliveryDto;
import com.supplysync.entity.Delivery;
import com.supplysync.entity.PurchaseOrder;
import com.supplysync.enums.DeliveryStatus;
import com.supplysync.enums.OrderStatus;
import com.supplysync.exception.BadRequestException;
import com.supplysync.exception.ResourceNotFoundException;
import com.supplysync.repository.DeliveryRepository;
import com.supplysync.repository.PurchaseOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderService purchaseOrderService;

    public DeliveryService(DeliveryRepository deliveryRepository,
                           PurchaseOrderRepository purchaseOrderRepository,
                           PurchaseOrderService purchaseOrderService) {
        this.deliveryRepository = deliveryRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseOrderService = purchaseOrderService;
    }

    @Transactional(readOnly = true)
    public List<DeliveryDto> getAllDeliveries(String search, DeliveryStatus status, Long vendorId) {
        List<Delivery> list;
        if ((search != null && !search.isBlank()) || status != null || vendorId != null) {
            list = deliveryRepository.searchDeliveries(search, status, vendorId);
        } else {
            list = deliveryRepository.findAll();
        }
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DeliveryDto getDeliveryById(Long id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + id));
        return toDto(delivery);
    }

    @Transactional(readOnly = true)
    public DeliveryDto getDeliveryByOrderId(Long orderId) {
        Delivery delivery = deliveryRepository.findByPurchaseOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No delivery tracking found for order id: " + orderId));
        return toDto(delivery);
    }

    @Transactional
    public DeliveryDto createDelivery(DeliveryDto dto) {
        PurchaseOrder po = purchaseOrderRepository.findById(dto.getPurchaseOrderId())
                .orElseThrow(() -> new BadRequestException("Purchase Order not found with id: " + dto.getPurchaseOrderId()));

        if (deliveryRepository.findByPurchaseOrderId(po.getId()).isPresent()) {
            throw new BadRequestException("A delivery record already exists for Purchase Order " + po.getOrderNumber());
        }

        String delNum = "DEL-" + System.currentTimeMillis() % 1000000;

        Delivery delivery = new Delivery(
                delNum,
                po,
                dto.getTrackingNumber(),
                dto.getCarrierName(),
                dto.getExpectedDeliveryDate() != null ? dto.getExpectedDeliveryDate() : po.getExpectedDeliveryDate(),
                dto.getDeliveryNotes()
        );
        if (dto.getShipmentStatus() != null) {
            delivery.setShipmentStatus(dto.getShipmentStatus());
        }
        delivery.setRecipientName(dto.getRecipientName());

        Delivery saved = deliveryRepository.save(delivery);

        // Synchronize PO status with delivery status if applicable
        updatePoStatusFromDelivery(po, saved.getShipmentStatus());

        return toDto(saved);
    }

    @Transactional
    public DeliveryDto updateDelivery(Long id, DeliveryDto dto) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + id));

        delivery.setTrackingNumber(dto.getTrackingNumber());
        delivery.setCarrierName(dto.getCarrierName());
        if (dto.getExpectedDeliveryDate() != null) {
            delivery.setExpectedDeliveryDate(dto.getExpectedDeliveryDate());
        }
        delivery.setDeliveryNotes(dto.getDeliveryNotes());
        delivery.setRecipientName(dto.getRecipientName());

        if (dto.getShipmentStatus() != null) {
            delivery.setShipmentStatus(dto.getShipmentStatus());
            if (dto.getShipmentStatus() == DeliveryStatus.DELIVERED && delivery.getActualDeliveryDate() == null) {
                delivery.setActualDeliveryDate(LocalDateTime.now());
            }
            updatePoStatusFromDelivery(delivery.getPurchaseOrder(), dto.getShipmentStatus());
        }

        Delivery saved = deliveryRepository.save(delivery);
        return toDto(saved);
    }

    @Transactional
    public DeliveryDto updateShipmentStatus(Long id, DeliveryStatus newStatus) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + id));

        delivery.setShipmentStatus(newStatus);
        if (newStatus == DeliveryStatus.DELIVERED) {
            delivery.setActualDeliveryDate(LocalDateTime.now());
        }

        updatePoStatusFromDelivery(delivery.getPurchaseOrder(), newStatus);

        Delivery saved = deliveryRepository.save(delivery);
        return toDto(saved);
    }

    private void updatePoStatusFromDelivery(PurchaseOrder po, DeliveryStatus deliveryStatus) {
        if (deliveryStatus == DeliveryStatus.DELIVERED) {
            purchaseOrderService.updateOrderStatus(po.getId(), OrderStatus.DELIVERED);
        } else if (deliveryStatus == DeliveryStatus.DISPATCHED || deliveryStatus == DeliveryStatus.IN_TRANSIT || deliveryStatus == DeliveryStatus.OUT_FOR_DELIVERY) {
            if (po.getStatus() != OrderStatus.SHIPPED && po.getStatus() != OrderStatus.DELIVERED) {
                po.setStatus(OrderStatus.SHIPPED);
                purchaseOrderRepository.save(po);
            }
        }
    }

    @Transactional
    public void deleteDelivery(Long id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + id));
        deliveryRepository.delete(delivery);
    }

    public DeliveryDto toDto(Delivery d) {
        DeliveryDto dto = new DeliveryDto();
        dto.setId(d.getId());
        dto.setDeliveryNumber(d.getDeliveryNumber());
        if (d.getPurchaseOrder() != null) {
            dto.setPurchaseOrderId(d.getPurchaseOrder().getId());
            dto.setPurchaseOrderNumber(d.getPurchaseOrder().getOrderNumber());
            if (d.getPurchaseOrder().getVendor() != null) {
                dto.setVendorId(d.getPurchaseOrder().getVendor().getId());
                dto.setVendorCompanyName(d.getPurchaseOrder().getVendor().getCompanyName());
            }
        }
        dto.setTrackingNumber(d.getTrackingNumber());
        dto.setCarrierName(d.getCarrierName());
        dto.setShipmentStatus(d.getShipmentStatus());
        dto.setExpectedDeliveryDate(d.getExpectedDeliveryDate());
        dto.setActualDeliveryDate(d.getActualDeliveryDate());
        dto.setDeliveryNotes(d.getDeliveryNotes());
        dto.setRecipientName(d.getRecipientName());
        dto.setCreatedAt(d.getCreatedAt());
        dto.setUpdatedAt(d.getUpdatedAt());
        return dto;
    }
}
