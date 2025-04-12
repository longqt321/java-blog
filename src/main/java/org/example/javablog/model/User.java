package org.example.javablog.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name ="users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String firstName;

    @Column(nullable = false, length = 10)
    private String lastName;

    @Column(length = 20, unique = true,nullable = false)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(updatable = false)
    @CreationTimestamp
    private Timestamp createdAt;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private Set<Post> posts = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

}


