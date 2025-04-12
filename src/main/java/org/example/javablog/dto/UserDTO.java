package org.example.javablog.dto;

import lombok.*;
import org.example.javablog.model.Role;

import java.sql.Timestamp;


@Data
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String description;
    private Role role;
    private Timestamp createdAt;
}
