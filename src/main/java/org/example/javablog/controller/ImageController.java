package org.example.javablog.controller;

import org.example.javablog.dto.ApiResponse;
import org.example.javablog.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/images")
public class ImageController
{
    @Autowired
    private ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            if (file.isEmpty() || !StringUtils.hasText(originalFilename)) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(true,"Invalid file: file is empty or has no name.",null));
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(true,"Invalid file: only image files are allowed.",null));
            }

            String savedPath = imageService.storeFile(file).getFileName();
            return ResponseEntity.ok(new ApiResponse<>(true,"File uploaded successfully", savedPath));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false,ex.getMessage(),null));
        }
    }
    @GetMapping("/")
    public ResponseEntity<Resource> getImage(@RequestParam String url) throws IOException {
        try {
            Resource resource = imageService.loadAsResource(url);

            // Xác định content type an toàn hơn
            String contentType = determineContentType(resource);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .header(HttpHeaders.CACHE_CONTROL, "max-age=3600") // Cache 1 giờ
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private String determineContentType(Resource resource) throws IOException {
        String contentType = Files.probeContentType(resource.getFile().toPath());

        if (contentType == null) {
            String filename = resource.getFilename();
            if (filename != null) {
                String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
                switch (extension) {
                    case "jpg":
                    case "jpeg":
                        return "image/jpeg";
                    case "png":
                        return "image/png";
                    case "gif":
                        return "image/gif";
                    case "webp":
                        return "image/webp";
                    default:
                        return "application/octet-stream";
                }
            }
        }
        return contentType;
    }

}
