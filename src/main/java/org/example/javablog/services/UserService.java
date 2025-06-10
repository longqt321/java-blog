package org.example.javablog.services;

import org.example.javablog.constant.UserRelationshipType;
import org.example.javablog.dto.UserFilterRequest;
import org.example.javablog.model.Image;
import org.example.javablog.model.UserRelationship;
import org.example.javablog.repository.PostRepository;
import org.example.javablog.repository.UserRelationshipRepository;
import org.example.javablog.specifications.UserSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRelationshipRepository userRelationshipRepository;

    @Autowired
    private ImageService imageService;


    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<Long> userIds = users.stream().map(User::getId).toList();
// Code below is supposed to be used to improve performance by reducing the number of queries
//        Map<Long, Long> followerCountMap = userRepository.countFollowersByUserIds(userIds).stream()
//                .collect(Collectors.toMap(
//                        row -> (Long) row[0],
//                        row -> (Long) row[1]
//                ));
//
//        Map<Long, Long> followingCountMap =  userRepository.countFollowingByUserIds(userIds).stream()
//                .collect(Collectors.toMap(
//                        row -> (Long) row[0],
//                        row -> (Long) row[1]
//                ));
//
//        Map<Long, Long> postCountMap = postRepository.countPostsByAuthorIds(userIds).stream()
//                .collect(Collectors.toMap(
//                        row -> (Long) row[0],
//                        row -> (Long) row[1]
//                ));

        return UserMapper.toDTOList(users).stream().map(userDTO -> {
            try {
                return this.enrichUserDTO(userDTO);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }
    public Page<UserDTO> searchUsers(UserFilterRequest filter, Pageable pageable){
        Long currentUserId = this.getCurrentUser().getId();
        Specification<User> spec = UserSpecification.filterBy(filter);

        List<UserDTO> filteredList = userRepository.findAll(spec, pageable)
                .map(UserMapper::toDTO)
                .stream()
                .filter(user -> !user.getId().equals(currentUserId))
                .filter(user -> !user.getRole().equals(Role.ROLE_ADMIN)) // Exclude admin users
                .toList();
        return new PageImpl<>(filteredList, pageable, filteredList.size());
    }

    public UserDTO getUserById(Long id) throws IOException {
        UserDTO userDTO = UserMapper.toDTO(Objects.requireNonNull(userRepository.findById(id).orElse(null)));
        return this.enrichUserDTO(userDTO);
    }
    public UserDTO getUserByUsername(String username){
        return UserMapper.toDTO(Objects.requireNonNull(userRepository.findByUsername(username).orElse(null)));
    }
    public void deleteUser(Long deletedUserId) {
        Optional<User> user = userRepository.findById(deletedUserId);
        if (user.isEmpty()) {
            throw new NullPointerException("User not found");
        }
        if (user.get().getRole() == Role.ROLE_ADMIN) {
            throw new SecurityException("You cannot delete an admin user");
        }

        userRepository.delete(user.get());
    }
    public UserDTO updateUser(Long userId,UserDTO userDTO) throws IOException {
        User updatedUser = userRepository.findById(userId).orElseThrow(NullPointerException::new);
        updatedUser.setLastName(userDTO.getLastName());
        updatedUser.setFirstName(userDTO.getFirstName());
        updatedUser.setDescription(userDTO.getDescription());
        updatedUser.setEmail(userDTO.getEmail());
        Image avatar = new Image();
        avatar.setId(userDTO.getAvatarId());
        updatedUser.setAvatar(avatar);
        return this.enrichUserDTO(UserMapper.toDTO(userRepository.save(updatedUser)));
    }
    public boolean isAdmin(){
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
    public UserRelationshipType getUserRelationship(Long sourceId, Long targetId) {
        return userRelationshipRepository.findBySourceUserIdAndTargetUserId(sourceId, targetId)
                .map(UserRelationship::getUserRelationshipType)
                .orElse(null);
    }

    public UserDTO getCurrentUser(){
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        return UserMapper.toDTO(Objects.requireNonNull(userRepository.findByUsername(username).orElse(null)));
    }

    private UserDTO enrichUserDTO(UserDTO userDTO) throws IOException {
        userDTO.setFollowersCount(userRelationshipRepository.countByTargetUserIdAndUserRelationshipType(userDTO.getId(), UserRelationshipType.FOLLOWING));
        userDTO.setFollowingCount(userRelationshipRepository.countBySourceUserIdAndUserRelationshipType(userDTO.getId(), UserRelationshipType.FOLLOWING));
        userDTO.setPostCount(postRepository.countByAuthorId(userDTO.getId()));
        return userDTO;
    }
}
