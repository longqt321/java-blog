package org.example.javablog.mapper;

import org.example.javablog.dto.HashtagDTO;
import org.example.javablog.model.Hashtag;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HashtagMapper {
    public static HashtagDTO toDTO(Hashtag hashtag) {
        return new HashtagDTO(
                hashtag.getId(),
                hashtag.getName()
        );
    }
    public static Set<HashtagDTO> toDTOList(Set<Hashtag> hashtags) {
        return hashtags.stream().map(HashtagMapper::toDTO).collect(Collectors.toSet());
    }
}
