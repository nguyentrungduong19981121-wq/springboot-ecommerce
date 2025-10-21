package com.hendisantika.ecommerce.springbootecommerce.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-ecommerce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 2019-04-26
 * Time: 06:03
 */
@Entity
@Table(name = "orders")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "orderProducts")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "pk.order", cascade = CascadeType.ALL)
    @Valid
    private List<OrderProduct> orderProducts = new ArrayList<>();

    @Transient
    public Double getTotalPrice() {
        double sum = 0D;
        List<OrderProduct> orderProducts = getOrderProducts();
        for (OrderProduct op : orderProducts) {
            sum += op.getTotalPrice();
        }
        return sum;
    }

    @Transient
    public Double getTotalOrderPrice() {
        return getTotalPrice();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    // Legacy getter for backwards compatibility
    public java.time.LocalDate getDateCreated() {
        return createdAt != null ? createdAt.toLocalDate() : null;
    }

    public void setDateCreated(java.time.LocalDate dateCreated) {
        if (dateCreated != null) {
            this.createdAt = dateCreated.atStartOfDay();
        }
    }

    public List<OrderProduct> getOrderProducts() {
        return orderProducts;
    }

    public void setOrderProducts(List<OrderProduct> orderProducts) {
        this.orderProducts = orderProducts;
    }

    @Transient
    public int getNumberOfProducts() {
        return this.orderProducts.size();
    }
}
