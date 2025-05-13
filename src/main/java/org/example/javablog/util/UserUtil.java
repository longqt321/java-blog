package org.example.javablog.util;

import org.example.javablog.repository.UserRelationshipRepository;
import org.example.javablog.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserUtil {
    private final UserRepository userRepository;
    private final UserRelationshipRepository userRelationshipRepository;

    public UserUtil(UserRepository userRepository, UserRelationshipRepository userRelationshipRepository) {
            this.userRepository = userRepository;
            this.userRelationshipRepository = userRelationshipRepository;
    }
}
