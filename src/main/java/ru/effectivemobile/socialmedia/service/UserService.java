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
import ru.effectivemobile.socialmedia.web.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class UserService {
    private UserRepository userRepository;
    private InvitationRepository invitationRepository;

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

    public void acceptInvite(String recipientUsername, long invitationId) {
        User recipient = userRepository.findByUsername(recipientUsername).orElse(null);
        if (recipient == null) {
            throw new BadRequestException("Failed to accept friend invite: Invalid recipient username");
        }
        Invitation invitation = invitationRepository.getInvitationById(invitationId).orElse(null);
        if (invitation == null || !recipient.getInvitations().contains(invitation)) {
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

    public void removeFriend(String username, String friendUsername) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throw new BadRequestException("Failed to remove user from friends: Invalid username");
        }
        User friend = userRepository.findByUsername(friendUsername).orElse(null);
        if (friend == null || friend.equals(user) || !user.getFriends().contains(friend)) {
            throw new BadRequestException("Failed to remove friend: Friend with this username does not exist");
        }
        user.getFriends().remove(friend);
        user.getSubscribes().remove(friend);
        friend.getFriends().remove(user);
        friend.getSubscribers().remove(user);
        userRepository.save(friend);
        userRepository.save(user);
    }

    public List<UserDto> getUserFriends(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throw new BadRequestException("Failed to get list of friend: Invalid username");
        }
        List<User> friendList = user.getFriends();
        return friendList.stream().map(UserDto::build).collect(Collectors.toList());
    }
}
