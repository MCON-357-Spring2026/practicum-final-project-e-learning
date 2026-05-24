package com.elearning.controller;

import com.elearning.dto.ChatPreviewDTO;
import com.elearning.enums.Department;
import com.elearning.model.ChatConversation;
import com.elearning.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for chat conversation endpoints at {@code /api/chats}.
 * Provides CRUD operations and custom endpoints for previews and chat flow.
 */
@RestController
@RequestMapping("/api/chats")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Retrieves all chat conversations.
     *
     * @return 200 with list of all conversations
     */
    @GetMapping("/")
    public ResponseEntity<List<ChatConversation>> getAllChats() {
        return ResponseEntity.ok(chatService.getAll());
    }

    /**
     * Retrieves a chat conversation by ID.
     *
     * @param id the conversation ID
     * @return 200 with conversation, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ChatConversation> getChatById(@PathVariable String id) {
        return chatService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Creates a new chat conversation.
     *
     * @param conversation the conversation to create
     * @return 200 with created conversation
     */
    @PostMapping("/")
    public ResponseEntity<ChatConversation> createChat(@RequestBody ChatConversation conversation) {
        return ResponseEntity.ok(chatService.create(conversation));
    }

    /**
     * Partially updates a chat conversation.
     *
     * @param id           the conversation ID
     * @param conversation fields to update
     * @return 200 with updated conversation, or 404 if not found
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ChatConversation> updateChat(@PathVariable String id, @RequestBody ChatConversation conversation) {
        return chatService.update(id, conversation)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Fully replaces a chat conversation.
     *
     * @param id           the conversation ID
     * @param conversation replacement conversation data
     * @return 200 with replaced conversation, or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<ChatConversation> replaceChat(@PathVariable String id, @RequestBody ChatConversation conversation) {
        return chatService.replace(id, conversation)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deletes a chat conversation by ID.
     *
     * @param id the conversation ID
     * @return 204 no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChat(@PathVariable String id) {
        chatService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves all conversations for a person.
     *
     * @param personId the person ID
     * @return 200 with list of conversations
     */
    @PreAuthorize("#personId == authentication.principal.id or hasRole('ADMIN')")
    @GetMapping("/person/{personId}")
    public ResponseEntity<List<ChatConversation>> getChatsByPersonId(@PathVariable String personId) {
        return ResponseEntity.ok(chatService.getByPersonId(personId));
    }

    /**
     * Retrieves all conversations for a person filtered by subject.
     *
     * @param personId the person ID
     * @param subject  the subject filter
     * @return 200 with list of matching conversations
     */
    @PreAuthorize("#personId == authentication.principal.id or hasRole('ADMIN')")
    @GetMapping("/person/{personId}/subject")
    public ResponseEntity<List<ChatConversation>> getChatsByPersonIdAndSubject(
            @PathVariable String personId,
            @RequestParam Department subject) {
        return ResponseEntity.ok(chatService.getByPersonIdAndSubject(personId, subject));
    }

    /**
     * Retrieves all chat previews for a person.
     *
     * @param personId the person ID
     * @return 200 with list of chat previews
     */
    @PreAuthorize("#personId == authentication.principal.id or hasRole('ADMIN')")
    @GetMapping("/person/{personId}/previews")
    public ResponseEntity<List<ChatPreviewDTO>> getAllPreviewsByPersonId(@PathVariable String personId) {
        return ResponseEntity.ok(chatService.getAllPreviewsByPersonId(personId));
    }

    /**
     * Starts a new chat conversation for a person.
     *
     * @param personId the person ID
     * @param body     request body with subject, systemMessage, and userMessage
     * @return 200 with created conversation, or 400 on invalid request
     */
    @PreAuthorize("#personId == authentication.principal.id or hasRole('ADMIN')")
    @PostMapping("/person/{personId}/start")
    public ResponseEntity<?> startConversation(@PathVariable String personId, @RequestBody Map<String, String> body) {
        String subjectStr = body.get("subject");
        String userMessage = body.get("userMessage");

        if (subjectStr == null || userMessage == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "subject and userMessage are required"
            ));
        }

        Department subject;
        try {
            subject = Department.valueOf(subjectStr);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid department: " + subjectStr));
        }

        String subjectLabel = subject.name().replace('_', ' ').toLowerCase();
        String systemMessage = "You are a helpful " + subjectLabel + " tutor. You give guidance to students by "
                + "answering their questions and explaining the relevant topics clearly, simply and concisely."
                + " If you don't know the answer, say you don't know. Always be polite and encouraging. If asked about"
                + " anything unrelated to " + subjectLabel + ", politely explain that you can only help with " + subjectLabel + " topics.";

        return ResponseEntity.ok(chatService.start(personId, subject, systemMessage, userMessage));
    }

    /**
     * Renames a chat conversation.
     *
     * @param id    the conversation ID
     * @param title the new title
     * @return 200 with renamed conversation, or 404 if not found
     */
    @PatchMapping("/{id}/rename")
    public ResponseEntity<ChatConversation> renameChat(@PathVariable String id, @RequestBody String title) {
        return chatService.rename(id, title)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Sends a user message in an existing conversation.
     *
     * @param conversationId the conversation ID
     * @param body           request body with userMessage
     * @return 200 with updated conversation, 400 on invalid request, or 404 if not found
     */
    @PatchMapping("/{conversationId}/message")
    public ResponseEntity<?> messageConversation(@PathVariable String conversationId, @RequestBody Map<String, String> body) {
        String userMessage = body.get("userMessage");
        if (userMessage == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "userMessage is required"));
        }

        return chatService.message(conversationId, userMessage)
                .map(updated -> ResponseEntity.ok((Object) updated))
                .orElse(ResponseEntity.notFound().build());
    }
}