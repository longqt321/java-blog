package org.example.javablog.controller;

import org.example.javablog.services.HashtagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/hashtags")
public class HashtagController {
    @Autowired
    private HashtagService hashtagService;
    @GetMapping("/popular")
    public ResponseEntity<List<String>> getPopularHashtagNames() {
        List<String> hashtags = hashtagService.getPopularHashtagNames();
        return ResponseEntity.ok(hashtags);
    }
}
