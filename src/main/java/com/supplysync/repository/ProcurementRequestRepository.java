package com.supplysync.repository;

import com.supplysync.entity.ProcurementRequest;
import com.supplysync.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProcurementRequestRepository extends JpaRepository<ProcurementRequest, Long> {
    Optional<ProcurementRequest> findByRequestNumber(String requestNumber);
    List<ProcurementRequest> findByVendorId(Long vendorId);
    List<ProcurementRequest> findByStatus(RequestStatus status);

    @Query("SELECT pr FROM ProcurementRequest pr WHERE " +
           "(:query IS NULL OR :query = '' OR " +
           "LOWER(pr.requestNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(pr.vendor.companyName) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:status IS NULL OR pr.status = :status) AND " +
           "(:vendorId IS NULL OR pr.vendor.id = :vendorId) " +
           "ORDER BY pr.id DESC")
    List<ProcurementRequest> searchRequests(@Param("query") String query,
                                            @Param("status") RequestStatus status,
                                            @Param("vendorId") Long vendorId);
}
