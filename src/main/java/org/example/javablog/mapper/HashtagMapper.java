package org.example.javablog.mapper;

import org.example.javablog.dto.HashtagDTO;
import org.example.javablog.model.Hashtag;
import org.example.javablog.model.Post;


import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

public class HashtagMapper {

    public static HashtagDTO toDTO(Hashtag hashtag) {
        return new HashtagDTO(
                hashtag.getId(),
                hashtag.getName()
        );
    }
    public static Hashtag toEntity(HashtagDTO hashtagDTO) {
        String normalizedName = hashtagDTO.getName()
                .replace(" ","")
                .toLowerCase();
        return new Hashtag(hashtagDTO.getId(),
                normalizedName,
                new HashSet<Post>());
    }
    public static String toString(Hashtag hashtag) {
        return hashtag.getName();
    }
    public static Set<HashtagDTO> toDTOSet(Set<Hashtag> hashtags) {
        return hashtags.stream().map(HashtagMapper::toDTO).collect(Collectors.toSet());
    }
    public static Set<Hashtag> toEntitySetFromDTO(Set<HashtagDTO> hashtagDTOs) {
        return hashtagDTOs.stream().map(HashtagMapper::toEntity).collect(Collectors.toSet());
    }
    public static Set<Hashtag> toEntitySetFromName(Set<String> hashtagNames) {
        return hashtagNames.stream()
                .map(name -> new Hashtag(null, name, new HashSet<>()))
                .collect(Collectors.toSet());
    }
    public static Set<String> toStringSet(Set<HashtagDTO> hashtagDTOs) {
        return hashtagDTOs.stream()
                .map(HashtagDTO::getName)
                .collect(Collectors.toSet());
    }

}
