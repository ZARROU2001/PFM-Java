package com.perso.ecomm.orders.order;

import com.perso.ecomm.exception.ResourceNotFoundException;
import com.perso.ecomm.orders.orderItem.OrderItem;
import com.perso.ecomm.orders.orderItem.OrderItemService;
import com.perso.ecomm.playLoad.request.OrderRequest;
import com.perso.ecomm.product.Product;
import com.perso.ecomm.user.User;
import com.perso.ecomm.user.UserRepository;
import com.perso.ecomm.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final UserService userService;


    public OrderService(OrderRepository orderRepository, OrderItemService orderItemService, UserService userService) {
        this.orderRepository = orderRepository;
        this.orderItemService = orderItemService;
        this.userService = userService;
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
        // Retrieve or create user (authenticated or guest)
        User user = userService.getUserForOrder(orderRequest);

        // Convert product ids and quantities to order items
        List<OrderItem> orderItems = orderItemService.fromProductIdsToListOrderItem(
                orderRequest.getProductIds(),
                orderRequest.getQuantities()
        );

        // Calculate total
        double total = calculateTotal(orderItems);

        // Create new order
        Order order = new Order();
        order.setUser(user);  // Associate user (authenticated or guest)
        order.setStatus(OrderStatus.PENDING);
        order.setTotal(total);
        order.setOrderItems(orderItems);

        // Set order for each order item
        orderItems.forEach(orderItem -> orderItem.setOrder(order));

        // Save order items and order
        orderItemService.saveAllOrderItems(orderItems);
        orderRepository.save(order);

        return order;
    }

    private double calculateTotal(List<OrderItem> orderItems) {
        return orderItems.stream()
                .mapToDouble(orderItem -> orderItem.getProduct().getPriceAfterDiscount() * orderItem.getQuantity())
                .sum();
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
