package org.example.javablog.specifications;

import org.example.javablog.constant.Role;
import org.example.javablog.dto.UserFilterRequest;
import org.example.javablog.model.User;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;


public class UserSpecification {
    public static Specification<User> filterBy(UserFilterRequest filter) {
        Specification<User> spec = Specification.where(null);
        spec = spec.and(hasRole(String.valueOf(Role.ROLE_USER)));
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
                    cb.concat(cb.lower(root.get("lastName")), " "),
                    cb.lower(root.get("firstName"))
            );
            return cb.like(fullNameExpression, "%" + fullName.toLowerCase() + "%");
        };
    }
    private static Specification<User> hasRelationship(String relationship) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get("relationship")), relationship.toLowerCase());
    }
    private static Specification<User> hasRole(String role) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get("role")), role.toLowerCase());
    }
    public static Specification<User> excludeCurrentUser(Long currentUserId) {
        return (root, query, cb) -> cb.notEqual(root.get("id"), currentUserId);
    }
}
