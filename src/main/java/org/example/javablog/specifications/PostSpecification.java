package org.example.javablog.specifications;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.example.javablog.dto.PostFilterRequest;
import org.example.javablog.model.Hashtag;
import org.example.javablog.model.Post;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class PostSpecification {
    public static Specification<Post> filterBy(PostFilterRequest filter){
        Specification<Post> spec = Specification.where(null);
        if (filter.getTitle() != null) {
            spec = spec.and(hasTitle(filter.getTitle()));
        }
        if (filter.getHashtags() != null && !filter.getHashtags().isEmpty()) {
            spec = spec.and(hasHashtags(filter.getHashtags()));
        }
        if (filter.getAuthorName() != null) {
            spec = spec.and(hasAuthorName(filter.getAuthorName()));
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
                    cb.lower(root.get("author").get("firstName")),
                    cb.literal(" ")
            );
            Expression<String> fullNameCombined = cb.concat(fullName, cb.lower(root.get("author").get("lastName")));

            return cb.like(fullNameCombined, "%" + authorName.toLowerCase() + "%");
        };
    }

}
