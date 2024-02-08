package com.perso.ecomm.productCategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory,Long> {
    Optional<ProductCategory> findProductCategoriesByCategoryName(String categoryName);

    boolean existsProductCategoryByCategoryName(String categoryName);

    Optional<ProductCategory> findProductCategoriesByCategoryId(Long id);
}
