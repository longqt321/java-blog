package org.example.javablog.dto;

import lombok.*;
import org.example.javablog.constant.Role;

import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String description;
    private Long followersCount;
    private Long followingCount;
    private Long postCount;
    private String email;
    private String avatarUrl;
    private Long avatarId;
    private Role role;
    private LocalDateTime createdAt;
    private Set<String> interests;
}
