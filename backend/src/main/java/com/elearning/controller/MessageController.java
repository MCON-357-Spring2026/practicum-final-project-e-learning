package com.elearning.controller;

import com.elearning.dto.MessageDTO;
import com.elearning.model.Message;
import com.elearning.repository.PersonRepository;
import com.elearning.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for internal messaging endpoints at {@code /api/messages}.
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;
    private final PersonRepository personRepo;

    public MessageController(MessageService messageService, PersonRepository personRepo) {
        this.messageService = messageService;
        this.personRepo = personRepo;
    }

    private MessageDTO toDTO(Message message) {
        return new MessageDTO(message, personRepo);
    }

    private List<MessageDTO> toDTOList(List<Message> messages) {
        return messages.stream().map(this::toDTO).toList();
    }

    /**
     * Retrieves all messages.
     *
     * @return 200 with list of all messages
     */
    @GetMapping("/")
    public ResponseEntity<List<MessageDTO>> getAllMessages() {
        return ResponseEntity.ok(toDTOList(messageService.getAll()));
    }

    /**
     * Retrieves a message by its ID.
     *
     * @param id the message ID
     * @return 200 with the message, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<MessageDTO> getMessageById(@PathVariable String id) {
        return messageService.getById(id)
                .map(m -> ResponseEntity.ok(toDTO(m)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Creates a new message.
     *
     * @param message the message to send
     * @return 200 with the created message
     */
    @PostMapping("/")
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        return ResponseEntity.ok(messageService.create(message));
    }

    /**
     * Partially updates an existing message.
     *
     * @param id      the message ID
     * @param message the fields to update
     * @return 200 with the updated message, or 404 if not found
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Message> updateMessage(@PathVariable String id, @RequestBody Message message) {
        return messageService.update(id, message)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Fully replaces an existing message.
     *
     * @param id      the message ID
     * @param message the replacement message
     * @return 200 with the replaced message, or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Message> replaceMessage(@PathVariable String id, @RequestBody Message message) {
        return messageService.replace(id, message)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deletes a message by its ID.
     *
     * @param id the message ID
     * @return 204 no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable String id) {
        messageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves all messages sent before the given date/time.
     *
     * @param date the cutoff date/time (ISO format, e.g. 2026-04-16T12:00:00)
     * @return 200 with list of messages
     */
    @GetMapping("/sent-before")
    public ResponseEntity<List<MessageDTO>> getSentBefore(@RequestParam LocalDateTime date) {
        return ResponseEntity.ok(toDTOList(messageService.getSentBefore(date)));
    }

    /**
     * Retrieves all messages sent after the given date/time.
     *
     * @param date the cutoff date/time (ISO format, e.g. 2026-04-16T12:00:00)
     * @return 200 with list of messages
     */
    @GetMapping("/sent-after")
    public ResponseEntity<List<MessageDTO>> getSentAfter(@RequestParam LocalDateTime date) {
        return ResponseEntity.ok(toDTOList(messageService.getSentAfter(date)));
    }

    /**
     * Retrieves all messages where the user is either the sender or receiver.
     *
     * @param userId the user's ID
     * @return 200 with list of messages
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MessageDTO>> getByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(toDTOList(messageService.getByUserId(userId)));
    }

    /**
     * Retrieves all messages received by a given user.
     *
     * @param recipientId the recipient's ID
     * @return 200 with list of messages
     */
    @GetMapping("/recipient/{recipientId}")
    public ResponseEntity<List<MessageDTO>> getByRecipientId(@PathVariable String recipientId) {
        return ResponseEntity.ok(toDTOList(messageService.getByRecipientId(recipientId)));
    }

    /**
     * Retrieves all messages sent by a given user.
     *
     * @param senderId the sender's ID
     * @return 200 with list of messages
     */
    @GetMapping("/sender/{senderId}")
    public ResponseEntity<List<MessageDTO>> getBySenderId(@PathVariable String senderId) {
        return ResponseEntity.ok(toDTOList(messageService.getBySenderId(senderId)));
    }

    /**
     * Retrieves all messages between a specific sender and recipient.
     *
     * @param senderId    the sender's ID
     * @param recipientId the recipient's ID
     * @return 200 with list of messages
     */
    @GetMapping("/conversation")
    public ResponseEntity<List<MessageDTO>> getBySenderAndRecipient(
            @RequestParam String senderId, @RequestParam String recipientId) {
        return ResponseEntity.ok(toDTOList(messageService.getBySenderAndRecipient(senderId, recipientId)));
    }

    /**
     * Retrieves all messages between two users (both directions), sorted latest first.
     *
     * @param personId1 the first user's ID
     * @param personId2 the second user's ID
     * @return 200 with list of messages
     */
    @GetMapping("/conversation/between")
    public ResponseEntity<List<MessageDTO>> getConversation(
            @RequestParam String personId1, @RequestParam String personId2) {
        return ResponseEntity.ok(toDTOList(messageService.getConversation(personId1, personId2)));
    }
}
