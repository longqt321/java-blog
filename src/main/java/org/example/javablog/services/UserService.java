package org.example.javablog.services;

import org.example.javablog.constant.UserRelationshipType;
import org.example.javablog.model.UserRelationship;
import org.example.javablog.repository.UserRelationshipRepository;
import org.springframework.security.core.Authentication;
import org.example.javablog.dto.UserDTO;
import org.example.javablog.mapper.UserMapper;
import org.example.javablog.constant.Role;
import org.example.javablog.model.User;
import org.example.javablog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.javablog.util.UserUtils;

import java.util.List;
import java.util.Objects;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRelationshipRepository userRelationshipRepository;

    @Autowired
    private UserUtils userUtils;

    public List<UserDTO> getAllUsers() {
        return UserMapper.toDTOList(userRepository.findAll()).stream().map(userDTO -> {
            userUtils.enrichUserDTO(userDTO);
            return userDTO;
        }).toList();
    }
    public UserDTO getUserById(Long id) {
        UserDTO userDTO = UserMapper.toDTO(Objects.requireNonNull(userRepository.findById(id).orElse(null)));
        userUtils.enrichUserDTO(userDTO);
        return userDTO;
    }
    public UserDTO getUserByUsername(String username){
        return UserMapper.toDTO(Objects.requireNonNull(userRepository.findByUsername(username).orElse(null)));
    }
    public void deleteUser(Long deletedUserId, Long userId) {
        User user = userRepository.findById(deletedUserId).orElseThrow(NullPointerException::new);
        if (!deletedUserId.equals(userId) || !isAdmin(userId)) {
            throw new SecurityException("You are not unauthorized to delete this user");
        }
        userRepository.delete(user);
    }
    public boolean isAdmin(Long userID){
        return this.getCurrentUser().getRole().equals(Role.ROLE_ADMIN);
    }
    public void followUser(Long sourceId,Long targetId){
        if (sourceId.equals(targetId)) return;
        if (userRelationshipRepository.existsBySourceUserIdAndTargetUserIdAndUserRelationshipType(sourceId, targetId, UserRelationshipType.FOLLOWING)){
            throw new SecurityException("You are already following this user");
        }
        // sourceUser is block targetUser
        if (userRelationshipRepository.existsBySourceUserIdAndTargetUserIdAndUserRelationshipType(sourceId, targetId, UserRelationshipType.BLOCKING)){
            throw new SecurityException("You are blocking this user");
        }

        userRelationshipRepository.save(UserRelationship.fromIds(sourceId, targetId, UserRelationshipType.FOLLOWING));
    }
    @Transactional
    public void unfollowUser(Long sourceId,Long targetId){
        if (!userRelationshipRepository.existsBySourceUserIdAndTargetUserIdAndUserRelationshipType(sourceId, targetId, UserRelationshipType.FOLLOWING)){
            throw new SecurityException("You are not following this user");
        }
        userRelationshipRepository.deleteBySourceUserIdAndTargetUserIdAndUserRelationshipType(sourceId,targetId,UserRelationshipType.FOLLOWING);
    }
    @Transactional
    public void blockUser(Long sourceId,Long targetId){
        if (sourceId.equals(targetId)) return;
        if (userRelationshipRepository.existsBySourceUserIdAndTargetUserIdAndUserRelationshipType(sourceId, targetId, UserRelationshipType.BLOCKING)){
            throw new SecurityException("You are already blocking this user");
        }
        userRelationshipRepository.deleteBySourceUserIdAndTargetUserIdAndUserRelationshipType(sourceId,targetId,UserRelationshipType.FOLLOWING); // Delete the following relationship if it exists

        userRelationshipRepository.save(UserRelationship.fromIds(sourceId, targetId, UserRelationshipType.BLOCKING));
    }
    @Transactional
    public void unblockUser(Long sourceId,Long targetId){
        if (!userRelationshipRepository.existsBySourceUserIdAndTargetUserIdAndUserRelationshipType(sourceId, targetId, UserRelationshipType.BLOCKING)){
            throw new SecurityException("You are not blocking this user");
        }
        userRelationshipRepository.deleteBySourceUserIdAndTargetUserIdAndUserRelationshipType(sourceId,targetId,UserRelationshipType.BLOCKING);
    }
    public Long getFollowerCount(Long userId){
        return userRelationshipRepository.countByTargetUserIdAndUserRelationshipType(userId, UserRelationshipType.FOLLOWING);
    }
    public Long getFollowingCount(Long userId){
        return userRelationshipRepository.countBySourceUserIdAndUserRelationshipType(userId, UserRelationshipType.FOLLOWING);
    }

    public UserDTO getCurrentUser(){
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        return UserMapper.toDTO(Objects.requireNonNull(userRepository.findByUsername(username).orElse(null)));
    }
}
