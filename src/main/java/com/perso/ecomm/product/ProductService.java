package com.perso.ecomm.product;

import com.perso.ecomm.playLoad.request.ProductRequest;
import com.perso.ecomm.productCategory.ProductCategory;
import com.perso.ecomm.productCategory.ProductCategoryRepository;
import com.perso.ecomm.util.FileUploadUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Service
public class ProductService {
    final String FOLDER_PATH = "src/main/resources/static/images/";
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;

    public ProductService(ProductRepository productRepository, ProductCategoryRepository productCategoryRepository) {
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Page<Product> getSortedAndPagedData(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product with ID + categoryId + not found"));
        productRepository.delete(product);

    }

    public Product registerNewProduct(ProductRequest productRequest) throws IOException {

        ProductCategory productCategory = productCategoryRepository.findProductCategoriesByCategoryName(productRequest.getCategory())
                .orElseThrow(() -> new EntityNotFoundException("Category not Found"));

        FileUploadUtil.saveFile(FOLDER_PATH, productRequest.getImageUrl().getOriginalFilename(), productRequest.getImageUrl());

        Product product = new Product(
                productCategory,
                productRequest.getName(),
                productRequest.getDescription(),
                productRequest.getPrice(),
                productRequest.getStockQuantity(),
                "http://localhost:8080/images/" + productRequest.getImageUrl().getOriginalFilename()
        );

        productRepository.save(product);

        return product;
    }

    @Transactional
    public void updateProduct(Long productId, ProductRequest productRequest) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException(
                        " product with id " + productId + " doesn't exist "));

        ProductCategory productCategory = productCategoryRepository.findProductCategoriesByCategoryName(productRequest.getCategory())
                .orElseThrow(() -> new EntityNotFoundException("Category not Found"));


        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setStockQuantity(productRequest.getStockQuantity());
        product.setCategory(productCategory);
        FileUploadUtil.saveFile(FOLDER_PATH, productRequest.getImageUrl().getOriginalFilename(), productRequest.getImageUrl());
        product.setImageUrl("http://localhost:8080/images/" + productRequest.getImageUrl().getOriginalFilename());
    }

    public byte[] getImage(Long id) throws IOException {
        Product product = productRepository.findById(id).get();
        String filePath = FOLDER_PATH + product.getImageUrl();

        return Files.readAllBytes(new File(filePath).toPath());
    }


}
