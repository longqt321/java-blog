package org.example.javablog.services;

import jakarta.transaction.Transactional;
import org.example.javablog.mapper.UserMapper;
import org.example.javablog.model.FollowRelationship;
import org.example.javablog.repository.FollowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.javablog.model.User;

@Service
public class FollowService {
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private UserService userService;

    public void followUser(Long followingUserId,Long followedUserId){
        User followingUser = UserMapper.toEntity(userService.getUserById(followingUserId));
        User followedUser = UserMapper.toEntity(userService.getUserById(followedUserId));
        if (followRepository.existsByFollowingUserAndFollowedUser(followingUser, followedUser)) {
            return; // Already following
        }
        FollowRelationship followRelationship = new FollowRelationship();
        followRelationship.setFollowedUser(followedUser);
        followRelationship.setFollowingUser(followingUser);
        followRepository.save(followRelationship);
    }
    @Transactional
    public void unFollowUser(Long followingUserId,Long followedUserId){
       User followingUser = UserMapper.toEntity(userService.getUserById(followingUserId));
       User followedUser = UserMapper.toEntity(userService.getUserById(followedUserId));
       followRepository.deleteByFollowingUserAndFollowedUser(followingUser,followedUser);
    }
}
