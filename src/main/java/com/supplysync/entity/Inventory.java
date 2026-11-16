package com.supplysync.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @NotNull
    @Min(0)
    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity = 0;

    @NotNull
    @Min(0)
    @Column(name = "minimum_stock_level", nullable = false)
    private Integer minimumStockLevel = 10;

    @NotNull
    @Min(1)
    @Column(name = "maximum_stock_level", nullable = false)
    private Integer maximumStockLevel = 1000;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    public Inventory() {}

    public Inventory(Product product, Integer availableQuantity, Integer minimumStockLevel, Integer maximumStockLevel) {
        this.product = product;
        this.availableQuantity = availableQuantity;
        this.minimumStockLevel = minimumStockLevel;
        this.maximumStockLevel = maximumStockLevel;
        this.lastUpdated = LocalDateTime.now();
    }

    @PrePersist
    @PreUpdate
    protected void onPersist() {
        this.lastUpdated = LocalDateTime.now();
    }

    public boolean isLowStock() {
        return availableQuantity != null && minimumStockLevel != null && availableQuantity <= minimumStockLevel;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(Integer availableQuantity) { this.availableQuantity = availableQuantity; }

    public Integer getMinimumStockLevel() { return minimumStockLevel; }
    public void setMinimumStockLevel(Integer minimumStockLevel) { this.minimumStockLevel = minimumStockLevel; }

    public Integer getMaximumStockLevel() { return maximumStockLevel; }
    public void setMaximumStockLevel(Integer maximumStockLevel) { this.maximumStockLevel = maximumStockLevel; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
