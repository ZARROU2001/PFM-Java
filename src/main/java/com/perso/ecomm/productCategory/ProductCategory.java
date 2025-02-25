package com.perso.ecomm.productCategory;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.perso.ecomm.product.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

import java.util.List;

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(name = "UniqueCategory",columnNames = "categoryName")
})
@Getter
@Setter
@AllArgsConstructor
@ToString
public class ProductCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<Product> product;

    @Column(nullable = false,unique = true)
    @NotNull(message = "Name cannot be blank")
    private String categoryName;

    public ProductCategory() {

    }

    public ProductCategory(String categoryName) {
        this.categoryName = categoryName;
    }
}
