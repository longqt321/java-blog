package org.example.javablog.repository;

import org.example.javablog.model.RecommendScore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendScoreRepository extends JpaRepository<RecommendScore, Long> {
    void deleteByPostId(Long postId);
}
