package org.example.javablog.controller;

import org.example.javablog.dto.HashtagDTO;
import org.example.javablog.services.HashtagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/hashtags")
public class HashtagController {
    @Autowired
    private HashtagService hashtagService;
    @GetMapping("/popular")
    public ResponseEntity<Page<HashtagDTO>> getPopularHashtagNames(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<HashtagDTO> result = hashtagService.getPopularHashtags(pageable);
        return ResponseEntity.ok(result);
    }
}
