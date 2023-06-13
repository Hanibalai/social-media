package ru.effectivemobile.socialmedia.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.effectivemobile.socialmedia.model.ERole;
import ru.effectivemobile.socialmedia.model.Role;
import ru.effectivemobile.socialmedia.model.User;
import ru.effectivemobile.socialmedia.repository.RoleRepository;
import ru.effectivemobile.socialmedia.repository.UserRepository;
import ru.effectivemobile.socialmedia.security.UserDetailsImpl;
import ru.effectivemobile.socialmedia.security.jwt.JwtUtils;
import ru.effectivemobile.socialmedia.web.dto.request.LoginRequest;
import ru.effectivemobile.socialmedia.web.dto.request.SignupRequest;
import ru.effectivemobile.socialmedia.web.dto.response.JwtResponse;
import ru.effectivemobile.socialmedia.web.dto.response.MessageResponse;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Authentication Controller", description = "Registration and authorization")
public class AuthController {
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtils jwtUtils;

    @PostMapping("/signin")
    @Operation(
            summary = "User authorization",
            description = "Allows the user to login. " +
                    "It accepts as input a LoginRequest object with two fields in the String format: " +
                    "Username and password. " +
                    "Returns a JwtResponse object containing the JWT token in string format " +
                    "and the data of an authorized user"
    )
    public ResponseEntity<?> authUser(@RequestBody @Valid LoginRequest loginRequest) {
        log.info("New authorization request from user: {}", loginRequest.getUsername());
        String jwt;
        UserDetailsImpl userDetails;
        List<String> roles;
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            jwt = jwtUtils.generateJwtToken(authentication);

            userDetails = (UserDetailsImpl) authentication.getPrincipal();
            roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
        } catch (AuthenticationException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
        log.info("Authorization was successful");
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/signup")
    @Operation(
            summary = "User registration",
            description = "Allows the user to register. " +
                    "It accepts as input an object of the SignupRequest class containing four fields " +
                    "in String format: Username, Email, List of user roles and Password. " +
                    "Performs validation and returns result in String format nested in MessageResponse object"
    )
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {
        log.info("New registration request from client");
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            log.warn("Username-{} exists", signupRequest.getUsername());
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username exists"));
        }
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            log.warn("Email-{} exists", signupRequest.getEmail());
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email exists"));
        }
        User user = new User();

        try {
            user.setUsername(signupRequest.getUsername());
            user.setEmail(signupRequest.getEmail());
            user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }

        Set<String> requestRoles = signupRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (requestRoles == null) {
            Role userRole = roleRepository
                    .findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error, Role USER is not found"));
            roles.add(userRole);
        } else {
            requestRoles.forEach(r -> {
                switch (r) {
                    case "admin" -> {
                        Role adminRole = roleRepository
                                .findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error, Role ADMIN is not found"));
                        roles.add(adminRole);
                    }
                    case "mod" -> {
                        Role modRole = roleRepository
                                .findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error, Role MODERATOR is not found"));
                        roles.add(modRole);
                    }
                    default -> {
                        Role userRole = roleRepository
                                .findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error, Role USER is not found"));
                        roles.add(userRole);
                    }
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        log.info("Registration was successful. New User saved to the database: {}", user.getUsername());
        return ResponseEntity.ok(new MessageResponse("User has been successfully created"));
    }
}
