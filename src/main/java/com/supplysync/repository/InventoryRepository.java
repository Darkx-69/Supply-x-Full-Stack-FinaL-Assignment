package com.supplysync.repository;

import com.supplysync.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductId(Long productId);

    @Query("SELECT i FROM Inventory i WHERE i.availableQuantity <= i.minimumStockLevel")
    List<Inventory> findLowStockItems();

    @Query("SELECT i FROM Inventory i WHERE " +
           "(:query IS NULL OR :query = '' OR " +
           "LOWER(i.product.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(i.product.sku) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:categoryId IS NULL OR i.product.category.id = :categoryId) AND " +
           "(:lowStockOnly = false OR i.availableQuantity <= i.minimumStockLevel) " +
           "ORDER BY i.availableQuantity ASC")
    List<Inventory> searchInventory(@Param("query") String query,
                                   @Param("categoryId") Long categoryId,
                                   @Param("lowStockOnly") boolean lowStockOnly);

    @Query("SELECT SUM(i.availableQuantity) FROM Inventory i")
    Long countTotalInventoryQuantity();

    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.availableQuantity <= i.minimumStockLevel")
    Long countLowStockProducts();
}
