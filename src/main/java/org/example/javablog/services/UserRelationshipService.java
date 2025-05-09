package org.example.javablog.services;

import jakarta.transaction.Transactional;
import org.example.javablog.constant.UserRelationshipType;
import org.example.javablog.mapper.UserMapper;
import org.example.javablog.model.User;
import org.example.javablog.model.UserRelationship;
import org.example.javablog.repository.UserRelationshipRepository;
import org.example.javablog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRelationshipService {
    @Autowired
    private UserRelationshipRepository userRelationshipRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    public UserRelationshipType getRelationship(Long sourceId, Long targetId) {
        return userRelationshipRepository.findBySourceUserIdAndTargetUserId(sourceId, targetId)
                .map(UserRelationship::getUserRelationshipType)
                .orElse(UserRelationshipType.NONE);
    }
    public void follow(Long sourceId, Long targetId){
        UserRelationship userRelationship = userRelationshipRepository.findBySourceUserIdAndTargetUserId(sourceId, targetId)
                .orElse(new UserRelationship());
        User sourceUser = UserMapper.toEntity(userService.getUserById(sourceId));
        User targetUser = UserMapper.toEntity(userService.getUserById(targetId));

        userRelationship.setSourceUser(sourceUser);
        userRelationship.setTargetUser(targetUser);
        userRelationship.setUserRelationshipType(UserRelationshipType.FOLLOWING);
        userRelationshipRepository.save(userRelationship);
    }
    public void block(Long sourceId, Long targetId){
        UserRelationship userRelationship = userRelationshipRepository.findBySourceUserIdAndTargetUserId(sourceId, targetId)
                .orElse(new UserRelationship());

        User sourceUser = UserMapper.toEntity(userService.getUserById(sourceId));
        User targetUser = UserMapper.toEntity(userService.getUserById(targetId));

        userRelationship.setSourceUser(sourceUser);
        userRelationship.setTargetUser(targetUser);
        userRelationship.setUserRelationshipType(UserRelationshipType.BLOCKING);
        userRelationshipRepository.save(userRelationship);
    }
    @Transactional
    public void resetRelationship(Long sourceId, Long targetId){
        userRelationshipRepository.deleteBySourceUserIdAndTargetUserId(sourceId, targetId);
    }
}
