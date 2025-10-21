package com.hendisantika.ecommerce.springbootecommerce.controller;

import com.hendisantika.ecommerce.springbootecommerce.repository.CustomerRepository;
import com.hendisantika.ecommerce.springbootecommerce.repository.OrderRepository;
import com.hendisantika.ecommerce.springbootecommerce.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public ReportController(OrderRepository orderRepository, 
                           ProductRepository productRepository,
                           CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/sales")
    public ResponseEntity<Map<String, Object>> getSalesReport(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        
        if (startDate == null) startDate = LocalDate.now().minusMonths(1);
        if (endDate == null) endDate = LocalDate.now();

        var orders = orderRepository.findOrdersBetweenDates(startDate, endDate);
        
        double totalRevenue = orders.stream()
                .mapToDouble(order -> order.getTotalPrice())
                .sum();
        
        long totalOrders = orders.size();
        double averageOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0;

        Map<String, Object> report = new HashMap<>();
        report.put("startDate", startDate);
        report.put("endDate", endDate);
        report.put("totalRevenue", totalRevenue);
        report.put("totalOrders", totalOrders);
        report.put("averageOrderValue", averageOrderValue);
        
        return ResponseEntity.ok(report);
    }

    @GetMapping("/top-products")
    public ResponseEntity<Map<String, Object>> getTopProducts(
            @RequestParam(defaultValue = "10") int limit) {
        
        var topProducts = productRepository.findBestSellingProducts();
        
        var limitedProducts = topProducts.stream()
                .limit(limit)
                .collect(Collectors.toList());

        Map<String, Object> report = new HashMap<>();
        report.put("topProducts", limitedProducts);
        report.put("count", limitedProducts.size());
        
        return ResponseEntity.ok(report);
    }

    @GetMapping("/customers")
    public ResponseEntity<Map<String, Object>> getCustomerReport() {
        long totalCustomers = customerRepository.count();
        var allCustomers = customerRepository.findAll();
        
        var recentCustomers = allCustomers.stream()
                .sorted((a, b) -> b.getRegisteredDate().compareTo(a.getRegisteredDate()))
                .limit(10)
                .collect(Collectors.toList());

        Map<String, Object> report = new HashMap<>();
        report.put("totalCustomers", totalCustomers);
        report.put("recentCustomers", recentCustomers);
        
        return ResponseEntity.ok(report);
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummaryReport() {
        long totalOrders = orderRepository.count();
        long totalProducts = productRepository.count();
        long totalCustomers = customerRepository.count();
        
        var allOrders = orderRepository.findAll();
        double totalRevenue = allOrders.stream()
                .mapToDouble(order -> order.getTotalPrice())
                .sum();

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalOrders", totalOrders);
        summary.put("totalProducts", totalProducts);
        summary.put("totalCustomers", totalCustomers);
        summary.put("totalRevenue", totalRevenue);
        
        return ResponseEntity.ok(summary);
    }
}
