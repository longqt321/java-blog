package org.example.javablog.services;

import org.example.javablog.dto.UserDTO;
import org.example.javablog.mapper.UserMapper;
import org.example.javablog.model.Role;
import org.example.javablog.model.User;
import org.example.javablog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<UserDTO> getAllUsers() {
        return UserMapper.toDTOList(userRepository.findAll());
    }
    public UserDTO getUserById(Long id) {
        return UserMapper.toDTO(Objects.requireNonNull(userRepository.findById(id).orElse(null)));
    }
    public void deleteUser(Long deletedUserId, Long userId) {
        User user = userRepository.findById(deletedUserId).orElseThrow(NullPointerException::new);
        if (!deletedUserId.equals(userId) && !isAdmin(userId)) {
            throw new SecurityException("You are not unauthorized to delete this user");
        }
        userRepository.delete(user);
    }
    public boolean isAdmin(Long userID){
        return userRepository.findById(userID)
                .map(user -> user.getRole() == Role.ROLE_ADMIN)
                .orElse(false);
    }
}
