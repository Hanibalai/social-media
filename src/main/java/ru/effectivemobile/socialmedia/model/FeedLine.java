package ru.effectivemobile.socialmedia.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "feed_line")
public class FeedLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(mappedBy = "feedLine")
    private User owner;

    @OneToMany()
    private List<Post> posts;
}
