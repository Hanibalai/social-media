package ru.effectivemobile.socialmedia.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.effectivemobile.socialmedia.exception.BadRequestException;
import ru.effectivemobile.socialmedia.exception.MessageErrorException;
import ru.effectivemobile.socialmedia.model.Message;
import ru.effectivemobile.socialmedia.model.User;
import ru.effectivemobile.socialmedia.repository.MessageRepository;
import ru.effectivemobile.socialmedia.repository.UserRepository;
import ru.effectivemobile.socialmedia.web.dto.MessageDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class MessageService {
    private UserRepository userRepository;
    private MessageRepository messageRepository;

    public MessageDto sendMessage(String senderUsername, String recipientUsername, MessageDto messageDto) {
        User sender = userRepository.findByUsername(senderUsername).orElse(null);
        if (sender == null) {
            throw new BadRequestException("Failed to send message: Invalid sender username");
        }
        User recipient = userRepository.findByUsername(recipientUsername).orElse(null);
        if (recipient == null) {
            throw new BadRequestException("Failed to send message: Invalid recipient username");
        }
        if (!sender.getFriends().contains(recipient)) {
            throw new MessageErrorException("Failed to send message: Users are not friends");
        }
        Message message = new Message();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setText(messageDto.getText());
        messageRepository.save(message);
        return MessageDto.build(message);
    }

    public List<MessageDto> getSentMessages(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throw new BadRequestException("Failed to get list of messages: Invalid username");
        }
        List<Message> messageList = messageRepository.getMessagesBySenderOrderByIdDesc(user);
        return messageList.stream().map(MessageDto::build).collect(Collectors.toList());
    }

    public List<MessageDto> getReceivedMessages(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throw new BadRequestException("Failed to get list of messages: Invalid username");
        }
        List<Message> messageList = messageRepository.getMessagesByRecipientOrderByIdDesc(user);
        return messageList.stream().map(MessageDto::build).collect(Collectors.toList());
    }

}
