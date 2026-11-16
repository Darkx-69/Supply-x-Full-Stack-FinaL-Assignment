package com.supplysync.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "procurement_request_items")
public class ProcurementRequestItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procurement_request_id", nullable = false)
    private ProcurementRequest procurementRequest;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer quantity;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "estimated_unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal estimatedUnitPrice;

    @NotNull
    @DecimalMin("0.00")
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotal;

    public ProcurementRequestItem() {}

    public ProcurementRequestItem(ProcurementRequest procurementRequest, Product product, Integer quantity, BigDecimal estimatedUnitPrice) {
        this.procurementRequest = procurementRequest;
        this.product = product;
        this.quantity = quantity;
        this.estimatedUnitPrice = estimatedUnitPrice;
        this.subtotal = estimatedUnitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ProcurementRequest getProcurementRequest() { return procurementRequest; }
    public void setProcurementRequest(ProcurementRequest procurementRequest) { this.procurementRequest = procurementRequest; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getEstimatedUnitPrice() { return estimatedUnitPrice; }
    public void setEstimatedUnitPrice(BigDecimal estimatedUnitPrice) { this.estimatedUnitPrice = estimatedUnitPrice; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}
