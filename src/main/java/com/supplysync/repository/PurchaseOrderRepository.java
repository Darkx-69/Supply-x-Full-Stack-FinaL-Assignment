package com.supplysync.repository;

import com.supplysync.entity.PurchaseOrder;
import com.supplysync.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    Optional<PurchaseOrder> findByOrderNumber(String orderNumber);
    List<PurchaseOrder> findByVendorId(Long vendorId);
    List<PurchaseOrder> findByStatus(OrderStatus status);

    @Query("SELECT po FROM PurchaseOrder po WHERE " +
           "(:query IS NULL OR :query = '' OR " +
           "LOWER(po.orderNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(po.vendor.companyName) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:status IS NULL OR po.status = :status) AND " +
           "(:vendorId IS NULL OR po.vendor.id = :vendorId) AND " +
           "(:startDate IS NULL OR po.orderDate >= :startDate) AND " +
           "(:endDate IS NULL OR po.orderDate <= :endDate) " +
           "ORDER BY po.id DESC")
    List<PurchaseOrder> searchPurchaseOrders(@Param("query") String query,
                                            @Param("status") OrderStatus status,
                                            @Param("vendorId") Long vendorId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);

    Long countByStatus(OrderStatus status);

    @Query("SELECT COALESCE(SUM(po.totalAmount), 0) FROM PurchaseOrder po WHERE po.status != com.supplysync.enums.OrderStatus.CANCELLED")
    BigDecimal sumTotalProcurementValue();

    @Query("SELECT po FROM PurchaseOrder po ORDER BY po.id DESC LIMIT 5")
    List<PurchaseOrder> findRecentPurchaseOrders();
}
