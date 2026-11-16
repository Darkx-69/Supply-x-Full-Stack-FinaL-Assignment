package com.supplysync.service;

import com.supplysync.dto.*;
import com.supplysync.entity.Category;
import com.supplysync.entity.Inventory;
import com.supplysync.entity.PurchaseOrder;
import com.supplysync.entity.Vendor;
import com.supplysync.enums.DeliveryStatus;
import com.supplysync.enums.OrderStatus;
import com.supplysync.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final VendorRepository vendorRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final DeliveryRepository deliveryRepository;
    private final StockTransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final PurchaseOrderService purchaseOrderService;
    private final InventoryService inventoryService;
    private final VendorService vendorService;

    public DashboardService(VendorRepository vendorRepository,
                            ProductRepository productRepository,
                            InventoryRepository inventoryRepository,
                            PurchaseOrderRepository purchaseOrderRepository,
                            DeliveryRepository deliveryRepository,
                            StockTransactionRepository transactionRepository,
                            CategoryRepository categoryRepository,
                            PurchaseOrderService purchaseOrderService,
                            InventoryService inventoryService,
                            VendorService vendorService) {
        this.vendorRepository = vendorRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.deliveryRepository = deliveryRepository;
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.purchaseOrderService = purchaseOrderService;
        this.inventoryService = inventoryService;
        this.vendorService = vendorService;
    }

    @Transactional(readOnly = true)
    public DashboardStatsDto getAdminDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();

        stats.setTotalVendors(vendorRepository.count());
        stats.setTotalProducts(productRepository.count());

        Long totalInv = inventoryRepository.countTotalInventoryQuantity();
        stats.setTotalInventoryItems(totalInv != null ? totalInv : 0L);

        stats.setTotalPurchaseOrders(purchaseOrderRepository.count());
        stats.setPendingOrders(purchaseOrderRepository.countByStatus(OrderStatus.PENDING));
        stats.setCompletedDeliveries(deliveryRepository.countByShipmentStatus(DeliveryStatus.DELIVERED));

        Long lowStockCount = inventoryRepository.countLowStockProducts();
        stats.setLowStockProducts(lowStockCount != null ? lowStockCount : 0L);

        BigDecimal totalProcurement = purchaseOrderRepository.sumTotalProcurementValue();
        stats.setTotalProcurementValue(totalProcurement != null ? totalProcurement : BigDecimal.ZERO);

        // Chart 1: Monthly Orders Chart (Last 6 Months)
        List<PurchaseOrder> allOrders = purchaseOrderRepository.findAll();
        Map<String, Object> monthlyChart = buildMonthlyOrdersChart(allOrders);
        stats.setMonthlyOrdersChart(monthlyChart);

        // Chart 2: Inventory Distribution by Category
        Map<String, Object> invDistChart = buildInventoryDistributionChart();
        stats.setInventoryDistributionChart(invDistChart);

        // Chart 3: Order Status Distribution
        Map<String, Object> statusChart = buildOrderStatusChart(allOrders);
        stats.setOrderStatusChart(statusChart);

        // Recent items
        stats.setRecentPurchaseOrders(
                purchaseOrderRepository.findRecentPurchaseOrders().stream()
                        .map(purchaseOrderService::toDto)
                        .collect(Collectors.toList())
        );

        stats.setRecentTransactions(
                transactionRepository.findRecentTransactions().stream()
                        .map(inventoryService::toTransactionDto)
                        .collect(Collectors.toList())
        );

        stats.setLowStockAlerts(
                inventoryRepository.findLowStockItems().stream()
                        .map(inventoryService::toDto)
                        .collect(Collectors.toList())
        );

        List<Vendor> allVendors = vendorRepository.findAll();
        List<VendorDto> recentVendors = allVendors.stream()
                .sorted(Comparator.comparing(Vendor::getId).reversed())
                .limit(5)
                .map(vendorService::toDto)
                .collect(Collectors.toList());
        stats.setRecentVendors(recentVendors);

        return stats;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getVendorDashboardStats(Long vendorId) {
        Map<String, Object> data = new HashMap<>();

        List<PurchaseOrder> vendorOrders = purchaseOrderRepository.findByVendorId(vendorId);

        long totalOrders = vendorOrders.size();
        long pendingOrders = vendorOrders.stream().filter(o -> o.getStatus() == OrderStatus.PENDING).count();
        long processingOrders = vendorOrders.stream().filter(o -> o.getStatus() == OrderStatus.PROCESSING || o.getStatus() == OrderStatus.APPROVED).count();
        long shippedOrders = vendorOrders.stream().filter(o -> o.getStatus() == OrderStatus.SHIPPED).count();
        long deliveredOrders = vendorOrders.stream().filter(o -> o.getStatus() == OrderStatus.DELIVERED).count();

        BigDecimal totalValue = vendorOrders.stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
                .map(PurchaseOrder::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        data.put("totalOrders", totalOrders);
        data.put("pendingOrders", pendingOrders);
        data.put("processingOrders", processingOrders);
        data.put("shippedOrders", shippedOrders);
        data.put("deliveredOrders", deliveredOrders);
        data.put("totalValue", totalValue);

        // Monthly orders for vendor
        data.put("monthlyOrdersChart", buildMonthlyOrdersChart(vendorOrders));
        data.put("orderStatusChart", buildOrderStatusChart(vendorOrders));

        List<PurchaseOrderDto> recentOrders = vendorOrders.stream()
                .sorted(Comparator.comparing(PurchaseOrder::getId).reversed())
                .limit(5)
                .map(purchaseOrderService::toDto)
                .collect(Collectors.toList());
        data.put("recentOrders", recentOrders);

        return data;
    }

    private Map<String, Object> buildMonthlyOrdersChart(List<PurchaseOrder> orders) {
        List<String> labels = new ArrayList<>();
        List<Integer> orderCounts = new ArrayList<>();
        List<BigDecimal> orderAmounts = new ArrayList<>();

        YearMonth current = YearMonth.now();
        DateTimeFormatter labelFormat = DateTimeFormatter.ofPattern("MMM yyyy");

        // Last 6 months
        for (int i = 5; i >= 0; i--) {
            YearMonth ym = current.minusMonths(i);
            labels.add(ym.format(labelFormat));

            int count = 0;
            BigDecimal sum = BigDecimal.ZERO;

            for (PurchaseOrder po : orders) {
                if (po.getOrderDate() != null) {
                    YearMonth poYm = YearMonth.from(po.getOrderDate());
                    if (poYm.equals(ym)) {
                        count++;
                        if (po.getTotalAmount() != null) {
                            sum = sum.add(po.getTotalAmount());
                        }
                    }
                }
            }
            orderCounts.add(count);
            orderAmounts.add(sum);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("labels", labels);
        map.put("counts", orderCounts);
        map.put("amounts", orderAmounts);
        return map;
    }

    private Map<String, Object> buildInventoryDistributionChart() {
        List<Category> categories = categoryRepository.findAll();
        List<String> labels = new ArrayList<>();
        List<Long> quantities = new ArrayList<>();

        List<Inventory> allInventory = inventoryRepository.findAll();
        for (Category cat : categories) {
            labels.add(cat.getName());
            long sum = allInventory.stream()
                    .filter(inv -> inv.getProduct().getCategory().getId().equals(cat.getId()))
                    .mapToLong(Inventory::getAvailableQuantity)
                    .sum();
            quantities.add(sum);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("labels", labels);
        map.put("quantities", quantities);
        return map;
    }

    private Map<String, Object> buildOrderStatusChart(List<PurchaseOrder> orders) {
        Map<OrderStatus, Long> counts = orders.stream()
                .collect(Collectors.groupingBy(PurchaseOrder::getStatus, Collectors.counting()));

        List<String> labels = new ArrayList<>();
        List<Long> values = new ArrayList<>();

        for (OrderStatus st : OrderStatus.values()) {
            labels.add(st.name());
            values.add(counts.getOrDefault(st, 0L));
        }

        Map<String, Object> map = new HashMap<>();
        map.put("labels", labels);
        map.put("counts", values);
        return map;
    }
}
