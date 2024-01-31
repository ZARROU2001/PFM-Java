package com.perso.ecomm.playLoad.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ReviewRequest {

    @NotEmpty
    Long productId;

    @NotEmpty
    Long userId;

    @Size(min = 1, max = 5)
    int rating;

    String comment;

}
