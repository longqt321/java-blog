package org.example.javablog.specifications;

import org.example.javablog.constant.Role;
import org.example.javablog.constant.UserRelationshipType;
import org.example.javablog.dto.UserFilterRequest;
import org.example.javablog.model.User;
import org.example.javablog.model.UserRelationship;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;
import org.example.javablog.util.SessionUtils;

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
        spec = spec.and(excludeCurrentUser());
        spec = spec.and(excludeAdmins());
        return spec;
    }
    private static Specification<User> excludeCurrentUser() {
        return (root, query, cb) -> cb.notEqual(root.get("id"), SessionUtils.getCurrentUserId());
    }
    private static Specification<User> excludeAdmins(){
        return (root, query, cb) -> cb.notEqual(root.get("role"), Role.ROLE_ADMIN);
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
        return (root, query, cb) -> {
            System.out.println("Filtering users by relationship: " + relationship);
            assert query != null;
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<UserRelationship> relRoot = subquery.from(UserRelationship.class);
            subquery.select(relRoot.get("targetUser").get("id"))
                    .where(
                            cb.equal(relRoot.get("userRelationshipType"), UserRelationshipType.valueOf(relationship)),
                            cb.equal(relRoot.get("sourceUser").get("id"), SessionUtils.getCurrentUserId())
                    );
            return root.get("id").in(subquery);
        };
    }
}
