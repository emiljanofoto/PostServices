package com.example.postservice.post_service.controller;

import com.example.postservice.post_service.entity.ChatMessage;
import com.example.postservice.post_service.service.AIChatService;
import com.example.postservice.post_service.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;       // Existing chat handling (e.g., storing user messages)
    @Autowired
    private AIChatService aiChatService;   // AI logic for generating responses

    /**
     * Endpoint to send a user message.
     * If the sender is the user, attempt an AI reply.
     */
    @PostMapping("/send")
    public ChatMessage sendMessage(@RequestBody ChatMessage message) {
        // 1. Save user message to DB
        ChatMessage userMessage = chatService.sendMessage(message);

        // 2. If the message is from a user (role 'USER'), generate AI reply
        if ("USER".equalsIgnoreCase(userMessage.getSender())) {
            String aiResponse = aiChatService.getAIResponse(userMessage.getMessage());
            ChatMessage aiMessage = new ChatMessage();
            aiMessage.setSender("AI"); // Or "SYSTEM", etc.
            aiMessage.setRecipient(userMessage.getSender());
            aiMessage.setMessage(aiResponse);
            chatService.sendMessage(aiMessage);

            // Optionally return both messages (user + AI response) if desired
        }

        // Return the user’s message or the entire chat state
        return userMessage;
    }

    /**
     * Endpoint to retrieve the conversation history for a given participant.
     */
    @GetMapping("/history")
    public List<ChatMessage> getConversation(@RequestParam String participant) {
        return chatService.getConversation(participant);
    }

    /**
     * Endpoint to mark a message as read.
     */
    @PutMapping("/read/{messageId}")
    public void markMessageAsRead(@PathVariable Long messageId) {
        chatService.markAsRead(messageId);
    }
}