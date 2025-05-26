package org.example.javablog.repository;

import org.example.javablog.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNullApi;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    boolean existsByHash(String hash);
    Optional<Image> findByHash(String hash);
    Optional<Image> findByFileName(String fileName);
}
