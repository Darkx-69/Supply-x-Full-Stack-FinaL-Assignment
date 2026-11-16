package com.supplysync.repository;

import com.supplysync.entity.ProcurementRequestItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProcurementRequestItemRepository extends JpaRepository<ProcurementRequestItem, Long> {
    List<ProcurementRequestItem> findByProcurementRequestId(Long procurementRequestId);
}
