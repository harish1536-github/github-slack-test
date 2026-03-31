package com.example.tracker.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Commit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "commit_id", unique = true)
    private String commitId;

    @Column(name = "message", length = 1000)
    private String message;

    @Column(name = "timestamp")
    private String timestamp;

    @Column(name = "committer_name")
    private String committerName;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;
}