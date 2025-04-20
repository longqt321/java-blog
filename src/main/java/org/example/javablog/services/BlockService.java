package org.example.javablog.services;

import jakarta.transaction.Transactional;
import org.example.javablog.mapper.UserMapper;
import org.example.javablog.model.BlockRelationship;
import org.example.javablog.model.User;
import org.example.javablog.repository.BlockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class BlockService {
    @Autowired
    private BlockRepository blockRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private FollowService followService;

    public void blockUser(Long blockingUserId,Long blockedUserId){
        if (Objects.equals(blockingUserId,blockedUserId)){
            return;
        }
        User blockingUser = UserMapper.toEntity(userService.getUserById(blockingUserId));
        User blockedUser = UserMapper.toEntity(userService.getUserById(blockedUserId));
        if (blockRepository.existsByBlockingUserAndBlockedUser(blockingUser,blockedUser)){
            return;
        }
        BlockRelationship blockRelationship = new BlockRelationship();
        blockRelationship.setBlockingUser(blockingUser);
        blockRelationship.setBlockedUser(blockedUser);
        followService.unfollowUser(blockingUserId,blockedUserId);
        blockRepository.save(blockRelationship);
    }

    @Transactional
    public void unblockUser(Long blockingUserId,Long blockedUserId){
        if (Objects.equals(blockingUserId,blockedUserId)){
            return;
        }
        User unblockingUser = UserMapper.toEntity(userService.getUserById(blockingUserId));
        User unblockedUser = UserMapper.toEntity(userService.getUserById(blockedUserId));
        blockRepository.deleteByBlockingUserAndBlockedUser(unblockingUser,unblockedUser);
    }
}
