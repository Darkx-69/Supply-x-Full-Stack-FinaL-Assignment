package com.supplysync.service;

import com.supplysync.dto.ProductDto;
import com.supplysync.entity.Category;
import com.supplysync.entity.Inventory;
import com.supplysync.entity.Product;
import com.supplysync.enums.ProductStatus;
import com.supplysync.exception.BadRequestException;
import com.supplysync.exception.ResourceNotFoundException;
import com.supplysync.repository.CategoryRepository;
import com.supplysync.repository.InventoryRepository;
import com.supplysync.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts(String query, Long categoryId, ProductStatus status) {
        List<Product> products;
        if ((query != null && !query.isBlank()) || categoryId != null || status != null) {
            products = productRepository.searchProducts(query, categoryId, status);
        } else {
            products = productRepository.findAll();
        }
        return products.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return toDto(product);
    }

    @Transactional
    public ProductDto createProduct(ProductDto dto) {
        if (productRepository.existsBySku(dto.getSku())) {
            throw new BadRequestException("A product with SKU '" + dto.getSku() + "' already exists");
        }

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new BadRequestException("Category not found with id: " + dto.getCategoryId()));

        Product product = new Product();
        product.setName(dto.getName());
        product.setSku(dto.getSku().trim().toUpperCase());
        product.setDescription(dto.getDescription());
        product.setCategory(category);
        product.setUnitPrice(dto.getUnitPrice());
        product.setUnitOfMeasurement(dto.getUnitOfMeasurement());
        product.setStatus(dto.getStatus() != null ? dto.getStatus() : ProductStatus.ACTIVE);

        Product saved = productRepository.save(product);

        // Automatically initialize an inventory entry for this new product if none exists
        if (inventoryRepository.findByProductId(saved.getId()).isEmpty()) {
            Inventory inventory = new Inventory(saved, 0, 10, 1000);
            inventoryRepository.save(inventory);
        }

        return toDto(saved);
    }

    @Transactional
    public ProductDto updateProduct(Long id, ProductDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (!product.getSku().equalsIgnoreCase(dto.getSku()) && productRepository.existsBySku(dto.getSku())) {
            throw new BadRequestException("A product with SKU '" + dto.getSku() + "' already exists");
        }

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new BadRequestException("Category not found with id: " + dto.getCategoryId()));

        product.setName(dto.getName());
        product.setSku(dto.getSku().trim().toUpperCase());
        product.setDescription(dto.getDescription());
        product.setCategory(category);
        product.setUnitPrice(dto.getUnitPrice());
        product.setUnitOfMeasurement(dto.getUnitOfMeasurement());
        if (dto.getStatus() != null) {
            product.setStatus(dto.getStatus());
        }

        Product saved = productRepository.save(product);
        return toDto(saved);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Remove inventory record if exists
        inventoryRepository.findByProductId(id).ifPresent(inventoryRepository::delete);

        productRepository.delete(product);
    }

    public ProductDto toDto(Product p) {
        ProductDto dto = new ProductDto();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setSku(p.getSku());
        dto.setDescription(p.getDescription());
        if (p.getCategory() != null) {
            dto.setCategoryId(p.getCategory().getId());
            dto.setCategoryName(p.getCategory().getName());
        }
        dto.setUnitPrice(p.getUnitPrice());
        dto.setUnitOfMeasurement(p.getUnitOfMeasurement());
        dto.setStatus(p.getStatus());
        dto.setCreatedAt(p.getCreatedAt());

        inventoryRepository.findByProductId(p.getId())
                .ifPresent(inv -> dto.setAvailableStock(inv.getAvailableQuantity()));

        return dto;
    }
}
