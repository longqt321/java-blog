package org.example.javablog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.javablog.model.Status;

import java.sql.Timestamp;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private Long id;
    private String title;
    private String body;
    private UserDTO author;
    private Status status;
    private Timestamp createdAt;
    private Set<HashtagDTO> hashtags;
}
