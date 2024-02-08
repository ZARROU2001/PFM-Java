package com.perso.ecomm.productCategory;

import com.perso.ecomm.exception.ResourceNotFoundException;
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
        boolean isExist = productCategoryRepository.existsProductCategoryByCategoryName(productCategory.getCategoryName());
        if (isExist){
           throw new ResourceNotFoundException("Product Category Name already exist : " + productCategory.getCategoryName());
        }
        return productCategoryRepository.save(productCategory);
    }

    public void deleteCategory(Long categoryId) {

        ProductCategory category = productCategoryRepository.findProductCategoriesByCategoryId(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Product category with ID " + categoryId + " not found"));

        productCategoryRepository.delete(category);
    }

    @Transactional
    public ProductCategory updateCategory(Long categoryId, ProductCategory updatedCategory) {
        ProductCategory productCategory = productCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Product category with ID " + categoryId + " not found"));
        boolean isAlreadyExist = productCategoryRepository.existsProductCategoryByCategoryName(updatedCategory.getCategoryName());
        if (isAlreadyExist){
            throw new ResourceNotFoundException("Product Category Name already exist : " + productCategory.getCategoryName());
        }
        productCategory.setCategoryName(updatedCategory.getCategoryName());
        return productCategory;
    }

    public Page<ProductCategory> getSortedAndPagedData(Pageable pageable) {
        return productCategoryRepository.findAll(pageable);
    }
}
