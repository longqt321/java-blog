package org.example.javablog.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.example.javablog.constant.UserRelationshipType;
import org.example.javablog.dto.UserDTO;
import org.example.javablog.repository.PostRepository;
import org.example.javablog.repository.UserRelationshipRepository;
import org.example.javablog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component

public class UserUtils {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRelationshipRepository userRelationshipRepository;

    @Autowired
    private PostRepository postRepository;

    public void enrichUserDTO(UserDTO userDTO) {
        userDTO.setFollowersCount(userRelationshipRepository.countByTargetUserIdAndUserRelationshipType(userDTO.getId(), UserRelationshipType.FOLLOWING));
        userDTO.setFollowingCount(userRelationshipRepository.countBySourceUserIdAndUserRelationshipType(userDTO.getId(), UserRelationshipType.FOLLOWING));
        userDTO.setPostCount(postRepository.countByAuthorId(userDTO.getId()));
    }
}
