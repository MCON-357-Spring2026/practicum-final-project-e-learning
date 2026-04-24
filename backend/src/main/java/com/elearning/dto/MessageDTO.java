package com.elearning.dto;

import com.elearning.model.Message;
import com.elearning.model.User;
import com.elearning.repository.PersonRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {

    private String id;
    private String senderId;
    private String senderName;
    private String senderEmail;
    private String receiverId;
    private String receiverName;
    private String receiverEmail;
    private String subject;
    private String body;
    private boolean read;
    private LocalDateTime sentAt;

    public MessageDTO(Message message, PersonRepository personRepo) {
        this.id = message.getId();
        this.senderId = message.getSenderId();
        this.receiverId = message.getReceiverId();
        this.subject = message.getSubject();
        this.body = message.getBody();
        this.read = message.isRead();
        this.sentAt = message.getSentAt();

        personRepo.findById(message.getSenderId()).ifPresent(person -> {
            this.senderName = person.getFirstName() + " " + person.getLastName();
            if (person instanceof User) {
                this.senderEmail = ((User) person).getEmail();
            }
        });

        personRepo.findById(message.getReceiverId()).ifPresent(person -> {
            this.receiverName = person.getFirstName() + " " + person.getLastName();
            if (person instanceof User) {
                this.receiverEmail = ((User) person).getEmail();
            }
        });
    }
}
