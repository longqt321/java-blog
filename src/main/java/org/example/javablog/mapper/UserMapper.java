package org.example.javablog.mapper;

import org.example.javablog.dto.UserDTO;
import org.example.javablog.model.User;

import java.util.List;

public class UserMapper {
    public static UserDTO toDTO(User user){
        return new UserDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getDescription(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
    public static List<UserDTO> toDTOList(List<User> users) {
        return users.stream().map(UserMapper::toDTO).toList();
    }
}
