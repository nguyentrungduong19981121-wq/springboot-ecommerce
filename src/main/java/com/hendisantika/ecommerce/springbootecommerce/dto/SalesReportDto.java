package com.hendisantika.ecommerce.springbootecommerce.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2025-10-20
 * Time: 08:10
 */
public class SalesReportDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String period; // "daily" or "monthly"
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate startDate;
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate endDate;
    
    private List<SalesDataPoint> salesData;

    public SalesReportDto() {
    }

    public SalesReportDto(String period, LocalDate startDate, LocalDate endDate, List<SalesDataPoint> salesData) {
        this.period = period;
        this.startDate = startDate;
        this.endDate = endDate;
        this.salesData = salesData;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<SalesDataPoint> getSalesData() {
        return salesData;
    }

    public void setSalesData(List<SalesDataPoint> salesData) {
        this.salesData = salesData;
    }

    public static class SalesDataPoint implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        @JsonFormat(pattern = "dd/MM/yyyy")
        private LocalDate date;
        
        private Double revenue;
        private Long orderCount;

        public SalesDataPoint() {
        }

        public SalesDataPoint(LocalDate date, Double revenue, Long orderCount) {
            this.date = date;
            this.revenue = revenue;
            this.orderCount = orderCount;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public Double getRevenue() {
            return revenue;
        }

        public void setRevenue(Double revenue) {
            this.revenue = revenue;
        }

        public Long getOrderCount() {
            return orderCount;
        }

        public void setOrderCount(Long orderCount) {
            this.orderCount = orderCount;
        }
    }
}
