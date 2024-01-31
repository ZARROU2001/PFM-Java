package com.perso.ecomm.productCategory;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductCategoryService {
    private final ProductCategoryRepository productCategoryRepository;

    public ProductCategoryService(ProductCategoryRepository productCategoryRepository) {
        this.productCategoryRepository = productCategoryRepository;
    }

    public List<ProductCategory> getAllCategories() {
        return productCategoryRepository.findAll();
    }


    public ProductCategory addNewCategory(ProductCategory productCategory) {
        productCategoryRepository.findProductCategoriesByCategoryName(productCategory.getCategoryName()).orElseThrow(
                () -> new EntityNotFoundException("Product Category Name already exist : " + productCategory.getCategoryName())
        );
        return productCategoryRepository.save(productCategory);
    }

    public void deleteCategory(Long categoryId) {

        ProductCategory category = productCategoryRepository.findProductCategoriesByCategoryId(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Product category with ID " + categoryId + " not found"));

        productCategoryRepository.delete(category);
    }

    @Transactional
    public void updateCategory(Long categoryId, ProductCategory updatedCategory) {
        ProductCategory productCategory = productCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Product category with ID " + categoryId + " not found"));
        productCategoryRepository.findProductCategoriesByCategoryName(productCategory.getCategoryName()).orElseThrow(
                () -> new EntityNotFoundException("Product Category Name already exist : " + productCategory.getCategoryName())
        );
        productCategory.setCategoryName(updatedCategory.getCategoryName());
    }

    public Page<ProductCategory> getSortedAndPagedData(Pageable pageable) {
        return productCategoryRepository.findAll(pageable);
    }
}
