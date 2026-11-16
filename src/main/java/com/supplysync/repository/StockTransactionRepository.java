package com.supplysync.repository;

import com.supplysync.entity.StockTransaction;
import com.supplysync.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {
    List<StockTransaction> findByProductIdOrderByTransactionDateDesc(Long productId);

    @Query("SELECT st FROM StockTransaction st WHERE " +
           "(:query IS NULL OR :query = '' OR " +
           "LOWER(st.product.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(st.product.sku) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(st.referenceOrder) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:type IS NULL OR st.transactionType = :type) " +
           "ORDER BY st.transactionDate DESC")
    List<StockTransaction> searchTransactions(@Param("query") String query,
                                             @Param("type") TransactionType type);

    @Query("SELECT st FROM StockTransaction st ORDER BY st.transactionDate DESC LIMIT 5")
    List<StockTransaction> findRecentTransactions();
}
