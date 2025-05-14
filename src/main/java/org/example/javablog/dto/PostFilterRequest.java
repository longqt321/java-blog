package org.example.javablog.dto;

import lombok.Data;

import java.util.List;

@Data
public class PostFilterRequest {
    private String title;
    private List<String> hashtags;
    private String authorName;
    private String username;
    private String visibility;
    private Long userId;
    private Long authorId;
    private String relationshipType;
}
