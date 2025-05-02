package org.example.javablog.mapper;

import org.example.javablog.dto.HashtagDTO;
import org.example.javablog.model.Hashtag;
import org.example.javablog.model.Post;


import java.util.HashSet;
import java.util.Set;
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
    public static Set<HashtagDTO> toDTOList(Set<Hashtag> hashtags) {
        return hashtags.stream().map(HashtagMapper::toDTO).collect(Collectors.toSet());
    }

    public static Set<Hashtag> toEntityListFromDTO(Set<HashtagDTO> hashtagDTOs) {
        return hashtagDTOs.stream().map(HashtagMapper::toEntity).collect(Collectors.toSet());
    }
    public static Set<Hashtag> toEntityListFromName(Set<String> hashtagNames) {
        return hashtagNames.stream()
                .map(name -> new Hashtag(null, name, new HashSet<>()))
                .collect(Collectors.toSet());
    }
    public static Set<String> toStringList(Set<HashtagDTO> hashtagDTOs) {
        return hashtagDTOs.stream()
                .map(HashtagDTO::getName)
                .collect(Collectors.toSet());
    }

}
