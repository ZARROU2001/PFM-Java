package com.perso.ecomm.product;


import com.perso.ecomm.playLoad.request.ProductRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getProducts() {
        return productService.getAllProducts();
    }

    // get products by category
    @GetMapping("/{categoryId}/products")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable Long categoryId) {
        List<Product> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(products);
    }

    // get sortable, pageable products by category
    @GetMapping("/{categoryId}/paginate")
    public ResponseEntity<Page<Product>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortField", defaultValue = "productId") String sortField,
            @RequestParam(name = "sortOrder", defaultValue = "asc") String sortOrder) {

        Sort.Direction direction = sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, direction, sortField);
        Page<Product> products = productService.getSortedAndPagedProductsByCategory(categoryId, pageable);
        return ResponseEntity.ok(products);
    }

    // get all sortable, pageable products
    @GetMapping("/paginate")
    public Page<Product> paginateProduct(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortField", defaultValue = "productId") String sortField,
            @RequestParam(name = "sortOrder", defaultValue = "asc") String sortOrder) {
        Sort.Direction direction = sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, direction, sortField);
        return productService.getSortedAndPagedData(pageable);
    }

    // get product by its id
    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable("productId") Long productId) {
        try {
            Product product = productService.getProductById(productId);
            return ResponseEntity.ok(product);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
    }

    // Get latest products
    @GetMapping("/latest-products")
    public ResponseEntity<List<Product>> getLatestProducts() {
        List<Product> products = productService.getLatestProducts();
        return ResponseEntity.ok(products);
    }

    //Get Hot Deals
    @GetMapping("/hot-deals")
    public ResponseEntity<List<Product>> getHotDeals() {
        List<Product> products = productService.getHotDealsProducts();
        return ResponseEntity.ok(products);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(path = "/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable("productId") @Valid Long productId) {
            productService.deleteProduct(productId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Product deleted successfully");
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<?> createNewProduct(@Valid ProductRequest productRequest, BindingResult result) throws IOException {

        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
            Product a = productService.registerNewProduct(productRequest);
            return ResponseEntity.ok(a);

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(path = "/{productId}")
    public ResponseEntity<?> updateProduct(
            @PathVariable("productId") Long productId,
            @Valid ProductRequest productRequest,
            BindingResult result
    ) throws IOException {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
            Product product = productService.updateProduct(productId, productRequest);
            return ResponseEntity.ok(product);

    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getProductImage(@PathVariable Long id) {
        try {
            byte[] imageBytes = productService.getImage(id);
            if (imageBytes == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
