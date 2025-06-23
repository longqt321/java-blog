package org.example.javablog.repository;

import org.example.javablog.model.RecommendScore;
import org.example.javablog.model.RecommendScoreId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendScoreRepository extends JpaRepository<RecommendScore, RecommendScoreId> {
    void deleteByPostId(Long postId);
}
