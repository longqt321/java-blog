package org.example.javablog.services;

import jakarta.transaction.Transactional;
import org.example.javablog.mapper.UserMapper;
import org.example.javablog.model.FollowRelationship;
import org.example.javablog.repository.FollowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.javablog.model.User;

import java.util.Objects;

@Service
public class FollowService {
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private UserService userService;

    public void followUser(Long followingUserId,Long followedUserId){
        if (Objects.equals(followingUserId,followedUserId)){
            return;
        }
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
    public void unfollowUser(Long unfollowingUserId,Long unfollowedUserId){
        if (Objects.equals(unfollowingUserId,unfollowedUserId)){
            return;
        }
       User unfollowingUser = UserMapper.toEntity(userService.getUserById(unfollowingUserId));
       User unfollowedUser = UserMapper.toEntity(userService.getUserById(unfollowedUserId));
       followRepository.deleteByFollowingUserAndFollowedUser(unfollowingUser,unfollowedUser);
    }
}
