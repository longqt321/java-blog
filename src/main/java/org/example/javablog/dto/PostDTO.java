package org.example.javablog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.javablog.constant.Visibility;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private Long id;
    private String title;
    private String body;
    private UserDTO author;
    private Visibility visibility;
    private LocalDateTime createdAt;
    private Set<String> hashtags;
    private PostRelationshipDTO relationship;
}
