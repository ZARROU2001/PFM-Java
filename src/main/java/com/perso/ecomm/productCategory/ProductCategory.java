package com.perso.ecomm.productCategory;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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

    @Column(nullable = false,unique = true)
    @NotNull(message = "Name cannot be blank")
    private String categoryName;

    public ProductCategory() {

    }
}
