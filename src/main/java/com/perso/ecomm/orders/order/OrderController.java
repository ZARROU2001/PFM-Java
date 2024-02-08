package com.perso.ecomm.orders.order;

import com.perso.ecomm.playLoad.request.OrderRequest;
import com.perso.ecomm.playLoad.response.OrderResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<?> changeOrderStatus(@PathVariable Long orderId, String orderStatus) {
            Order order = orderService.changeOrderStatus(orderId, orderStatus);
            return ResponseEntity.ok(order);
    }

    @PostMapping("store")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest order) {
            Order savedOrder = orderService.saveOrder(order);
            return ResponseEntity.ok(savedOrder);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long orderId) {
            orderService.deleteOrder(orderId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Order with id : %d deleted successfully".formatted(orderId));
    }


}
