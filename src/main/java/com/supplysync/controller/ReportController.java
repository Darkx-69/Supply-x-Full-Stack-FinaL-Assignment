package com.supplysync.controller;

import com.supplysync.dto.ApiResponse;
import com.supplysync.enums.OrderStatus;
import com.supplysync.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/inventory")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getInventoryReport() {
        Map<String, Object> report = reportService.getInventoryReport();
        return ResponseEntity.ok(ApiResponse.success("Inventory report generated", report));
    }

    @GetMapping("/vendors")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getVendorPerformanceReport() {
        Map<String, Object> report = reportService.getVendorPerformanceReport();
        return ResponseEntity.ok(ApiResponse.success("Vendor performance report generated", report));
    }

    @GetMapping("/purchase-orders")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPurchaseOrderReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) OrderStatus status) {
        Map<String, Object> report = reportService.getPurchaseOrderReport(startDate, endDate, status);
        return ResponseEntity.ok(ApiResponse.success("Purchase order report generated", report));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLowStockReport() {
        Map<String, Object> report = reportService.getLowStockReport();
        return ResponseEntity.ok(ApiResponse.success("Low stock report generated", report));
    }

    @GetMapping("/export/{type}")
    public ResponseEntity<byte[]> exportReportCsv(@PathVariable String type) {
        String csv = reportService.exportToCsv(type);
        byte[] bytes = csv.getBytes(java.nio.charset.StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + type + "_report.csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(bytes);
    }
}
