package ru.effectivemobile.socialmedia.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Data
@Entity
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user")
    private String username;

    @Column(name = "text")
    private String text;

    @Column(name = "header", length = 50)
    private String header;

    @Column(name = "image")
    private byte[] image;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_time")
    private Date creationTime;

    @PrePersist
    private void onCreate() {
        creationTime = new Date();
    }
}
