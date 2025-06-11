package org.example.javablog.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import org.example.javablog.models.RecommendScoreId;

@Entity
@Table(name = "recommend_score")
@IdClass(RecommendScore.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendScore {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "post_id")
    private Long postId;


    @Column(name = "score", precision = 5, scale = 4, nullable = false)
    private BigDecimal score;

}
