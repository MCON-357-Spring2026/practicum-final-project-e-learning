package com.elearning.controller;

import com.elearning.dto.MessageBlastDTO;
import com.elearning.dto.MessageDTO;
import com.elearning.dto.SendBlastDTO;
import com.elearning.model.Message;
import com.elearning.model.MessageBlast;
import com.elearning.repository.PersonRepository;
import com.elearning.security.AuthenticatedUser;
import com.elearning.service.MessageBlastService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/message-blasts")
public class MessageBlastController {

    private final MessageBlastService blastService;
    private final PersonRepository personRepo;

    public MessageBlastController(MessageBlastService blastService, PersonRepository personRepo) {
        this.blastService = blastService;
        this.personRepo = personRepo;
    }

    private MessageBlastDTO toDTO(MessageBlast blast) {
        List<Message> messages = blastService.getBlastMessages(blast.getId());
        return new MessageBlastDTO(blast, messages, personRepo);
    }

    /**
     * Creates a message blast. The recipients field can be:
     * "teachers", "admin", "teachers and admin", or a course ID.
     */
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @PostMapping("/")
    public ResponseEntity<?> createBlast(@RequestBody SendBlastDTO request) {
        AuthenticatedUser principal = (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            MessageBlast blast = blastService.createBlastFromString(
                    principal.id(),
                    request.getRecipients(),
                    request.getSubject(),
                    request.getBody()
            );
            return ResponseEntity.ok(toDTO(blast));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Retrieves all blasts sent by the authenticated user.
     */
    @GetMapping("/")
    public ResponseEntity<List<MessageBlastDTO>> getMyBlasts() {
        AuthenticatedUser principal = (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(blastService.getBySenderId(principal.id()).stream().map(this::toDTO).toList());
    }

    /**
     * Retrieves a blast by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MessageBlastDTO> getBlastById(@PathVariable String id) {
        return blastService.getById(id)
                .map(this::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Retrieves all individual messages belonging to a blast.
     */
    @GetMapping("/{id}/messages")
    public ResponseEntity<List<MessageDTO>> getBlastMessages(@PathVariable String id) {
        List<Message> messages = blastService.getBlastMessages(id);
        List<MessageDTO> dtos = messages.stream()
                .map(m -> new MessageDTO(m, personRepo))
                .toList();
        return ResponseEntity.ok(dtos);
    }

    /**
     * Deletes a blast (does not delete individual messages).
     */
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlast(@PathVariable String id) {
        blastService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
