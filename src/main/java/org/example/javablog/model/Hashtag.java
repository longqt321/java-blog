package org.example.javablog.model;


import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "hashtags")
public class Hashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "hashtags")
    private Set<Post> posts;
}
