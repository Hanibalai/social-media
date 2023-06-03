package ru.effectivemobile.socialmedia.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.effectivemobile.socialmedia.exception.BadRequestException;
import ru.effectivemobile.socialmedia.exception.MessageErrorException;
import ru.effectivemobile.socialmedia.model.Message;
import ru.effectivemobile.socialmedia.model.User;
import ru.effectivemobile.socialmedia.repository.MessageRepository;
import ru.effectivemobile.socialmedia.repository.UserRepository;
import ru.effectivemobile.socialmedia.web.dto.MessageDto;

@Service
@Transactional
@AllArgsConstructor
public class MessageService {
    private UserRepository userRepository;
    private MessageRepository messageRepository;

    @Transactional
    public void sendMessage(String senderUsername, String recipientUsername, MessageDto messageDto) {
        User sender = userRepository.findByUsername(senderUsername).orElse(null);
        if (sender == null) {
            throw new BadRequestException("Failed to send message: Invalid sender username");
        }
        User recipient = userRepository.findByUsername(recipientUsername).orElse(null);
        if (recipient == null) {
            throw new BadRequestException("Failed to send message: Invalid recipient username");
        }
        System.out.println(messageDto.getText());
        if (!sender.getFriends().contains(recipient)) {
            throw new MessageErrorException("Failed to send message: Users are not friends");
        }
        System.out.println(messageDto.getText());
        Message message = new Message();
        message.setText(messageDto.getText());
        message.setSender(sender);
        message.setRecipient(recipient);
        messageRepository.save(message);
    }
}
