package ru.effectivemobile.socialmedia.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.effectivemobile.socialmedia.model.Invitation;
import ru.effectivemobile.socialmedia.model.User;
import ru.effectivemobile.socialmedia.repository.InvitationRepository;
import ru.effectivemobile.socialmedia.repository.PostRepository;
import ru.effectivemobile.socialmedia.repository.UserRepository;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;

    private PostRepository postRepository;
    private InvitationRepository invitationRepository;

    @Transactional
    public void inviteFriend(String username, String invitedFriendUsername) {

        User user = userRepository.findByUsername(username).orElse(null);
        User invitedUser = userRepository.findByUsername(invitedFriendUsername).orElse(null);
        if (user != null && invitedUser != null) {
            user.getSubscribes().add(invitedUser);
            invitedUser.getSubscribers().add(user);
            Invitation invitation = new Invitation();
            invitation.setSender(user);
            invitation.setRecipient(invitedUser);
            try {
                invitationRepository.save(invitation);
                userRepository.save(user);
                userRepository.save(invitedUser);
            } catch (Exception e) {
                throw new RuntimeException("There is an error in the request body");
            }
        } else {
            throw new RuntimeException("There is an error in the request body");
        }
    }
}
