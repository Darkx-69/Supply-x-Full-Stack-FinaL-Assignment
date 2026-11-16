package com.supplysync.service;

import com.supplysync.dto.InventoryDto;
import com.supplysync.dto.StockAdjustmentRequest;
import com.supplysync.dto.StockTransactionDto;
import com.supplysync.entity.Inventory;
import com.supplysync.entity.Product;
import com.supplysync.entity.StockTransaction;
import com.supplysync.enums.TransactionType;
import com.supplysync.exception.BadRequestException;
import com.supplysync.exception.ResourceNotFoundException;
import com.supplysync.repository.InventoryRepository;
import com.supplysync.repository.ProductRepository;
import com.supplysync.repository.StockTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final StockTransactionRepository transactionRepository;

    public InventoryService(InventoryRepository inventoryRepository,
                            ProductRepository productRepository,
                            StockTransactionRepository transactionRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public List<InventoryDto> getAllInventory(String search, Long categoryId, Boolean lowStockOnly) {
        List<Inventory> list;
        boolean filterLow = Boolean.TRUE.equals(lowStockOnly);
        if ((search != null && !search.isBlank()) || categoryId != null || filterLow) {
            list = inventoryRepository.searchInventory(search, categoryId, filterLow);
        } else {
            list = inventoryRepository.findAll();
        }
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InventoryDto getInventoryById(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory record not found with id: " + id));
        return toDto(inventory);
    }

    @Transactional(readOnly = true)
    public InventoryDto getInventoryByProductId(Long productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product id: " + productId));
        return toDto(inventory);
    }

    @Transactional(readOnly = true)
    public List<InventoryDto> getLowStockItems() {
        return inventoryRepository.findLowStockItems().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public InventoryDto adjustStock(StockAdjustmentRequest request, String username) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        Inventory inventory = inventoryRepository.findByProductId(product.getId())
                .orElseGet(() -> new Inventory(product, 0, 10, 1000));

        int prevQty = inventory.getAvailableQuantity();
        int change = request.getQuantity();
        int newQty;

        TransactionType type = request.getTransactionType();
        if (type == TransactionType.STOCK_ADDED || type == TransactionType.PURCHASE_RECEIVED) {
            newQty = prevQty + change;
        } else if (type == TransactionType.STOCK_REMOVED) {
            if (prevQty < change) {
                throw new BadRequestException("Insufficient stock. Available: " + prevQty + ", Requested reduction: " + change);
            }
            newQty = prevQty - change;
        } else if (type == TransactionType.MANUAL_ADJUSTMENT) {
            // In manual adjustment, quantity represents the new absolute stock count or delta
            newQty = change;
        } else {
            throw new BadRequestException("Unsupported transaction type: " + type);
        }

        inventory.setAvailableQuantity(newQty);
        inventory.setLastUpdated(LocalDateTime.now());
        Inventory saved = inventoryRepository.save(inventory);

        // Record stock transaction history
        StockTransaction transaction = new StockTransaction(
                product,
                type,
                type == TransactionType.STOCK_REMOVED ? -change : change,
                prevQty,
                newQty,
                request.getReferenceOrder() != null ? request.getReferenceOrder() : "MANUAL-ADJ",
                request.getDescription() != null ? request.getDescription() : "Inventory adjustment by " + username,
                username
        );
        transactionRepository.save(transaction);

        return toDto(saved);
    }

    @Transactional
    public InventoryDto updateThresholds(Long id, Integer minLevel, Integer maxLevel) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory record not found with id: " + id));

        if (minLevel != null) {
            if (minLevel < 0) throw new BadRequestException("Minimum stock level cannot be negative");
            inventory.setMinimumStockLevel(minLevel);
        }
        if (maxLevel != null) {
            if (maxLevel < 1) throw new BadRequestException("Maximum stock level must be at least 1");
            inventory.setMaximumStockLevel(maxLevel);
        }
        inventory.setLastUpdated(LocalDateTime.now());
        Inventory saved = inventoryRepository.save(inventory);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<StockTransactionDto> getTransactions(String search, TransactionType type) {
        List<StockTransaction> list;
        if ((search != null && !search.isBlank()) || type != null) {
            list = transactionRepository.searchTransactions(search, type);
        } else {
            list = transactionRepository.findAll();
        }
        return list.stream().map(this::toTransactionDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StockTransactionDto> getProductTransactions(Long productId) {
        return transactionRepository.findByProductIdOrderByTransactionDateDesc(productId).stream()
                .map(this::toTransactionDto)
                .collect(Collectors.toList());
    }

    public InventoryDto toDto(Inventory inv) {
        InventoryDto dto = new InventoryDto();
        dto.setId(inv.getId());
        dto.setProductId(inv.getProduct().getId());
        dto.setProductName(inv.getProduct().getName());
        dto.setSku(inv.getProduct().getSku());
        if (inv.getProduct().getCategory() != null) {
            dto.setCategoryName(inv.getProduct().getCategory().getName());
        }
        dto.setUnitPrice(inv.getProduct().getUnitPrice());
        dto.setUnitOfMeasurement(inv.getProduct().getUnitOfMeasurement());
        dto.setProductStatus(inv.getProduct().getStatus());
        dto.setAvailableQuantity(inv.getAvailableQuantity());
        dto.setMinimumStockLevel(inv.getMinimumStockLevel());
        dto.setMaximumStockLevel(inv.getMaximumStockLevel());
        dto.setLowStock(inv.isLowStock());
        if (inv.getProduct().getUnitPrice() != null && inv.getAvailableQuantity() != null) {
            dto.setTotalValue(inv.getProduct().getUnitPrice().multiply(BigDecimal.valueOf(inv.getAvailableQuantity())));
        } else {
            dto.setTotalValue(BigDecimal.ZERO);
        }
        dto.setLastUpdated(inv.getLastUpdated());
        return dto;
    }

    public StockTransactionDto toTransactionDto(StockTransaction t) {
        StockTransactionDto dto = new StockTransactionDto();
        dto.setId(t.getId());
        dto.setProductId(t.getProduct().getId());
        dto.setProductName(t.getProduct().getName());
        dto.setProductSku(t.getProduct().getSku());
        dto.setTransactionType(t.getTransactionType());
        dto.setQuantity(t.getQuantity());
        dto.setPreviousQuantity(t.getPreviousQuantity());
        dto.setNewQuantity(t.getNewQuantity());
        dto.setTransactionDate(t.getTransactionDate());
        dto.setReferenceOrder(t.getReferenceOrder());
        dto.setDescription(t.getDescription());
        dto.setCreatedBy(t.getCreatedBy());
        return dto;
    }
}
