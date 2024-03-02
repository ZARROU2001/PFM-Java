package com.perso.ecomm.orders.order;

import com.perso.ecomm.exception.ResourceNotFoundException;
import com.perso.ecomm.orders.orderItem.OrderItem;
import com.perso.ecomm.orders.orderItem.OrderItemService;
import com.perso.ecomm.playLoad.request.OrderRequest;
import com.perso.ecomm.product.Product;
import com.perso.ecomm.user.User;
import com.perso.ecomm.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final UserRepository userRepository;


    public OrderService(OrderRepository orderRepository, OrderItemService orderItemService, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderItemService = orderItemService;
        this.userRepository = userRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long orderId) {

        return orderRepository.findById(orderId).orElseThrow(
                () -> new ResourceNotFoundException("There's no order with id :" + orderId)
        );
    }

    @Transactional
    public Order saveOrder(OrderRequest orderRequest) {
        // Retrieve user from the database
        User user = userRepository.findById(orderRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("There's no user with id : " + orderRequest.getUserId()));

        // Convert product ids and quantities to order items
        List<OrderItem> orderItems = orderItemService.fromProductIdsToListOrderItem(orderRequest.getProductIds(), orderRequest.getQuantities());

        // Calculate total
        double total = orderItems.stream()
                .mapToDouble(orderItem -> orderItem.getProduct().getPrice() * orderItem.getQuantity())
                .sum();

        // Create new order
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotal(total);
        order.setOrderItems(orderItems);

        // Set order for each order item
        orderItems.forEach(orderItem -> orderItem.setOrder(order));

        // Save order items in batch
        orderItemService.saveAllOrderItems(orderItems);

        // Save the order
        orderRepository.save(order);

        return order;
    }


    @Transactional
    public Order changeOrderStatus(Long orderId, String orderStatus) {

        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new ResourceNotFoundException("There's no order with id :" + orderId)
        );
        order.setStatus(OrderStatus.valueOf(orderStatus.toUpperCase()));
        return order;

    }

    public void deleteOrder(Long orderId) {

        orderRepository.findById(orderId).orElseThrow(
                () -> new ResourceNotFoundException("There's no order with id :" + orderId)
        );
        orderRepository.deleteById(orderId);

    }


    public Page<Order> getSortedAndPagedData(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }
}
