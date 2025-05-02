package org.example.javablog.repository;

import org.example.javablog.model.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    Optional<Hashtag> findByName(String name);
    @Query("""
            SELECT h FROM Hashtag h
            JOIN h.posts p
            GROUP BY h.id
            ORDER BY COUNT(p) DESC
            """)
    List<Hashtag> findPopularHashtags();
}
