package com.hendisantika.ecommerce.springbootecommerce.service;

import com.hendisantika.ecommerce.springbootecommerce.dto.DashboardStatsDto;
import com.hendisantika.ecommerce.springbootecommerce.dto.SalesReportDto;

import java.time.LocalDate;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 09:00
 */
public interface AdminDashboardService {
    
    /**
     * Get dashboard overview statistics
     * @return DashboardStatsDto with revenue, orders, customers, and best-selling products
     */
    DashboardStatsDto getDashboardStats();
    
    /**
     * Get sales report for a specific period
     * @param period "daily" or "monthly"
     * @param startDate Start date for the report
     * @param endDate End date for the report
     * @return SalesReportDto with sales data points
     */
    SalesReportDto getSalesReport(String period, LocalDate startDate, LocalDate endDate);
}
