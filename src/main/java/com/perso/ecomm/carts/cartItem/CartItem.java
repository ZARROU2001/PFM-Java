package com.perso.ecomm.carts.cartItem;

import com.perso.ecomm.carts.cart.Cart;
import com.perso.ecomm.product.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;

    @ManyToOne
    @JoinColumn(name = "cart_id",foreignKey = @ForeignKey(name = "fk_cartItem_cart"))
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id",foreignKey = @ForeignKey(name = "fk_cartItem_product"))
    private Product product;

    @Min(1)
    private int quantity;


}
