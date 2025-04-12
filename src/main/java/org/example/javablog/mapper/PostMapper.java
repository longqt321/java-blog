package org.example.javablog.mapper;

import org.example.javablog.dto.PostDTO;
import org.example.javablog.model.Post;

import java.util.List;

public class PostMapper {
    public static PostDTO toDTO(Post post) {
        return new PostDTO(
                post.getId(),
                post.getTitle(),
                post.getBody(),
                UserMapper.toDTO(post.getAuthor()),
                post.getStatus(),
                post.getCreatedAt(),
                HashtagMapper.toDTOList(post.getHashtags())
        );
    }
    public static List<PostDTO> toDTOList(List<Post> posts) {
        return posts.stream().map(PostMapper::toDTO).toList();
    }
}
