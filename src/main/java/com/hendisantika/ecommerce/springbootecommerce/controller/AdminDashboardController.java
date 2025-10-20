package com.hendisantika.ecommerce.springbootecommerce.controller;

import com.hendisantika.ecommerce.springbootecommerce.dto.DashboardStatsDto;
import com.hendisantika.ecommerce.springbootecommerce.dto.SalesReportDto;
import com.hendisantika.ecommerce.springbootecommerce.service.AdminDashboardService;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 09:30
 */
@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    /**
     * GET /api/admin/dashboard
     * Returns overview statistics: revenue, orders, new customers, and best-selling products
     * 
     * @return DashboardStatsDto with all overview statistics
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsDto> getDashboardOverview() {
        DashboardStatsDto stats = adminDashboardService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * GET /api/admin/reports/sales
     * Returns sales report (chart data) by day or month
     * 
     * @param period "daily" or "monthly" - the aggregation period
     * @param startDate Start date for the report (format: yyyy-MM-dd)
     * @param endDate End date for the report (format: yyyy-MM-dd)
     * @return SalesReportDto with sales data points for the chart
     */
    @GetMapping("/reports/sales")
    public ResponseEntity<SalesReportDto> getSalesReport(
            @RequestParam(defaultValue = "daily") String period,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        // Validate period parameter
        if (!period.equalsIgnoreCase("daily") && !period.equalsIgnoreCase("monthly")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        
        // Validate date range
        if (startDate.isAfter(endDate)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        
        SalesReportDto report = adminDashboardService.getSalesReport(period, startDate, endDate);
        return ResponseEntity.ok(report);
    }
}
