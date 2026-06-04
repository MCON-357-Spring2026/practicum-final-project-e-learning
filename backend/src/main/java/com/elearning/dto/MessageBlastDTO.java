package com.elearning.dto;

import com.elearning.model.Message;
import com.elearning.model.MessageBlast;
import com.elearning.model.Person;
import com.elearning.model.User;
import com.elearning.repository.PersonRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageBlastDTO {

    private String id;
    private String subject;
    private String body;
    private LocalDateTime sentAt;
    private BasicUserDTO sender;
    private ArrayList<BasicUserDTO> recipients;

    public MessageBlastDTO(MessageBlast blast, List<Message> messages, PersonRepository personRepo) {
        this.id = blast.getId();
        this.subject = blast.getSubject();
        this.body = messages.stream()
                .findFirst()
                .map(Message::getBody)
                .orElse("");
        this.sentAt = blast.getSentAt();
        this.sender = personRepo.findById(blast.getSenderId())
                .map(BasicUserDTO::new)
                .orElse(null);
        this.recipients = messages.stream()
                .map(Message::getReceiverId)
                .distinct()
                .map(personRepo::findById)
                .flatMap(java.util.Optional::stream)
                .map(BasicUserDTO::new)
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
    }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BasicUserDTO {
        private String personId;
        private String name;
        private String email;

        public BasicUserDTO(Person person) {
            this.personId = person.getId();
            this.name = person.getFirstName() + " " + person.getLastName();
            this.email = person instanceof User user ? user.getEmail() : null;
        }
    }
}