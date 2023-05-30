package ru.effectivemobile.socialmedia.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
public class SignupRequest {
    private String username;
    private String email;
    private Set<String> roles;
    private String password;
}
