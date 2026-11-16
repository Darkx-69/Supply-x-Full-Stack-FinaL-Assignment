package com.supplysync.repository;

import com.supplysync.entity.Vendor;
import com.supplysync.enums.VendorStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Vendor> findByVendorStatus(VendorStatus status);

    @Query("SELECT v FROM Vendor v WHERE " +
           "(:query IS NULL OR :query = '' OR " +
           "LOWER(v.companyName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(v.contactPerson) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(v.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(v.city) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:status IS NULL OR v.vendorStatus = :status) AND " +
           "(:category IS NULL OR :category = '' OR LOWER(v.businessCategory) = LOWER(:category)) " +
           "ORDER BY v.id DESC")
    List<Vendor> searchVendors(@Param("query") String query,
                               @Param("status") VendorStatus status,
                               @Param("category") String category);
}
