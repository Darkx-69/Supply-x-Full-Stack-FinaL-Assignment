package com.supplysync.repository;

import com.supplysync.entity.Delivery;
import com.supplysync.enums.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByDeliveryNumber(String deliveryNumber);
    Optional<Delivery> findByPurchaseOrderId(Long purchaseOrderId);
    List<Delivery> findByShipmentStatus(DeliveryStatus status);
    Long countByShipmentStatus(DeliveryStatus status);

    @Query("SELECT d FROM Delivery d WHERE " +
           "(:query IS NULL OR :query = '' OR " +
           "LOWER(d.deliveryNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.trackingNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.carrierName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.purchaseOrder.orderNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.purchaseOrder.vendor.companyName) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:status IS NULL OR d.shipmentStatus = :status) AND " +
           "(:vendorId IS NULL OR d.purchaseOrder.vendor.id = :vendorId) " +
           "ORDER BY d.id DESC")
    List<Delivery> searchDeliveries(@Param("query") String query,
                                   @Param("status") DeliveryStatus status,
                                   @Param("vendorId") Long vendorId);
}
