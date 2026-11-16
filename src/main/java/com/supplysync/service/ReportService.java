package com.supplysync.service;

import com.supplysync.entity.*;
import com.supplysync.enums.OrderStatus;
import com.supplysync.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final InventoryRepository inventoryRepository;
    private final VendorRepository vendorRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final DeliveryRepository deliveryRepository;

    public ReportService(InventoryRepository inventoryRepository,
                         VendorRepository vendorRepository,
                         PurchaseOrderRepository purchaseOrderRepository,
                         DeliveryRepository deliveryRepository) {
        this.inventoryRepository = inventoryRepository;
        this.vendorRepository = vendorRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.deliveryRepository = deliveryRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getInventoryReport() {
        List<Inventory> list = inventoryRepository.findAll();
        List<Map<String, Object>> rows = new ArrayList<>();
        BigDecimal totalVal = BigDecimal.ZERO;
        long totalItems = 0;

        for (Inventory inv : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("productId", inv.getProduct().getId());
            map.put("productName", inv.getProduct().getName());
            map.put("sku", inv.getProduct().getSku());
            map.put("category", inv.getProduct().getCategory() != null ? inv.getProduct().getCategory().getName() : "-");
            map.put("unitPrice", inv.getProduct().getUnitPrice());
            map.put("availableQuantity", inv.getAvailableQuantity());
            map.put("minimumStockLevel", inv.getMinimumStockLevel());
            map.put("maximumStockLevel", inv.getMaximumStockLevel());
            BigDecimal val = inv.getProduct().getUnitPrice().multiply(BigDecimal.valueOf(inv.getAvailableQuantity()));
            map.put("totalValue", val);
            map.put("isLowStock", inv.isLowStock());
            rows.add(map);

            totalVal = totalVal.add(val);
            totalItems += inv.getAvailableQuantity();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("records", rows);
        result.put("totalStockUnits", totalItems);
        result.put("totalValuation", totalVal);
        result.put("totalProducts", list.size());
        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getVendorPerformanceReport() {
        List<Vendor> vendors = vendorRepository.findAll();
        List<PurchaseOrder> allOrders = purchaseOrderRepository.findAll();
        List<Map<String, Object>> rows = new ArrayList<>();

        for (Vendor v : vendors) {
            List<PurchaseOrder> vOrders = allOrders.stream()
                    .filter(o -> o.getVendor().getId().equals(v.getId()))
                    .collect(Collectors.toList());

            long totalOrders = vOrders.size();
            long completedOrders = vOrders.stream().filter(o -> o.getStatus() == OrderStatus.DELIVERED).count();
            BigDecimal totalSpend = vOrders.stream()
                    .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
                    .map(PurchaseOrder::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> map = new HashMap<>();
            map.put("vendorId", v.getId());
            map.put("companyName", v.getCompanyName());
            map.put("contactPerson", v.getContactPerson());
            map.put("email", v.getEmail());
            map.put("phone", v.getPhone());
            map.put("city", v.getCity());
            map.put("status", v.getVendorStatus());
            map.put("totalOrders", totalOrders);
            map.put("completedOrders", completedOrders);
            map.put("totalSpend", totalSpend);
            rows.add(map);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("records", rows);
        result.put("totalVendors", vendors.size());
        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPurchaseOrderReport(LocalDate startDate, LocalDate endDate, OrderStatus status) {
        List<PurchaseOrder> orders = purchaseOrderRepository.findAll();

        if (startDate != null) {
            orders = orders.stream().filter(o -> o.getOrderDate() != null && !o.getOrderDate().isBefore(startDate)).collect(Collectors.toList());
        }
        if (endDate != null) {
            orders = orders.stream().filter(o -> o.getOrderDate() != null && !o.getOrderDate().isAfter(endDate)).collect(Collectors.toList());
        }
        if (status != null) {
            orders = orders.stream().filter(o -> o.getStatus() == status).collect(Collectors.toList());
        }

        List<Map<String, Object>> rows = new ArrayList<>();
        BigDecimal totalSpent = BigDecimal.ZERO;

        for (PurchaseOrder po : orders) {
            Map<String, Object> map = new HashMap<>();
            map.put("orderId", po.getId());
            map.put("orderNumber", po.getOrderNumber());
            map.put("vendor", po.getVendor() != null ? po.getVendor().getCompanyName() : "-");
            map.put("orderDate", po.getOrderDate());
            map.put("expectedDeliveryDate", po.getExpectedDeliveryDate());
            map.put("status", po.getStatus());
            map.put("subtotal", po.getSubtotal());
            map.put("taxAmount", po.getTaxAmount());
            map.put("totalAmount", po.getTotalAmount());
            rows.add(map);

            if (po.getStatus() != OrderStatus.CANCELLED) {
                totalSpent = totalSpent.add(po.getTotalAmount());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("records", rows);
        result.put("totalOrders", orders.size());
        result.put("totalAmount", totalSpent);
        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getLowStockReport() {
        List<Inventory> list = inventoryRepository.findLowStockItems();
        List<Map<String, Object>> rows = new ArrayList<>();

        for (Inventory inv : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("productId", inv.getProduct().getId());
            map.put("productName", inv.getProduct().getName());
            map.put("sku", inv.getProduct().getSku());
            map.put("category", inv.getProduct().getCategory() != null ? inv.getProduct().getCategory().getName() : "-");
            map.put("availableQuantity", inv.getAvailableQuantity());
            map.put("minimumStockLevel", inv.getMinimumStockLevel());
            map.put("deficit", inv.getMinimumStockLevel() - inv.getAvailableQuantity());
            map.put("unitPrice", inv.getProduct().getUnitPrice());
            rows.add(map);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("records", rows);
        result.put("count", list.size());
        return result;
    }

    @Transactional(readOnly = true)
    public String exportToCsv(String reportType) {
        StringBuilder sb = new StringBuilder();

        switch (reportType.toLowerCase()) {
            case "inventory":
                sb.append("Product ID,Product Name,SKU,Category,Unit Price,Available Quantity,Min Stock,Max Stock,Total Value,Status\n");
                for (Inventory inv : inventoryRepository.findAll()) {
                    BigDecimal val = inv.getProduct().getUnitPrice().multiply(BigDecimal.valueOf(inv.getAvailableQuantity()));
                    sb.append(inv.getProduct().getId()).append(",")
                      .append("\"").append(inv.getProduct().getName().replace("\"", "\"\"")).append("\",")
                      .append(inv.getProduct().getSku()).append(",")
                      .append("\"").append(inv.getProduct().getCategory() != null ? inv.getProduct().getCategory().getName() : "").append("\",")
                      .append(inv.getProduct().getUnitPrice()).append(",")
                      .append(inv.getAvailableQuantity()).append(",")
                      .append(inv.getMinimumStockLevel()).append(",")
                      .append(inv.getMaximumStockLevel()).append(",")
                      .append(val).append(",")
                      .append(inv.isLowStock() ? "LOW STOCK" : "IN STOCK").append("\n");
                }
                break;

            case "vendors":
                sb.append("Vendor ID,Company Name,Contact Person,Email,Phone,City,Status,Total Orders,Total Spend\n");
                List<PurchaseOrder> orders = purchaseOrderRepository.findAll();
                for (Vendor v : vendorRepository.findAll()) {
                    List<PurchaseOrder> vOrders = orders.stream().filter(o -> o.getVendor().getId().equals(v.getId())).toList();
                    BigDecimal total = vOrders.stream().filter(o -> o.getStatus() != OrderStatus.CANCELLED)
                            .map(PurchaseOrder::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    sb.append(v.getId()).append(",")
                      .append("\"").append(v.getCompanyName().replace("\"", "\"\"")).append("\",")
                      .append("\"").append(v.getContactPerson().replace("\"", "\"\"")).append("\",")
                      .append(v.getEmail()).append(",")
                      .append(v.getPhone()).append(",")
                      .append("\"").append(v.getCity() != null ? v.getCity() : "").append("\",")
                      .append(v.getVendorStatus()).append(",")
                      .append(vOrders.size()).append(",")
                      .append(total).append("\n");
                }
                break;

            case "orders":
                sb.append("Order ID,Order Number,Vendor,Order Date,Expected Delivery,Status,Subtotal,Tax,Total\n");
                for (PurchaseOrder po : purchaseOrderRepository.findAll()) {
                    sb.append(po.getId()).append(",")
                      .append(po.getOrderNumber()).append(",")
                      .append("\"").append(po.getVendor() != null ? po.getVendor().getCompanyName().replace("\"", "\"\"") : "").append("\",")
                      .append(po.getOrderDate()).append(",")
                      .append(po.getExpectedDeliveryDate() != null ? po.getExpectedDeliveryDate() : "").append(",")
                      .append(po.getStatus()).append(",")
                      .append(po.getSubtotal()).append(",")
                      .append(po.getTaxAmount()).append(",")
                      .append(po.getTotalAmount()).append("\n");
                }
                break;

            case "low-stock":
                sb.append("Product ID,Product Name,SKU,Category,Available Quantity,Min Stock,Deficit,Unit Price\n");
                for (Inventory inv : inventoryRepository.findLowStockItems()) {
                    int deficit = inv.getMinimumStockLevel() - inv.getAvailableQuantity();
                    sb.append(inv.getProduct().getId()).append(",")
                      .append("\"").append(inv.getProduct().getName().replace("\"", "\"\"")).append("\",")
                      .append(inv.getProduct().getSku()).append(",")
                      .append("\"").append(inv.getProduct().getCategory() != null ? inv.getProduct().getCategory().getName() : "").append("\",")
                      .append(inv.getAvailableQuantity()).append(",")
                      .append(inv.getMinimumStockLevel()).append(",")
                      .append(deficit).append(",")
                      .append(inv.getProduct().getUnitPrice()).append("\n");
                }
                break;

            default:
                sb.append("Message\nUnknown report type\n");
        }

        return sb.toString();
    }
}
