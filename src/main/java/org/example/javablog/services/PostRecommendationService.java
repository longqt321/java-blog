package org.example.javablog.services;

import org.example.javablog.dto.PostDTO;
import org.example.javablog.mapper.PostMapper;

import org.example.javablog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PostRecommendationService {
    @Autowired
    private PostRepository blogRepository;

    @Autowired
    private HashtagService hashtagService;

    @Autowired
    private UserService userService;

    private static final double HASHTAG_SIMILARITY_WEIGHT = 0.4;
    private static final double LIKE_RATIO = 0.2;
    private static final double LIKE_WEIGHT = 0.2;
    private static final double FOLLOWED_AUTHOR_WEIGHT = 0.3;
    private static final double BLOCKED_AUTHOR_WEIGHT = 0.2;

}
