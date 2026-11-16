package com.supplysync.service;

import com.supplysync.dto.CategoryDto;
import com.supplysync.dto.ProductDto;
import com.supplysync.entity.Category;
import com.supplysync.entity.Product;
import com.supplysync.exception.BadRequestException;
import com.supplysync.exception.ResourceNotFoundException;
import com.supplysync.repository.CategoryRepository;
import com.supplysync.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(cat -> {
                    List<Product> products = productRepository.findByCategoryId(cat.getId());
                    CategoryDto dto = new CategoryDto(cat.getId(), cat.getName(), cat.getDescription(), (long) products.size());
                    dto.setCreatedAt(cat.getCreatedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long id) {
        Category cat = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        List<Product> products = productRepository.findByCategoryId(cat.getId());
        CategoryDto dto = new CategoryDto(cat.getId(), cat.getName(), cat.getDescription(), (long) products.size());
        dto.setCreatedAt(cat.getCreatedAt());
        return dto;
    }

    @Transactional
    public CategoryDto createCategory(CategoryDto dto) {
        if (categoryRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new BadRequestException("Category with name '" + dto.getName() + "' already exists");
        }
        Category category = new Category(dto.getName(), dto.getDescription());
        Category saved = categoryRepository.save(category);
        return new CategoryDto(saved.getId(), saved.getName(), saved.getDescription(), 0L);
    }

    @Transactional
    public CategoryDto updateCategory(Long id, CategoryDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (!category.getName().equalsIgnoreCase(dto.getName()) && categoryRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new BadRequestException("Category with name '" + dto.getName() + "' already exists");
        }

        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        Category saved = categoryRepository.save(category);
        List<Product> products = productRepository.findByCategoryId(saved.getId());
        return new CategoryDto(saved.getId(), saved.getName(), saved.getDescription(), (long) products.size());
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        List<Product> products = productRepository.findByCategoryId(id);
        if (!products.isEmpty()) {
            throw new BadRequestException("Cannot delete category with " + products.size() + " associated products. Reassign or delete products first.");
        }
        categoryRepository.delete(category);
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getProductsByCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }
        return productRepository.findByCategoryId(categoryId).stream()
                .map(p -> {
                    ProductDto dto = new ProductDto();
                    dto.setId(p.getId());
                    dto.setName(p.getName());
                    dto.setSku(p.getSku());
                    dto.setDescription(p.getDescription());
                    dto.setCategoryId(p.getCategory().getId());
                    dto.setCategoryName(p.getCategory().getName());
                    dto.setUnitPrice(p.getUnitPrice());
                    dto.setUnitOfMeasurement(p.getUnitOfMeasurement());
                    dto.setStatus(p.getStatus());
                    dto.setCreatedAt(p.getCreatedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
