package org.example.javablog.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.javablog.dto.PostDTO;
import org.example.javablog.mapper.PostMapper;

import org.example.javablog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;


@Service
public class RecommenderService {
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${recommender-data-dir}")
    private String recommenderDataPath;

    private static final double HASHTAG_SIMILARITY_WEIGHT = 0.4;
    private static final double LIKE_RATIO = 0.2;
    private static final double LIKE_WEIGHT = 0.2;
    private static final double FOLLOWED_AUTHOR_WEIGHT = 0.3;
    private static final double BLOCKED_AUTHOR_WEIGHT = 0.2;

    public void recommendPosts(List<PostDTO> posts) throws IOException {
        String path = recommenderDataPath + File.separator + "input.json";
        objectMapper.writeValue(new File(path), posts);
        System.out.println("Recommender data written to: " + path);
    }
}
