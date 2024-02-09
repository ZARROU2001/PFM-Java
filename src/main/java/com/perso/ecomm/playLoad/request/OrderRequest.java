package com.perso.ecomm.playLoad.request;

import lombok.Getter;

import java.util.List;

@Getter
public class OrderRequest {

    private Long userId;

    private List<Long> productIds;

    private int[] quantities;

}
