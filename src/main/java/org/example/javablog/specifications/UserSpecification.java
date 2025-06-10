package org.example.javablog.specifications;

import org.example.javablog.dto.UserFilterRequest;
import org.example.javablog.model.User;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;


public class UserSpecification {
    public static Specification<User> filterBy(UserFilterRequest filter) {
        Specification<User> spec = Specification.where(null);
        if (filter.getUsername() != null && !filter.getUsername().isEmpty()) {
            spec = spec.and(hasUsername(filter.getUsername()));
        }
        if (filter.getFullName() != null && !filter.getFullName().isEmpty()) {
            spec = spec.and(hasFullName(filter.getFullName()));
        }
        if (filter.getRelationshipType() != null && !filter.getRelationshipType().isEmpty()) {
            spec = spec.and(hasRelationship(filter.getRelationshipType()));
        }
        return spec;
    }

    private static Specification<User> hasUsername(String username) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("username")), "%" + username.toLowerCase() + "%");
    }
    private static Specification<User> hasFullName(String fullName) {
        return (root, query, cb) -> {
            Expression<String> fullNameExpression = cb.concat(
                    cb.concat(cb.lower(root.get("firstName")), " "),
                    cb.lower(root.get("lastName"))
            );
            return cb.like(fullNameExpression, "%" + fullName.toLowerCase() + "%");
        };
    }
    private static Specification<User> hasRelationship(String relationship) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get("relationship")), relationship.toLowerCase());
    }
}
