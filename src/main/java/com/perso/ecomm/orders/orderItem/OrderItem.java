package com.perso.ecomm.orders.orderItem;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.perso.ecomm.orders.order.Order;
import com.perso.ecomm.product.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "order_item")
@EqualsAndHashCode
@ToString
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id",foreignKey = @ForeignKey(name = "fk_orderItem_order"))
    @JsonIgnore
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id",foreignKey = @ForeignKey(name = "fk_orderItem_product"))
    private Product product;

    @Min(1)
    private int quantity;

    private double subtotal;

    public OrderItem() {
    }
}
