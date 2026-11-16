package com.supplysync.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DashboardStatsDto {
    private long totalVendors;
    private long totalProducts;
    private long totalInventoryItems;
    private long totalPurchaseOrders;
    private long pendingOrders;
    private long completedDeliveries;
    private long lowStockProducts;
    private BigDecimal totalProcurementValue;

    // Chart Data
    private Map<String, Object> monthlyOrdersChart;
    private Map<String, Object> inventoryDistributionChart;
    private Map<String, Object> orderStatusChart;

    // Recent items
    private List<PurchaseOrderDto> recentPurchaseOrders;
    private List<StockTransactionDto> recentTransactions;
    private List<InventoryDto> lowStockAlerts;
    private List<VendorDto> recentVendors;

    public DashboardStatsDto() {}

    public long getTotalVendors() { return totalVendors; }
    public void setTotalVendors(long totalVendors) { this.totalVendors = totalVendors; }

    public long getTotalProducts() { return totalProducts; }
    public void setTotalProducts(long totalProducts) { this.totalProducts = totalProducts; }

    public long getTotalInventoryItems() { return totalInventoryItems; }
    public void setTotalInventoryItems(long totalInventoryItems) { this.totalInventoryItems = totalInventoryItems; }

    public long getTotalPurchaseOrders() { return totalPurchaseOrders; }
    public void setTotalPurchaseOrders(long totalPurchaseOrders) { this.totalPurchaseOrders = totalPurchaseOrders; }

    public long getPendingOrders() { return pendingOrders; }
    public void setPendingOrders(long pendingOrders) { this.pendingOrders = pendingOrders; }

    public long getCompletedDeliveries() { return completedDeliveries; }
    public void setCompletedDeliveries(long completedDeliveries) { this.completedDeliveries = completedDeliveries; }

    public long getLowStockProducts() { return lowStockProducts; }
    public void setLowStockProducts(long lowStockProducts) { this.lowStockProducts = lowStockProducts; }

    public BigDecimal getTotalProcurementValue() { return totalProcurementValue; }
    public void setTotalProcurementValue(BigDecimal totalProcurementValue) { this.totalProcurementValue = totalProcurementValue; }

    public Map<String, Object> getMonthlyOrdersChart() { return monthlyOrdersChart; }
    public void setMonthlyOrdersChart(Map<String, Object> monthlyOrdersChart) { this.monthlyOrdersChart = monthlyOrdersChart; }

    public Map<String, Object> getInventoryDistributionChart() { return inventoryDistributionChart; }
    public void setInventoryDistributionChart(Map<String, Object> inventoryDistributionChart) { this.inventoryDistributionChart = inventoryDistributionChart; }

    public Map<String, Object> getOrderStatusChart() { return orderStatusChart; }
    public void setOrderStatusChart(Map<String, Object> orderStatusChart) { this.orderStatusChart = orderStatusChart; }

    public List<PurchaseOrderDto> getRecentPurchaseOrders() { return recentPurchaseOrders; }
    public void setRecentPurchaseOrders(List<PurchaseOrderDto> recentPurchaseOrders) { this.recentPurchaseOrders = recentPurchaseOrders; }

    public List<StockTransactionDto> getRecentTransactions() { return recentTransactions; }
    public void setRecentTransactions(List<StockTransactionDto> recentTransactions) { this.recentTransactions = recentTransactions; }

    public List<InventoryDto> getLowStockAlerts() { return lowStockAlerts; }
    public void setLowStockAlerts(List<InventoryDto> lowStockAlerts) { this.lowStockAlerts = lowStockAlerts; }

    public List<VendorDto> getRecentVendors() { return recentVendors; }
    public void setRecentVendors(List<VendorDto> recentVendors) { this.recentVendors = recentVendors; }
}
