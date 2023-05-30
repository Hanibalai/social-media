package ru.effectivemobile.socialmedia.pojo;

import lombok.Getter;
import lombok.Setter;
import ru.effectivemobile.socialmedia.model.Role;

import java.util.List;

@Getter
@Setter
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private List<Role> roles;

    public JwtResponse(String token, Long id, String username, String email, List<Role> roles) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}
