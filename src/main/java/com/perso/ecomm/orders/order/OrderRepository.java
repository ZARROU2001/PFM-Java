package com.perso.ecomm.orders.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT SUM(o.total) FROM Order o WHERE o.status = 'DELIVERED'")
    BigDecimal getTotalRevenue();

    @Query("SELECT COUNT(o) FROM Order o")
    Long getTotalOrdersCount();

    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi")
    Long getTotalProductsSold();

    @Query("SELECT oi.product.name, SUM(oi.quantity) as totalSold " +
            "FROM OrderItem oi GROUP BY oi.product ORDER BY totalSold DESC")
    List<Object[]> getTopSellingProducts();

    @Query("SELECT p.category.categoryName, SUM(oi.subtotal) as revenue " +
            "FROM OrderItem oi JOIN oi.product p GROUP BY p.category.categoryName")
    List<Object[]> getRevenueByCategory();

    @Query("SELECT FUNCTION('DATE', o.orderDate), SUM(o.total) " +
            "FROM Order o GROUP BY FUNCTION('DATE', o.orderDate) ORDER BY o.orderDate")
    List<Object[]> getSalesTrend();


    @Query(value = """
            SELECT
                MONTH(o.order_date) AS month_number,
                MONTHNAME(o.order_date) AS month_name,
                COUNT(o.order_id) AS orders,
                SUM(o.total) AS revenue,
                SUM(CASE WHEN o.status = 'CANCELED' THEN 1 ELSE 0 END) AS refunds
            FROM orders o
            GROUP BY MONTH(o.order_date), MONTHNAME(o.order_date)
            ORDER BY month_number
            """, nativeQuery = true)
    List<Object[]> getOrdersOverview();

    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countOrdersByStatus();

}


