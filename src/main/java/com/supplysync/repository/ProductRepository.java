package com.supplysync.repository;

import com.supplysync.entity.Product;
import com.supplysync.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySku(String sku);
    boolean existsBySku(String sku);
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByStatus(ProductStatus status);

    @Query("SELECT p FROM Product p WHERE " +
           "(:query IS NULL OR :query = '' OR " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.sku) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
           "(:status IS NULL OR p.status = :status) " +
           "ORDER BY p.id DESC")
    List<Product> searchProducts(@Param("query") String query,
                                 @Param("categoryId") Long categoryId,
                                 @Param("status") ProductStatus status);
}
