package ru.effectivemobile.socialmedia.dto;

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
