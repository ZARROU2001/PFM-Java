package com.perso.ecomm.orders.order;

import com.perso.ecomm.playLoad.request.OrderRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/paginate")
    public Page<Order> paginateOrder(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortField", defaultValue = "orderId") String sortField,
            @RequestParam(name = "sortOrder", defaultValue = "asc") String sortOrder) {
        Sort.Direction direction = sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, direction, sortField);
        return orderService.getSortedAndPagedData(pageable);
    }


    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
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
