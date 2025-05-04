package org.example.javablog.repository;

import org.example.javablog.dto.HashtagDTO;
import org.example.javablog.model.Hashtag;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.Optional;


@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    Optional<Hashtag> findByName(String name);
    @Query("""
            SELECT new org.example.javablog.dto.HashtagDTO(h.id,h.name,COUNT(p))
            FROM Hashtag h
            JOIN h.posts p
            GROUP BY h.id,h.name
            ORDER BY COUNT(p) DESC
            """)
    Page<HashtagDTO> findPopularHashtags(Pageable pageable);
}
