package com.perso.ecomm.orders.order;

import com.perso.ecomm.exception.ResourceNotFoundException;
import com.perso.ecomm.orders.orderItem.OrderItem;
import com.perso.ecomm.orders.orderItem.OrderItemService;
import com.perso.ecomm.playLoad.request.OrderRequest;
import com.perso.ecomm.product.Product;
import com.perso.ecomm.user.User;
import com.perso.ecomm.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
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

        User user = userRepository.findById(orderRequest.getUserId()).orElseThrow(
                () -> new ResourceNotFoundException("There's no user with id : " + orderRequest.getUserId())
        );

        List<OrderItem> orderItems;
        orderItems = orderItemService.fromProductIdsToListOrderItem(
                orderRequest.getProductIds(),
                orderRequest.getQuantities()
        );

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        orderRepository.save(order);

        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(order);
            orderItem.setSubtotal(orderItem.getProduct().getPrice() * orderItem.getQuantity());
            orderItemService.saveOrderItem(orderItem);
        }

        order.setOrderItems(orderItems);

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
