package com.automart.service;

import com.automart.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

// Stores uploaded car images on local disk under app.upload-dir, and
// returns a URL path that WebConfig serves as static content.
// (A production version would swap this for S3/Cloudinary — the service
// interface wouldn't need to change, which is worth mentioning in interviews
// as an example of designing behind an abstraction.)
@Service
public class FileStorageService {

    @Value("${app.upload-dir}")
    private String uploadDir;

    public String store(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("Cannot upload an empty file");
        }
        try {
            Path dirPath = Paths.get(uploadDir, "cars");
            Files.createDirectories(dirPath);

            String original = file.getOriginalFilename();
            String extension = (original != null && original.contains("."))
                    ? original.substring(original.lastIndexOf('.'))
                    : "";
            String filename = UUID.randomUUID() + extension;

            Path target = dirPath.resolve(filename);
            Files.copy(file.getInputStream(), target);

            return "/uploads/cars/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
        }
    }
}
