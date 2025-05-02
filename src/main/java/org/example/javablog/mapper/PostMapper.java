package org.example.javablog.mapper;

import org.example.javablog.dto.PostDTO;
import org.example.javablog.model.Hashtag;
import org.example.javablog.model.Post;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PostMapper {
    public static PostDTO toDTO(Post post) {
        return new PostDTO(
                post.getId(),
                post.getTitle(),
                post.getBody(),
                UserMapper.toDTO(post.getAuthor()),
                post.getStatus(),
                post.getCreatedAt(),
                getHashtagNames(post.getHashtags())
        );
    }
    public static List<PostDTO> toDTOList(List<Post> posts) {
        return posts.stream().map(PostMapper::toDTO).toList();
    }
    private static Set<String> getHashtagNames(Set<Hashtag> hashtags) {
        return hashtags.stream()
                .map(Hashtag::getName)
                .collect(Collectors.toSet());
    }

    public static Post toEntity(PostDTO post) {
        return new Post(
                post.getId(),
                post.getTitle(),
                post.getBody(),
                UserMapper.toEntity(post.getAuthor()),
                post.getStatus(),
                post.getCreatedAt(),
                HashtagMapper.toEntitySetFromName(post.getHashtags())
        );
    }
}
