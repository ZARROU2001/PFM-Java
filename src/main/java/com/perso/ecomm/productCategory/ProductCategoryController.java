package com.perso.ecomm.productCategory;

import com.perso.ecomm.exception.RequestValidationException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "product_category")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductCategoryController {
    private final ProductCategoryService productCategoryService;

    public ProductCategoryController(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    @GetMapping
    public List<ProductCategory> getCategories() {
        return productCategoryService.getAllCategories();
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/paginate")
    public Page<ProductCategory> paginateCategories(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortField", defaultValue = "categoryId") String sortField,
            @RequestParam(name = "sortOrder", defaultValue = "asc") String sortOrder) {
        Sort.Direction direction = sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, direction, sortField);
        return productCategoryService.getSortedAndPagedData(pageable);
    }

    @PostMapping
    public ResponseEntity<?> addNewCategory(@Valid ProductCategory productCategory, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            throw new RequestValidationException(errors.toString());
        }
            ProductCategory category = productCategoryService.addNewCategory(productCategory);
            return ResponseEntity.ok(category);

    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable("categoryId") @Valid Long categoryId) {
            productCategoryService.deleteCategory(categoryId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Product category deleted successfully");
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<?> updateCategory(
            @PathVariable("categoryId") @Valid Long categoryId,
            @Valid ProductCategory updatedCategory
    ) {
            return ResponseEntity.ok(productCategoryService.updateCategory(categoryId, updatedCategory));
    }
}
