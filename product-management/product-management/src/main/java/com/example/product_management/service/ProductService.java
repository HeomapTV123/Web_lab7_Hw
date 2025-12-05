package com.example.product_management.service;

import com.example.product_management.entity.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface ProductService {

    // ================== BASIC CRUD ==================
    List<Product> getAllProducts();

    List<Product> getAllProducts(Sort sort);

    Optional<Product> getProductById(Long id);

    Product saveProduct(Product product);

    void deleteProduct(Long id);

    // ================== CATEGORY FILTER ==================
    List<Product> getProductsByCategory(String category);

    List<Product> getProductsByCategory(String category, Sort sort);

    List<String> getAllCategories();

    // ================== PAGINATED SEARCH ==================
    Page<Product> searchProducts(String keyword, Pageable pageable);

    Page<Product> advancedSearch(
            String name,
            String category,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable);

    // === EXERCISE 8 SERVICE METHODS ===

    long countByCategory(String category);

    BigDecimal calculateTotalValue();

    BigDecimal calculateAveragePrice();

    List<Product> findLowStockProducts(int threshold);

    long getTotalProducts();

    List<Product> getRecentProducts(int limit);
}
