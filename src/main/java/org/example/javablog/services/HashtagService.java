package org.example.javablog.services;

import org.example.javablog.dto.HashtagDTO;
import org.example.javablog.mapper.HashtagMapper;
import org.example.javablog.model.Hashtag;
import org.example.javablog.repository.HashtagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class HashtagService {
    @Autowired
    private HashtagRepository hashtagRepository;

    public Hashtag getOrCreateHashtag(String name) {
        String normalizedName = name.replaceAll(" ","").toLowerCase();
        Optional<Hashtag> existingHashtag = hashtagRepository.findByName(normalizedName);
        return existingHashtag.orElseGet(() -> {
            Hashtag newHashtag = new Hashtag();
            newHashtag.setName(normalizedName);
            return hashtagRepository.save(newHashtag);
        });
    }
    public List<String> getPopularHashtagNames(){
        List<Hashtag> hashtags = hashtagRepository.findPopularHashtags();
        return hashtags.stream()
                .map(Hashtag::getName)
                .toList();
    }
}
