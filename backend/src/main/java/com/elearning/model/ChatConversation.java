package com.elearning.model;

import com.elearning.enums.Department;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Document(collection = "conversations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatConversation {

    @Id
    private String conversationId;

    private String personId;

    private String title;

    private Department subject;

    @Builder.Default
    private ArrayList<ChatMessage> messages = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessage {
        private String message;
        private Sender from;
        private LocalDateTime time;
    }

    public enum Sender {
        SYSTEM,
        AGENT,
        USER
    }
}
