package com.perso.ecomm.product;

import com.perso.ecomm.productCategory.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface  ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory(ProductCategory category);
    Page<Product> findAllByCategory(ProductCategory productCategory, Pageable pageable);

    @Query("SELECT p FROM Product p ORDER BY p.creationDate DESC")
    List<Product> findLatestByCreationDateDesc(Pageable pageable);

    List<Product> findByDiscountPercentGreaterThan(double discountPercent);


//    @Query("SELECT p FROM Product p ORDER BY p.discountPercent DESC")
//    List<Product> findHotDealsByDiscountPercent(Pageable pageable);
}
