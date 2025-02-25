package com.perso.ecomm.playLoad.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Setter
public class ProductRequest {

    @NotBlank(message = "required field")
    private String name;

    @NotBlank(message = "required field")
    private String description;

    private MultipartFile imageUrl;

    @NotBlank
    private String category;

    @DecimalMin(value = "0.01",message = "should be a number")
    private double priceAfterDiscount;

    private double priceBeforeDiscount;

    @Min(value = 0, message = "stock should more than or equal 0")
    private int stockQuantity;

}
