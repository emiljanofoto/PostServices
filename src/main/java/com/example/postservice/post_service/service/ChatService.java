package com.example.postservice.post_service.service;

import com.example.postservice.post_service.entity.ChatMessage;
import com.example.postservice.post_service.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    public ChatMessage sendMessage(ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        return chatMessageRepository.save(message);
    }

    public List<ChatMessage> getConversation(String recipient) {
        return chatMessageRepository.findByRecipientOrderByTimestampDesc(recipient);
    }

    public void markAsRead(Long id) {
        ChatMessage message = chatMessageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setRead(true);
        chatMessageRepository.save(message);
    }
}
