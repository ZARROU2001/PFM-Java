package com.perso.ecomm.orders.orderItem;

import com.perso.ecomm.exception.InsufficientStockException;
import com.perso.ecomm.exception.ResourceNotFoundException;
import com.perso.ecomm.product.Product;
import com.perso.ecomm.product.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    private final ProductRepository productRepository;

    public OrderItemService(OrderItemRepository orderItemRepository, ProductRepository productRepository) {
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
    }

    public List<OrderItem> fromProductIdsToListOrderItem(List<Long> productIds, int[] quantities) {
        // Batch fetch products
        List<Product> products = productRepository.findAllById(productIds);

        if (products.size() != productIds.size()) {
            throw new ResourceNotFoundException("One or more products not found.");
        }

        List<OrderItem> orderItems = new ArrayList<>();

        // Populate order items
        for (int i = 0; i < products.size(); i++) {
            OrderItem orderItem = getItem(quantities, products, i);
            orderItems.add(orderItem);
        }

        return orderItems;
    }

    private static OrderItem getItem(int[] quantities, List<Product> products, int i) {
        Product product = products.get(i);
        int quantity = quantities[i];

        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException("Insufficient stock for product with id: " + product.getProductId());
        }

        OrderItem orderItem = new OrderItem();
        product.setStockQuantity(product.getStockQuantity() - quantity);
        orderItem.setProduct(product);
        orderItem.setQuantity(quantity);
        orderItem.setSubtotal(product.getPrice() * quantity);
        return orderItem;
    }

    @Transactional
    public void saveOrderItem(OrderItem orderItem) {

        orderItemRepository.save(orderItem);

    }

    @Transactional
    public List<OrderItem> saveAllOrderItems(List<OrderItem> orderItems) {
        return orderItemRepository.saveAll(orderItems);
    }
}
