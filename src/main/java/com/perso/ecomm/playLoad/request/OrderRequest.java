package com.perso.ecomm.playLoad.request;

import lombok.Getter;

import java.util.List;

@Getter
public class OrderRequest {

    private Long userId;  // Nullable for guest orders
    private String guestName;  // Required if guest
    private String guestEmail; // Optional if guest
    private List<Long> productIds;
    private List<Integer> quantities;

    // Getters and Setters
}
