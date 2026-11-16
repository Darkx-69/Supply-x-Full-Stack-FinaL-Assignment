package com.supplysync.service;

import com.supplysync.dto.PurchaseOrderDto;
import com.supplysync.dto.PurchaseOrderItemDto;
import com.supplysync.entity.*;
import com.supplysync.enums.OrderStatus;
import com.supplysync.enums.TransactionType;
import com.supplysync.exception.BadRequestException;
import com.supplysync.exception.ResourceNotFoundException;
import com.supplysync.repository.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository orderItemRepository;
    private final VendorRepository vendorRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final StockTransactionRepository transactionRepository;
    private final ProcurementRequestRepository procurementRequestRepository;

    public PurchaseOrderService(PurchaseOrderRepository purchaseOrderRepository,
                                PurchaseOrderItemRepository orderItemRepository,
                                VendorRepository vendorRepository,
                                ProductRepository productRepository,
                                InventoryRepository inventoryRepository,
                                StockTransactionRepository transactionRepository,
                                @Lazy ProcurementRequestRepository procurementRequestRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.orderItemRepository = orderItemRepository;
        this.vendorRepository = vendorRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.transactionRepository = transactionRepository;
        this.procurementRequestRepository = procurementRequestRepository;
    }

    @Transactional(readOnly = true)
    public List<PurchaseOrderDto> getAllPurchaseOrders(String search, OrderStatus status, Long vendorId,
                                                      LocalDate startDate, LocalDate endDate) {
        List<PurchaseOrder> list;
        if ((search != null && !search.isBlank()) || status != null || vendorId != null || startDate != null || endDate != null) {
            list = purchaseOrderRepository.searchPurchaseOrders(search, status, vendorId, startDate, endDate);
        } else {
            list = purchaseOrderRepository.findAll();
        }
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PurchaseOrderDto getPurchaseOrderById(Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order not found with id: " + id));
        return toDto(po);
    }

    @Transactional
    public PurchaseOrderDto createPurchaseOrder(PurchaseOrderDto dto) {
        Vendor vendor = vendorRepository.findById(dto.getVendorId())
                .orElseThrow(() -> new BadRequestException("Vendor not found with id: " + dto.getVendorId()));

        String poNum = "PO-" + System.currentTimeMillis() % 1000000;

        PurchaseOrder po = new PurchaseOrder(poNum, vendor, dto.getOrderDate(), dto.getExpectedDeliveryDate(), dto.getNotes());
        if (dto.getStatus() != null) {
            po.setStatus(dto.getStatus());
        }
        if (dto.getTaxRate() != null) {
            po.setTaxRate(dto.getTaxRate());
        }

        if (dto.getProcurementRequestId() != null) {
            procurementRequestRepository.findById(dto.getProcurementRequestId())
                    .ifPresent(po::setProcurementRequest);
        }

        for (PurchaseOrderItemDto itemDto : dto.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new BadRequestException("Product not found with id: " + itemDto.getProductId()));
            PurchaseOrderItem item = new PurchaseOrderItem(po, product, itemDto.getQuantity(), itemDto.getUnitPrice());
            po.addItem(item);
        }

        po.recalculateTotals();
        PurchaseOrder saved = purchaseOrderRepository.save(po);

        // If created directly with status DELIVERED, process receipt
        if (saved.getStatus() == OrderStatus.DELIVERED && Boolean.FALSE.equals(saved.getInventoryReceived())) {
            receiveInventory(saved);
        }

        return toDto(saved);
    }

    @Transactional
    public PurchaseOrderDto updateOrderStatus(Long id, OrderStatus newStatus) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order not found with id: " + id));

        po.setStatus(newStatus);

        // If transitioned to DELIVERED, automatically update inventory
        if (newStatus == OrderStatus.DELIVERED && Boolean.FALSE.equals(po.getInventoryReceived())) {
            receiveInventory(po);
        }

        PurchaseOrder saved = purchaseOrderRepository.save(po);
        return toDto(saved);
    }

    @Transactional
    public void receiveInventory(PurchaseOrder po) {
        List<PurchaseOrderItem> items = orderItemRepository.findByPurchaseOrderId(po.getId());
        for (PurchaseOrderItem item : items) {
            Product product = item.getProduct();
            Inventory inventory = inventoryRepository.findByProductId(product.getId())
                    .orElseGet(() -> new Inventory(product, 0, 10, 1000));

            int prevQty = inventory.getAvailableQuantity();
            int newQty = prevQty + item.getQuantity();
            inventory.setAvailableQuantity(newQty);
            inventory.setLastUpdated(LocalDateTime.now());
            inventoryRepository.save(inventory);

            StockTransaction tx = new StockTransaction(
                    product,
                    TransactionType.PURCHASE_RECEIVED,
                    item.getQuantity(),
                    prevQty,
                    newQty,
                    po.getOrderNumber(),
                    "Received goods for Purchase Order " + po.getOrderNumber(),
                    "SYSTEM"
            );
            transactionRepository.save(tx);
        }
        po.setInventoryReceived(true);
    }

    @Transactional
    public void deletePurchaseOrder(Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order not found with id: " + id));
        if (Boolean.TRUE.equals(po.getInventoryReceived())) {
            throw new BadRequestException("Cannot delete a Purchase Order that has already been received into inventory.");
        }
        purchaseOrderRepository.delete(po);
    }

    public PurchaseOrderDto toDto(PurchaseOrder po) {
        PurchaseOrderDto dto = new PurchaseOrderDto();
        dto.setId(po.getId());
        dto.setOrderNumber(po.getOrderNumber());
        if (po.getVendor() != null) {
            dto.setVendorId(po.getVendor().getId());
            dto.setVendorCompanyName(po.getVendor().getCompanyName());
            dto.setVendorEmail(po.getVendor().getEmail());
            dto.setVendorPhone(po.getVendor().getPhone());
            dto.setVendorAddress(po.getVendor().getAddress() + ", " + po.getVendor().getCity());
        }
        if (po.getProcurementRequest() != null) {
            dto.setProcurementRequestId(po.getProcurementRequest().getId());
            dto.setProcurementRequestNumber(po.getProcurementRequest().getRequestNumber());
        }
        dto.setOrderDate(po.getOrderDate());
        dto.setExpectedDeliveryDate(po.getExpectedDeliveryDate());
        dto.setStatus(po.getStatus());
        dto.setSubtotal(po.getSubtotal());
        dto.setTaxRate(po.getTaxRate());
        dto.setTaxAmount(po.getTaxAmount());
        dto.setTotalAmount(po.getTotalAmount());
        dto.setNotes(po.getNotes());
        dto.setInventoryReceived(po.getInventoryReceived());
        dto.setCreatedAt(po.getCreatedAt());
        dto.setUpdatedAt(po.getUpdatedAt());

        List<PurchaseOrderItem> items = orderItemRepository.findByPurchaseOrderId(po.getId());
        dto.setItems(items.stream().map(i -> {
            PurchaseOrderItemDto itemDto = new PurchaseOrderItemDto();
            itemDto.setId(i.getId());
            itemDto.setProductId(i.getProduct().getId());
            itemDto.setProductName(i.getProduct().getName());
            itemDto.setProductSku(i.getProduct().getSku());
            itemDto.setUnitOfMeasurement(i.getProduct().getUnitOfMeasurement());
            itemDto.setQuantity(i.getQuantity());
            itemDto.setUnitPrice(i.getUnitPrice());
            itemDto.setTotalPrice(i.getTotalPrice());
            return itemDto;
        }).collect(Collectors.toList()));

        return dto;
    }
}
