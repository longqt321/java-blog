package org.example.javablog.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.javablog.dto.PostDTO;
import org.example.javablog.dto.RecommenderRequestDTO;
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

    public void recommendPosts(List<RecommenderRequestDTO> data) throws IOException {
        String path = recommenderDataPath + File.separator + "input.json";
        objectMapper.writeValue(new File(path), data);
        System.out.println("Recommender data written to: " + path);
    }
}
