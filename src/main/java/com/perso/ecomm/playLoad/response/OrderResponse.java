package com.perso.ecomm.playLoad.response;

import com.perso.ecomm.product.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private List<Product> products;
}
