package org.example.javablog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String nametag;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String description;
    private Timestamp createdAt;
}
