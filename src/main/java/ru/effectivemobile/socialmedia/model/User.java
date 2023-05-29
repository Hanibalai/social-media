package ru.effectivemobile.socialmedia.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "username", nullable = false, unique = true, length = 30)
    @NotEmpty(message = "Username should not be empty")
    @Size(min = 2, max = 30, message = "Username should be between 2 and 30 characters")
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 45)
    @Email
    @NotEmpty(message = "Email should not be empty")
    @Size(max = 45, message = "Email should not be greater than 45 characters")
    private String email;

    @Column(name = "password", nullable = false, length = 30)
    @NotEmpty(message = "Password should not be empty")
    @Size(max = 30, message = "Password should not be greater than 30 characters")
    private String password;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "user_post",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id"))
    private List<Post> postList;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_subscriber",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "subscriber_id", referencedColumnName = "id"))
    private Set<User> subscribers;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_subscribe",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "subscribe_id", referencedColumnName = "id"))
    private Set<User> subscribes;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_friend",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "friend_id", referencedColumnName = "id", nullable = false))
    private Set<User> friends;

}
