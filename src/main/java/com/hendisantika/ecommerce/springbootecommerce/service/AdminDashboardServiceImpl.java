package com.hendisantika.ecommerce.springbootecommerce.service;

import com.hendisantika.ecommerce.springbootecommerce.dto.BestSellingProductDto;
import com.hendisantika.ecommerce.springbootecommerce.dto.DashboardStatsDto;
import com.hendisantika.ecommerce.springbootecommerce.dto.SalesReportDto;
import com.hendisantika.ecommerce.springbootecommerce.model.Order;
import com.hendisantika.ecommerce.springbootecommerce.repository.CustomerRepository;
import com.hendisantika.ecommerce.springbootecommerce.repository.OrderRepository;
import com.hendisantika.ecommerce.springbootecommerce.repository.ProductRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 09:05
 */
@Service
@Transactional
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public AdminDashboardServiceImpl(OrderRepository orderRepository, 
                                    ProductRepository productRepository,
                                    CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    @Cacheable(value = "dashboardStats", unless = "#result == null")
    public DashboardStatsDto getDashboardStats() {
        // Calculate total revenue from all orders
        Double totalRevenue = calculateTotalRevenue();
        
        // Get total orders count
        Long totalOrders = orderRepository.countAllOrders();
        
        // Get new customers today
        LocalDate today = LocalDate.now();
        Long newCustomersToday = customerRepository.countNewCustomersSince(today);
        
        // Get new customers this month
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        Long newCustomersThisMonth = customerRepository.countNewCustomersSince(firstDayOfMonth);
        
        // Get best-selling products (top 5)
        List<BestSellingProductDto> bestSellingProducts = getBestSellingProducts(5);
        
        return new DashboardStatsDto(
                totalRevenue,
                totalOrders,
                newCustomersToday,
                newCustomersThisMonth,
                bestSellingProducts
        );
    }

    @Override
    @Cacheable(value = "salesReport", key = "#period + '_' + #startDate + '_' + #endDate", unless = "#result == null")
    public SalesReportDto getSalesReport(String period, LocalDate startDate, LocalDate endDate) {
        List<Order> orders = orderRepository.findOrdersBetweenDates(startDate, endDate);
        List<SalesReportDto.SalesDataPoint> salesData = new ArrayList<>();

        if ("daily".equalsIgnoreCase(period)) {
            salesData = generateDailySalesReport(orders, startDate, endDate);
        } else if ("monthly".equalsIgnoreCase(period)) {
            salesData = generateMonthlySalesReport(orders, startDate, endDate);
        }

        return new SalesReportDto(period, startDate, endDate, salesData);
    }

    private Double calculateTotalRevenue() {
        Double totalRevenue = 0.0;
        Iterable<Order> orders = orderRepository.findAll();
        for (Order order : orders) {
            totalRevenue += order.getTotalOrderPrice();
        }
        return totalRevenue;
    }

    private List<BestSellingProductDto> getBestSellingProducts(int limit) {
        List<Object[]> results = productRepository.findBestSellingProducts();
        
        return results.stream()
                .limit(limit)
                .map(result -> new BestSellingProductDto(
                        ((Number) result[0]).longValue(),  // productId
                        (String) result[1],                 // productName
                        ((Number) result[2]).longValue(),   // quantitySold
                        ((Number) result[3]).doubleValue()  // totalRevenue
                ))
                .collect(Collectors.toList());
    }

    private List<SalesReportDto.SalesDataPoint> generateDailySalesReport(List<Order> orders, 
                                                                          LocalDate startDate, 
                                                                          LocalDate endDate) {
        Map<LocalDate, SalesReportDto.SalesDataPoint> dailyData = new HashMap<>();
        
        // Initialize all dates in range with zero values
        LocalDate date = startDate;
        while (!date.isAfter(endDate)) {
            dailyData.put(date, new SalesReportDto.SalesDataPoint(date, 0.0, 0L));
            date = date.plusDays(1);
        }
        
        // Aggregate order data by date
        for (Order order : orders) {
            LocalDate orderDate = order.getDateCreated();
            if (orderDate != null && dailyData.containsKey(orderDate)) {
                SalesReportDto.SalesDataPoint dataPoint = dailyData.get(orderDate);
                dataPoint.setRevenue(dataPoint.getRevenue() + order.getTotalOrderPrice());
                dataPoint.setOrderCount(dataPoint.getOrderCount() + 1);
            }
        }
        
        return dailyData.values().stream()
                .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
                .collect(Collectors.toList());
    }

    private List<SalesReportDto.SalesDataPoint> generateMonthlySalesReport(List<Order> orders, 
                                                                            LocalDate startDate, 
                                                                            LocalDate endDate) {
        Map<YearMonth, SalesReportDto.SalesDataPoint> monthlyData = new HashMap<>();
        
        // Initialize all months in range with zero values
        YearMonth startMonth = YearMonth.from(startDate);
        YearMonth endMonth = YearMonth.from(endDate);
        YearMonth currentMonth = startMonth;
        
        while (!currentMonth.isAfter(endMonth)) {
            LocalDate firstDayOfMonth = currentMonth.atDay(1);
            monthlyData.put(currentMonth, new SalesReportDto.SalesDataPoint(firstDayOfMonth, 0.0, 0L));
            currentMonth = currentMonth.plusMonths(1);
        }
        
        // Aggregate order data by month
        for (Order order : orders) {
            LocalDate orderDate = order.getDateCreated();
            if (orderDate != null) {
                YearMonth orderMonth = YearMonth.from(orderDate);
                if (monthlyData.containsKey(orderMonth)) {
                    SalesReportDto.SalesDataPoint dataPoint = monthlyData.get(orderMonth);
                    dataPoint.setRevenue(dataPoint.getRevenue() + order.getTotalOrderPrice());
                    dataPoint.setOrderCount(dataPoint.getOrderCount() + 1);
                }
            }
        }
        
        return monthlyData.values().stream()
                .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
                .collect(Collectors.toList());
    }
}
