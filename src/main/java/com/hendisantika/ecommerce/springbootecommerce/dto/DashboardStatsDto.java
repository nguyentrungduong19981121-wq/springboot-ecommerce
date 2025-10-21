package com.hendisantika.ecommerce.springbootecommerce.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 08:00
 */
public class DashboardStatsDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Double totalRevenue;
    private Long totalOrders;
    private Long newCustomersToday;
    private Long newCustomersThisMonth;
    private List<BestSellingProductDto> bestSellingProducts;

    public DashboardStatsDto() {
    }

    public DashboardStatsDto(Double totalRevenue, Long totalOrders, Long newCustomersToday, 
                             Long newCustomersThisMonth, List<BestSellingProductDto> bestSellingProducts) {
        this.totalRevenue = totalRevenue;
        this.totalOrders = totalOrders;
        this.newCustomersToday = newCustomersToday;
        this.newCustomersThisMonth = newCustomersThisMonth;
        this.bestSellingProducts = bestSellingProducts;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Long getNewCustomersToday() {
        return newCustomersToday;
    }

    public void setNewCustomersToday(Long newCustomersToday) {
        this.newCustomersToday = newCustomersToday;
    }

    public Long getNewCustomersThisMonth() {
        return newCustomersThisMonth;
    }

    public void setNewCustomersThisMonth(Long newCustomersThisMonth) {
        this.newCustomersThisMonth = newCustomersThisMonth;
    }

    public List<BestSellingProductDto> getBestSellingProducts() {
        return bestSellingProducts;
    }

    public void setBestSellingProducts(List<BestSellingProductDto> bestSellingProducts) {
        this.bestSellingProducts = bestSellingProducts;
    }
}
