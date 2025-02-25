package com.perso.ecomm.util;

import com.perso.ecomm.product.ProductService;
import com.perso.ecomm.user.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/images")
public class ImageController {

    private final UserService userService;
    private final ProductService productService;

    public ImageController(UserService userService, ProductService productService) {
        this.userService = userService;
        this.productService = productService;
    }

    @GetMapping("/{type}/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable String type, @PathVariable Long id) {
        try {
            byte[] imageBytes;

            // Determine the entity type and fetch the image
            switch (type.toLowerCase()) {
                case "user":
                    imageBytes = userService.getUserImage(id);
                    break;
                case "product":
                    imageBytes = productService.getImage(id);
                    break;
                default:
                    return ResponseEntity.badRequest().body(null);
            }

            // If no image is found, return 404
            if (imageBytes == null) {
                return ResponseEntity.notFound().build();
            }

            // Return the image as a response
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
