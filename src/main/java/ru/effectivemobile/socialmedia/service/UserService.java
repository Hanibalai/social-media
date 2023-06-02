package ru.effectivemobile.socialmedia.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.effectivemobile.socialmedia.exception.BadRequestException;
import ru.effectivemobile.socialmedia.exception.InvitationException;
import ru.effectivemobile.socialmedia.model.Invitation;
import ru.effectivemobile.socialmedia.model.User;
import ru.effectivemobile.socialmedia.repository.InvitationRepository;
import ru.effectivemobile.socialmedia.repository.PostRepository;
import ru.effectivemobile.socialmedia.repository.UserRepository;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;
    private InvitationRepository invitationRepository;

    @Transactional()
    public void inviteFriend(String username, String invitedFriendUsername) {

        User sender = userRepository.findByUsername(username).orElse(null);
        if (sender == null) {
            throw new BadRequestException("Failed to invite: Invalid sender username");
        }
        User recipient = userRepository.findByUsername(invitedFriendUsername).orElse(null);
        if (recipient == null) {
            throw new BadRequestException("Failed to invite: Invalid recipient username");
        }
        Invitation invitation = new Invitation();
        invitation.setSender(sender);
        invitation.setRecipient(recipient);
        if (invitationRepository.existsBySenderAndRecipient(sender, recipient)) {
            throw new InvitationException("Failed to invite: This invitation has already been sent before");
        }
        sender.getSubscribes().add(recipient);
        recipient.getSubscribers().add(sender);
        invitationRepository.save(invitation);
    }
}
