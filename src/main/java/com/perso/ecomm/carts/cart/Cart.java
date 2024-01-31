package com.perso.ecomm.carts.cart;

import com.perso.ecomm.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @ManyToOne
    @JoinColumn(name = "customer_id",foreignKey = @ForeignKey(name = "fk_cart_user"))
    private User user;

}
