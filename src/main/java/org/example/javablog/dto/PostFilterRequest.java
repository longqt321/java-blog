package org.example.javablog.dto;

import lombok.Data;

import java.util.List;

@Data
public class PostFilterRequest {
    private String title;
    private List<String> hashtags;
    private String authorName;
}
