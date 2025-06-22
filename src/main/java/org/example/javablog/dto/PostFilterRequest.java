package org.example.javablog.dto;

import lombok.Data;

import java.util.List;

@Data
public class PostFilterRequest {
    private Long id;
    private String title;
    private List<String> hashtags;
    private String authorName;
    private String authorUsername;
    private String visibility;
    private Long userId;
    private Long authorId;
    private String relationshipType;
    private boolean excludeHidden = true;
}
