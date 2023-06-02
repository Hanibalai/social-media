package ru.effectivemobile.socialmedia.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.effectivemobile.socialmedia.exception.BadRequestException;
import ru.effectivemobile.socialmedia.exception.InvitationErrorException;
import ru.effectivemobile.socialmedia.model.Invitation;
import ru.effectivemobile.socialmedia.model.User;
import ru.effectivemobile.socialmedia.repository.InvitationRepository;
import ru.effectivemobile.socialmedia.repository.UserRepository;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;
    private InvitationRepository invitationRepository;

    @Transactional()
    public void invite(String senderUsername, String recipientUsername) {
        User sender = userRepository.findByUsername(senderUsername).orElse(null);
        if (sender == null) {
            throw new BadRequestException("Failed to invite: Invalid sender username");
        }
        User recipient = userRepository.findByUsername(recipientUsername).orElse(null);
        if (recipient == null || recipient.equals(sender)) {
            throw new BadRequestException("Failed to invite: Invalid recipient username");
        }
        if (sender.getFriends().contains(recipient)) {
            throw new InvitationErrorException("Failed to invite: Users are already friends");
        }
        Invitation invitation = new Invitation();
        invitation.setSender(sender);
        invitation.setRecipient(recipient);
        if (invitationRepository.existsBySenderAndRecipient(sender, recipient)) {
            throw new InvitationErrorException("Failed to invite: The invitation has already been sent before");
        }
        if (!sender.getSubscribes().contains(recipient)) {
            sender.getSubscribes().add(recipient);
            recipient.getSubscribers().add(sender);
        }
        invitationRepository.save(invitation);
    }

    @Transactional
    public void acceptInvite(String recipientUsername, long invitationId) {
        User recipient = userRepository.findByUsername(recipientUsername).orElse(null);
        if (recipient == null) {
            throw new BadRequestException("Failed to accept friend invite: Invalid recipient username");
        }
        Invitation invitation = invitationRepository.getInvitationById(invitationId).orElse(null);
        if (invitation == null) {
            throw new InvitationErrorException("Failed to accept friend invite: The invitation does not exist");
        }
        User sender = invitation.getSender();
        sender.getSubscribers().add(recipient);
        sender.getFriends().add(recipient);
        recipient.getFriends().add(sender);
        recipient.getSubscribes().add(sender);
        userRepository.save(recipient);
        userRepository.save(sender);
        invitationRepository.delete(invitation);
    }
}
