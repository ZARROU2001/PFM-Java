package com.perso.ecomm.review;

import com.perso.ecomm.orders.order.OrderRepository;
import com.perso.ecomm.product.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public StatisticsService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public BigDecimal getTotalRevenue() {
        return orderRepository.getTotalRevenue();
    }

    public Long getTotalOrdersCount() {
        return orderRepository.getTotalOrdersCount();
    }

    public Long getTotalProductsSold() {
        return orderRepository.getTotalProductsSold();
    }

    public List<Map<String, Object>> getTopSellingProducts() {
        return orderRepository.getTopSellingProducts().stream()
                .map(obj -> Map.of("productName", obj[0], "totalSold", obj[1]))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getRevenueByCategory() {
        return orderRepository.getRevenueByCategory().stream()
                .map(obj -> Map.of("categoryName", obj[0], "revenue", obj[1]))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getSalesTrend() {
        return orderRepository.getSalesTrend().stream()
                .map(obj -> Map.of("date", obj[0], "totalSales", obj[1]))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getLowStockProducts(int threshold) {
        return productRepository.getLowStockProducts(threshold).stream()
                .map(obj -> Map.of("productName", obj[0], "stock", obj[1]))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getOrdersOverview() {
        List<Object[]> results = orderRepository.getOrdersOverview();

        return results.stream().map(row -> Map.of(
                "month", row[1],
                "orders", row[2],
                "revenue", row[3],
                "canceled", row[4]
        )).collect(Collectors.toList());
    }


    public Map<String, Long> getOrderStatusCounts() {
        List<Object[]> results = orderRepository.countOrdersByStatus();
        Map<String, Long> statusCounts = new HashMap<>();

        for (Object[] result : results) {
            statusCounts.put(result[0].toString(), (Long) result[1]);
        }

        return statusCounts;
    }

}
