package org.example.javablog.specifications;

import jakarta.persistence.criteria.*;
import org.example.javablog.constant.PostRelationshipType;
import org.example.javablog.dto.PostFilterRequest;
import org.example.javablog.model.Hashtag;
import org.example.javablog.model.Post;
import org.example.javablog.model.PostRelationship;
import org.example.javablog.model.RecommendScore;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.List;

public class PostSpecification {
    public static Specification<Post> filterBy(PostFilterRequest filter){
        Specification<Post> spec = Specification.where(null);
        if (filter.getTitle() != null && !filter.getTitle().isEmpty()) {
            spec = spec.and(hasTitle(filter.getTitle()));
        }
        if (filter.getHashtags() != null && !filter.getHashtags().isEmpty()) {
            spec = spec.and(hasHashtags(filter.getHashtags()));
        }
        if (filter.getAuthorName() != null && !filter.getAuthorName().isEmpty()) {
            spec = spec.and(hasAuthorName(filter.getAuthorName()));
        }
        if (filter.getVisibility() != null  && !filter.getVisibility().isEmpty()) {
            spec = spec.and(hasVisibility(filter.getVisibility()));
        }
        if (filter.getRelationshipType() != null && !filter.getRelationshipType().isEmpty()  && filter.getUserId() != null   ) {
            spec = spec.and(hasRelationship(filter.getRelationshipType(), filter.getUserId()));
        }
        if (filter.getAuthorId() != null && filter.getAuthorId() > 0) {
            spec = spec.and(hasAuthorId(filter.getAuthorId()));
        }
        if (filter.getAuthorUsername() != null && !filter.getAuthorUsername().isEmpty()) {
            spec = spec.and(hasAuthorUsername(filter.getAuthorUsername()));
        }


        return spec;
    }
    private static Specification<Post> hasHashtags(List<String> hashtags) {
        return (root, query, cb) -> {
            if (hashtags == null || hashtags.isEmpty()) {
                return cb.conjunction(); // stop lọc
            }

            Join<Post, Hashtag> tagJoin = root.join("hashtags");

            Predicate inPredicate = tagJoin.get("name").in(hashtags);

            assert query != null;
            query.distinct(true); // tránh trùng bài viết nếu join nhiều hashtag

            return inPredicate;
        };
    }

    private static Specification<Post> hasTitle(String title) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    private static Specification<Post> hasAuthorName(String authorName) {
        return (root, query, cb) -> {
            Expression<String> fullName = cb.concat(
                    cb.lower(root.get("author").get("lastName")),
                    cb.literal(" ")
            );
            Expression<String> fullNameCombined = cb.concat(fullName, cb.lower(root.get("author").get("firstName")));

            return cb.like(fullNameCombined, "%" + authorName.toLowerCase() + "%");
        };
    }
    private static Specification<Post> hasVisibility(String visibility) {
        return (root, query, cb) -> cb.equal(root.get("visibility"), visibility);
    }
    private static Specification<Post> hasAuthorId(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("author").get("id"), userId);
    }
    private static Specification<Post> hasAuthorUsername(String username) {
        return (root, query, cb) -> cb.like(root.get("author").get("username"), "%" + username + "%");
    }
    private static Specification<Post> hasRelationship(String relationship, Long userId) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(relationship)) {
                return cb.conjunction();
            }

            assert query != null;
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<PostRelationship> relRoot = subquery.from(PostRelationship.class);

            subquery.select(relRoot.get("post").get("id"))
                    .where(
                            cb.equal(relRoot.get("user").get("id"), userId),
                            cb.equal(relRoot.get("postRelationshipType"), PostRelationshipType.valueOf(relationship.toUpperCase()))
                    );

            return root.get("id").in(subquery);
        };
    }
    public static Specification<Post> sortByRecommendScore(Long userId) {
        return (root, query, cb) -> {

            // Thêm JOIN thủ công thông qua tên bảng (bằng cách dùng subquery hoặc trực tiếp)
            assert query != null;
            Subquery<Double> subquery = query.subquery(Double.class);
            Root<RecommendScore> recommendRoot = subquery.from(RecommendScore.class);
            subquery.select(recommendRoot.get("score"));
            subquery.where(
                    cb.equal(recommendRoot.get("postId"), root.get("id")),
                    cb.equal(recommendRoot.get("userId"), userId)
            );

            // Sắp xếp theo subquery
            query.orderBy(cb.desc(subquery));

            // Trả về không có filter cụ thể, chỉ sắp xếp
            return cb.conjunction();
        };
    }
}
