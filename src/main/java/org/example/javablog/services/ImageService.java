package org.example.javablog.services;

import org.example.javablog.model.Image;
import org.example.javablog.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
public class ImageService {
    @Value("${image.upload-dir}")
    private String uploadDir;

    @Autowired
    private ImageRepository imageRepository;

    public Image storeFile(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        try {
            if (file.isEmpty()) {
                throw new IOException("File is empty");
            }
            String hashValue = computeSHA256Hash(file);
            Optional<Image> existingImage = imageRepository.findByHash(hashValue);
            if (existingImage.isPresent()) {
                return existingImage.get();
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IOException("Only image files are allowed.");
            }
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            if (fileName.length() > 255) {
                throw new IOException("File name is too long.");
            }
            Path destinationPath = uploadPath.resolve(fileName).normalize().toAbsolutePath();
            if (!destinationPath.getParent().equals(uploadPath.toAbsolutePath())) {
                throw new IOException("Cannot store file outside the target directory.");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            }

            Image image = new Image();
            image.setFileName(fileName);
            image.setHash(hashValue);
            System.out.println(image.getHash());
            return imageRepository.save(image);
        } catch (IOException e) {
            throw new IOException("Failed to store file: " + e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            throw new NoSuchAlgorithmException("SHA-256 algorithm not found: " + e.getMessage(), e);
        }
    }
    public Resource loadAsResource(String fileName) throws IOException {
        Path file = getImagePathByFileName(fileName);
        try {
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new IOException("Could not read the file: " + fileName);
            }
        } catch (MalformedURLException e) {
            throw new IOException("Invalid file path", e);
        }
    }

    private Path getImagePathByFileName(String fileName) throws IOException {
        Optional<Image> optionalImage = imageRepository.findByFileName(fileName);
        if (optionalImage.isEmpty()) {
            throw new IOException("Image not found in database");
        }

        Path path = Paths.get(uploadDir).resolve(fileName).normalize().toAbsolutePath();
        if (!path.toFile().exists()) {
            throw new IOException("Image file not found on disk");
        }

        return path;
    }


    private String computeSHA256Hash(MultipartFile file) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] fileBytes = digest.digest(file.getBytes());
        return Base64.getEncoder().encodeToString(fileBytes);
    }

}
