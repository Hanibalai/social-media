package ru.effectivemobile.socialmedia.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.effectivemobile.socialmedia.exception.BadRequestException;
import ru.effectivemobile.socialmedia.exception.InvitationErrorException;
import ru.effectivemobile.socialmedia.model.Invitation;
import ru.effectivemobile.socialmedia.model.User;
import ru.effectivemobile.socialmedia.repository.InvitationRepository;
import ru.effectivemobile.socialmedia.repository.UserRepository;
import ru.effectivemobile.socialmedia.web.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class UserService {
    private UserRepository userRepository;
    private InvitationRepository invitationRepository;

    public void invite(String senderUsername, String recipientUsername) {
        log.debug("Database query to save a new invitation: sender-{}, recipient-{}",
                senderUsername, recipientUsername);
        User sender = userRepository.findByUsername(senderUsername).orElseThrow(
                () -> new BadRequestException("Failed to invite: Invalid sender username"));
        User recipient = userRepository.findByUsername(recipientUsername).orElseThrow(
                () -> new BadRequestException("Failed to invite: Invalid recipient username"));
        if (recipient.equals(sender)) {
            throw new InvitationErrorException("Failed to invite: User cannot invite himself");
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
        if (!sender.getSubscriptions().contains(recipient)) {
            sender.getSubscriptions().add(recipient);
            recipient.getSubscribers().add(sender);
        }
        invitationRepository.save(invitation);
        log.debug("Invitation has been saved to the database: {}", invitation);
    }

    public void acceptInvite(String recipientUsername, String senderUsername) {
        log.debug("Database query to accept friend invitation: sender-{}, recipient-{}",
                senderUsername, recipientUsername);
        User recipient = userRepository.findByUsername(recipientUsername).orElseThrow(
                () -> new BadRequestException("Failed to accept friend invite: Invalid recipient username"));
        User sender = userRepository.findByUsername(senderUsername).orElseThrow(
                () -> new BadRequestException("Failed to accept friend invite: Invalid sender username"));
        Invitation invitation = invitationRepository
                .getInvitationByRecipientAndSender(recipient, sender)
                .orElseThrow(
                        () -> new InvitationErrorException("Failed to accept friend invite: Invitation does not exist"));
        recipient.getSubscriptions().add(sender);
        recipient.getFriends().add(sender);
        sender.getFriends().add(recipient);
        sender.getSubscribers().add(recipient);
        userRepository.save(recipient);
        userRepository.save(sender);
        invitationRepository.delete(invitation);
        log.debug("New friend: {} for user: {} has been saved", senderUsername, recipientUsername);
    }

    public void removeFriend(String username, String friendUsername) {
        log.debug("Database query to remove a friend: user-{}, friend-{}", username, friendUsername);
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new BadRequestException("Failed to remove friend: Invalid username"));
        User friend = userRepository.findByUsername(friendUsername).orElseThrow(
                () -> new BadRequestException("Failed to remove friend: Invalid friend username"));
        if (!user.getFriends().contains(friend)) {
            throw new BadRequestException("Failed to remove friend: Friend with this username does not exist");
        }
        user.getFriends().remove(friend);
        user.getSubscriptions().remove(friend);
        friend.getFriends().remove(user);
        friend.getSubscribers().remove(user);
        userRepository.save(friend);
        userRepository.save(user);
        log.debug("Friend-{} has been removed from the user-{}", friendUsername, username);
    }

    public List<UserDto> getUserFriends(String username) {
        log.debug("Database query to get a list of user friends: user-{}", username);
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new BadRequestException("Failed to get list of friend: Invalid username"));
        List<User> friendList = user.getFriends();
        log.debug("Retrieved successful");
        return friendList.stream().map(UserDto::build).collect(Collectors.toList());
    }
}
