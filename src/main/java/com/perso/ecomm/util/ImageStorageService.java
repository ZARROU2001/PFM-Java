package com.perso.ecomm.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Service
public class ImageStorageService {

    @Value("${user.image.upload-dir}")  // Configurable directory path
    private String uploadDir;

    public String saveImage(String userId, MultipartFile file) throws IOException {
        String fileName = userId + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);

        // Ensure directory exists
        Files.createDirectories(filePath.getParent());

        // Save the image
        Files.write(filePath, file.getBytes());

        return filePath.toString();
    }

    public byte[] loadImage(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllBytes(path);
    }

    public void deleteImage(String filePath) throws IOException {
        if (filePath != null) {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
            }
        }
    }
}
