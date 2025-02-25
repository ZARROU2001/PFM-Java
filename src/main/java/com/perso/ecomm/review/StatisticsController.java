package com.perso.ecomm.review;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/total-revenue")
    public ResponseEntity<BigDecimal> getTotalRevenue() {
        return ResponseEntity.ok(statisticsService.getTotalRevenue());
    }

    @GetMapping("/total-orders")
    public ResponseEntity<Long> getTotalOrdersCount() {
        return ResponseEntity.ok(statisticsService.getTotalOrdersCount());
    }

    @GetMapping("/total-products-sold")
    public ResponseEntity<Long> getTotalProductsSold() {
        return ResponseEntity.ok(statisticsService.getTotalProductsSold());
    }

    @GetMapping("/top-selling-products")
    public ResponseEntity<List<Map<String, Object>>> getTopSellingProducts() {
        return ResponseEntity.ok(statisticsService.getTopSellingProducts());
    }

    @GetMapping("/revenue-by-category")
    public ResponseEntity<List<Map<String, Object>>> getRevenueByCategory() {
        return ResponseEntity.ok(statisticsService.getRevenueByCategory());
    }

    @GetMapping("/sales-trend")
    public ResponseEntity<List<Map<String, Object>>> getSalesTrend() {
        return ResponseEntity.ok(statisticsService.getSalesTrend());
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<Map<String, Object>>> getLowStockProducts(@RequestParam(defaultValue = "5") int threshold) {
        return ResponseEntity.ok(statisticsService.getLowStockProducts(threshold));
    }


    @GetMapping("/orders-overview")
    public List<Map<String, Object>> getOrdersOverview() {
        return statisticsService.getOrdersOverview();
    }


    @GetMapping("/status-count")
    public Map<String, Long> getOrderStatusCounts() {
        return statisticsService.getOrderStatusCounts();
    }
}

