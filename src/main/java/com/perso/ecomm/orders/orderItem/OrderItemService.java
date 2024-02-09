package com.perso.ecomm.orders.orderItem;

import com.perso.ecomm.exception.ResourceNotFoundException;
import com.perso.ecomm.product.Product;
import com.perso.ecomm.product.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    private final ProductRepository productRepository;

    public OrderItemService(OrderItemRepository orderItemRepository, ProductRepository productRepository) {
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
    }

    public List<OrderItem> fromProductIdsToListOrderItem(List<Long> productIds, int[] quantities) {

        List<OrderItem> orderItems = new ArrayList<>();

        // Subtotal will be calculated in the OrderService.
        IntStream.range(0, productIds.size()).forEach(i -> {
            Product product = productRepository.findById(productIds.get(i)).orElseThrow(() -> new ResourceNotFoundException("Product with id : %d not found".formatted(productIds.get(i))));
            OrderItem orderItem = new OrderItem();
            product.setStockQuantity(product.getStockQuantity() - 1 );
            orderItem.setProduct(product);
            orderItem.setQuantity(quantities[i]);
            orderItems.add(orderItem);
        });

        return orderItems;

    }

    @Transactional
    public void saveOrderItem(OrderItem orderItem){

        orderItemRepository.save(orderItem);

    }

}
