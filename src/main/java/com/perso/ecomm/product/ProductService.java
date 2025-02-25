package com.perso.ecomm.product;

import com.perso.ecomm.exception.ResourceNotFoundException;
import com.perso.ecomm.playLoad.request.ProductRequest;
import com.perso.ecomm.productCategory.ProductCategory;
import com.perso.ecomm.productCategory.ProductCategoryRepository;
import com.perso.ecomm.util.ImageStorageService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ProductService {
    final String FOLDER_PATH = "src/main/resources/static/images/";
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ImageStorageService imageStorageService;

    public ProductService(ProductRepository productRepository, ProductCategoryRepository productCategoryRepository, ImageStorageService imageStorageService) {
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.imageStorageService = imageStorageService;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        ProductCategory category = productCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return productRepository.findByCategory(category);
    }

    public Page<Product> getSortedAndPagedProductsByCategory(Long categoryId, Pageable pageable) {
        ProductCategory productCategory = productCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return productRepository.findAllByCategory(productCategory, pageable);
    }

    public Page<Product> getSortedAndPagedData(Pageable pageable) {
        return productRepository.findAll(pageable);
    }


    // get latest products
    public List<Product> getLatestProducts() {
        Pageable pageable = PageRequest.of(0, 10);  // Fetching the latest 10 products
        return productRepository.findLatestByCreationDateDesc(pageable);
    }

    //get hot deals
    public List<Product> getHotDealsProducts() {
        return productRepository.findByDiscountPercentGreaterThan(45);
    }

    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID + categoryId + not found"));
        productRepository.delete(product);

    }

    public Product registerNewProduct(ProductRequest productRequest) throws IOException {
        ProductCategory productCategory = productCategoryRepository.findProductCategoriesByCategoryName(productRequest.getCategory())
                .orElseThrow(() -> new ResourceNotFoundException("Category not Found"));

        double discountPercent = calculateDiscountPercent(productRequest.getPriceBeforeDiscount(), productRequest.getPriceAfterDiscount());

        String imageUrl = (productRequest.getImageUrl() != null && !productRequest.getImageUrl().isEmpty())
                ? imageStorageService.saveImage(productRequest.getName(), productRequest.getImageUrl())
                : null;

        Product product = new Product(
                productCategory,
                productRequest.getName(),
                productRequest.getDescription(),
                productRequest.getPriceAfterDiscount(),
                productRequest.getPriceBeforeDiscount(),
                productRequest.getStockQuantity(),
                imageUrl
        );

        product.setDiscountPercent(discountPercent);
        productRepository.save(product);

        return product;
    }

    @Transactional
    public Product updateProduct(Long productId, ProductRequest productRequest) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + productId + " doesn't exist"));

        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPriceAfterDiscount(productRequest.getPriceAfterDiscount());
        product.setPriceBeforeDiscount(productRequest.getPriceBeforeDiscount());
        product.setStockQuantity(productRequest.getStockQuantity());

        if (productRequest.getImageUrl() != null && !productRequest.getImageUrl().isEmpty()) {
            String imageUrl = imageStorageService.saveImage(productRequest.getName(), productRequest.getImageUrl());
            product.setImageUrl(imageUrl);
        }

        return product;
    }

    public byte[] getImage(Long id) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));

        return imageStorageService.loadImage(product.getImageUrl());
    }


    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product with id " + productId + " not found"));
    }

    public double calculateDiscountPercent(double priceBeforeDiscount, double priceAfterDiscount) {
        if (priceBeforeDiscount > 0 && priceBeforeDiscount <= priceAfterDiscount) {
            throw new IllegalArgumentException("Price before discount must be greater than Price After discount");
        }
        double discount = ((priceBeforeDiscount - priceAfterDiscount) / priceBeforeDiscount) * 100;
        return (int) Math.round(discount);
    }

}
