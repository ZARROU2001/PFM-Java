package com.perso.ecomm.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.perso.ecomm.productCategory.ProductCategory;
import jakarta.persistence.*;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.*;


import java.util.Date;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@ToString
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @ManyToOne
    @JoinColumn(name = "category_id",foreignKey = @ForeignKey(name = "fk_product_category"))
    @JsonIgnore
    private ProductCategory category;

    @Size(min = 2, message = "at least 2 character")
    private String name;

    @Size(min = 2, message = "at least 2 character")
    private String description;

    @DecimalMin(value = "0.01",message = "should be a number")
    private double priceAfterDiscount;

    private double priceBeforeDiscount;

    private double discountPercent;

    @Min(value = 0, message = "stock should more than or equal 0")
    private int stockQuantity;

    //@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private String imageUrl;

    @CreationTimestamp
    private Date creationDate;

    @UpdateTimestamp
    private Date lastUpdate;

    public Product() {

    }

    public Product(ProductCategory category, String name, String description, double priceAfterDiscount , double priceBeforeDiscount, int stockQuantity, String image) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.priceAfterDiscount = priceAfterDiscount;
        this.priceBeforeDiscount=priceBeforeDiscount;
        this.stockQuantity = stockQuantity;
        this.imageUrl = image;
    }


}
