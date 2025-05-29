package org.example.javablog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.javablog.model.PostRelationship;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommenderRequestDTO {
    private Long userId;
    private Long postId;
    private List<String> hashtags;
    private List<String> interests;
    private boolean liked;
    private boolean saved;
    private boolean hidden;
    private boolean reported;
    private boolean authorFollowed;
    private boolean authorBlocked;

}