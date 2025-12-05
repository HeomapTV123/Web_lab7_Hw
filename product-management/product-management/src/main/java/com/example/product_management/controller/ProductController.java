package com.example.product_management.controller;

import com.example.product_management.entity.Product;
import com.example.product_management.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * LIST PAGE (SORT + FILTER)
     */
    @GetMapping
    public String listProducts(
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String category,
            Model model) {

        List<Product> products;

        boolean hasSorting = (sortBy != null && !sortBy.trim().isEmpty());

        if (hasSorting) {
            Sort sort = sortDir.equals("asc")
                    ? Sort.by(sortBy).ascending()
                    : Sort.by(sortBy).descending();

            if (category != null && !category.isEmpty()) {
                products = productService.getProductsByCategory(category, sort);
            } else {
                products = productService.getAllProducts(sort);
            }

        } else {
            if (category != null && !category.isEmpty()) {
                products = productService.getProductsByCategory(category);
            } else {
                products = productService.getAllProducts();
            }
        }

        model.addAttribute("products", products);
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("category", category);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);

        return "product-list";
    }

    /** CREATE NEW FORM **/
    @GetMapping("/new")
    public String showNewForm(Model model) {
        model.addAttribute("product", new Product());
        return "product-form";
    }

    /** EDIT FORM **/
    @GetMapping("/edit/{id}")
    public String showEditForm(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        return productService.getProductById(id)
                .map(product -> {
                    model.addAttribute("product", product);
                    return "product-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Product not found");
                    return "redirect:/products";
                });
    }

    /** SAVE PRODUCT (VALIDATION ENABLED) **/
    @PostMapping("/save")
    public String saveProduct(
            @Valid @ModelAttribute("product") Product product,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "product-form";
        }

        try {
            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute(
                    "message",
                    product.getId() == null ? "Product added!" : "Product updated!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/products";
    }

    /** DELETE PRODUCT **/
    @GetMapping("/delete/{id}")
    public String deleteProduct(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("message", "Product deleted!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting product.");
        }

        return "redirect:/products";
    }

    /** PAGINATED SEARCH **/
    @GetMapping("/search")
    public String searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage;

        boolean hasKeyword = (keyword != null && !keyword.trim().isEmpty());

        if (hasKeyword) {
            productPage = productService.searchProducts(keyword, pageable);
            model.addAttribute("keyword", keyword);

        } else if (name != null || category != null || minPrice != null || maxPrice != null) {
            productPage = productService.advancedSearch(name, category, minPrice, maxPrice, pageable);

            model.addAttribute("name", name);
            model.addAttribute("category", category);
            model.addAttribute("minPrice", minPrice);
            model.addAttribute("maxPrice", maxPrice);

        } else {
            return "redirect:/products";
        }

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("size", size);

        return "product-list";
    }
}
