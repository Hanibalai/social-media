package ru.effectivemobile.socialmedia.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "text")
    private String text;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "sender")
    private User sender;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "recipient")
    private User recipient;
}
