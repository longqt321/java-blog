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

import java.util.List;
import java.util.Objects;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRelationshipRepository userRelationshipRepository;

    public List<UserDTO> getAllUsers() {
        return UserMapper.toDTOList(userRepository.findAll());
    }
    public UserDTO getUserById(Long id) {
        return UserMapper.toDTO(Objects.requireNonNull(userRepository.findById(id).orElse(null)));
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
        return userRepository.findById(userID)
                .map(user -> user.getRole() == Role.ROLE_ADMIN)
                .orElse(false);
    }
    public void followUser(Long sourceId,Long targetId){
        if (sourceId.equals(targetId)) return;
        if (userRelationshipRepository.existsBySourceUserIdAndTargetUserIdAndUserRelationshipType(sourceId, targetId, UserRelationshipType.FOLLOWING)){
            throw new SecurityException("You are already following this user");
        }
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
        if (userRelationshipRepository.existsBySourceUserIdAndTargetUserIdAndUserRelationshipType(sourceId, targetId, UserRelationshipType.FOLLOWING)){
            this.unfollowUser(sourceId, targetId);
        }
        userRelationshipRepository.save(UserRelationship.fromIds(sourceId, targetId, UserRelationshipType.BLOCKING));
    }
    @Transactional
    public void unblockUser(Long sourceId,Long targetId){
        if (!userRelationshipRepository.existsBySourceUserIdAndTargetUserIdAndUserRelationshipType(sourceId, targetId, UserRelationshipType.BLOCKING)){
            throw new SecurityException("You are not blocking this user");
        }
        userRelationshipRepository.deleteBySourceUserIdAndTargetUserIdAndUserRelationshipType(sourceId,targetId,UserRelationshipType.BLOCKING);
    }

    public UserDTO getCurrentUser(){
        final Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication();
        final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        return UserMapper.toDTO(Objects.requireNonNull(userRepository.findByUsername(username).orElse(null)));
    }
}
