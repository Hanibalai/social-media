package ru.effectivemobile.socialmedia.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.effectivemobile.socialmedia.model.Post;
import ru.effectivemobile.socialmedia.model.User;
import ru.effectivemobile.socialmedia.repository.PostRepository;
import ru.effectivemobile.socialmedia.repository.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;

    private PostRepository postRepository;

//    public void saveUser(UserDetailsImpl userDto) {
//        Optional<User> optionalUser = userRepository.findByEmail(userDto.getEmail());
//        if (optionalUser.isPresent()) {
//            User user = optionalUser.get();
//            userRepository.save(user);
//        }
//    }
//
//    public User getUser(String email) {
//        Optional<User> optionalUser = userRepository.findByEmail(email);
//        return optionalUser.orElse(null);
//    }
//
//    public List<User> getAllUsers() {
//        return userRepository.findAll();
//    }

    public List<Post> getUserPosts(User user) {
        return postRepository.findPostByUserOrderById(user);
    }

    public User getUser(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }


}
