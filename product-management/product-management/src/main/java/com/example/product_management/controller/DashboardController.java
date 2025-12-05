package com.example.product_management.controller;

import com.example.product_management.entity.Product;
import com.example.product_management.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final ProductService productService;

    @Autowired
    public DashboardController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String showDashboard(Model model) {

        long totalProducts = productService.getTotalProducts();
        BigDecimal totalValue = productService.calculateTotalValue();
        BigDecimal averagePrice = productService.calculateAveragePrice();

        // Category counts
        Map<String, Long> categoryCounts = new LinkedHashMap<>();
        for (String category : productService.getAllCategories()) {
            long count = productService.countByCategory(category);
            categoryCounts.put(category, count);
        }

        int lowStockThreshold = 10;
        List<Product> lowStockProducts = productService.findLowStockProducts(lowStockThreshold);
        List<Product> recentProducts = productService.getRecentProducts(5);

        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalValue", totalValue);
        model.addAttribute("averagePrice", averagePrice);
        model.addAttribute("categoryCounts", categoryCounts);
        model.addAttribute("lowStockThreshold", lowStockThreshold);
        model.addAttribute("lowStockProducts", lowStockProducts);
        model.addAttribute("recentProducts", recentProducts);

        return "dashboard";
    }
}
