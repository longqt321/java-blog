package org.example.javablog.services;

import org.example.javablog.constant.Relationship;
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

    public Relationship getRelationship(Long sourceId, Long targetId) {
        return userRelationshipRepository.findBySourceUserIdAndTargetUserId(sourceId, targetId)
                .map(UserRelationship::getRelationship)
                .orElse(Relationship.NONE);
    }
    public void follow(Long sourceId, Long targetId){
        UserRelationship userRelationship = userRelationshipRepository.findBySourceUserIdAndTargetUserId(sourceId, targetId)
                .orElse(new UserRelationship());

        User sourceUser = UserMapper.toEntity(userService.getUserById(sourceId));
        User targetUser = UserMapper.toEntity(userService.getUserById(targetId));

        userRelationship.setSourceUser(sourceUser);
        userRelationship.setTargetUser(targetUser);
        userRelationship.setRelationship(Relationship.FOLLOWING);
        userRelationshipRepository.save(userRelationship);
    }
    public void block(Long sourceId, Long targetId){
        UserRelationship userRelationship = userRelationshipRepository.findBySourceUserIdAndTargetUserId(sourceId, targetId)
                .orElse(new UserRelationship());

        User sourceUser = UserMapper.toEntity(userService.getUserById(sourceId));
        User targetUser = UserMapper.toEntity(userService.getUserById(targetId));

        userRelationship.setSourceUser(sourceUser);
        userRelationship.setTargetUser(targetUser);
        userRelationship.setRelationship(Relationship.BLOCKING);
        userRelationshipRepository.save(userRelationship);
    }
    public void resetRelationship(Long sourceId, Long targetId){
        userRelationshipRepository.deleteBySourceUserIdAndTargetUserId(sourceId, targetId);
    }
}
