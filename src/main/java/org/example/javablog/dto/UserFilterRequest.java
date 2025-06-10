package org.example.javablog.dto;

import lombok.Data;

@Data
public class UserFilterRequest {
    private String username;
    private String fullName;
    private String relationshipType;
}
