package com.hendisantika.ecommerce.springbootecommerce.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

/**
 * Shipping Method entity
 */
@Entity
public class ShippingMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name is required")
    private String name;

    @NotNull(message = "Base cost is required")
    private Double baseCost;

    private Double perKmCost;

    private String description;

    private Boolean active;

    public ShippingMethod() {
        this.active = true;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getBaseCost() {
        return baseCost;
    }

    public void setBaseCost(Double baseCost) {
        this.baseCost = baseCost;
    }

    public Double getPerKmCost() {
        return perKmCost;
    }

    public void setPerKmCost(Double perKmCost) {
        this.perKmCost = perKmCost;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Double calculateShippingCost(Double distanceKm) {
        if (distanceKm == null || distanceKm <= 0) {
            return baseCost;
        }
        if (perKmCost == null) {
            return baseCost;
        }
        return baseCost + (perKmCost * distanceKm);
    }
}
