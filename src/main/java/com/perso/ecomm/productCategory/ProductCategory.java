package com.perso.ecomm.productCategory;


import com.perso.ecomm.product.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.Set;

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
    private List<Product> product;

    @Column(nullable = false,unique = true)
    @NotNull(message = "Name cannot be blank")
    private String categoryName;

    public ProductCategory() {

    }
}
