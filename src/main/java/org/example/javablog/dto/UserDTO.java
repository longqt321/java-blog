package org.example.javablog.dto;

import lombok.*;
import org.example.javablog.constant.Role;

import java.sql.Timestamp;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String description;
    private Role role;
    private LocalDateTime createdAt;

}
