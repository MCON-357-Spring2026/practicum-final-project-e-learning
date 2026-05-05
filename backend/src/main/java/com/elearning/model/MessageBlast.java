package com.elearning.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Represents a message blast — a single send action that creates
 * multiple individual {@link Message} records for each recipient.
 * Stored in the "message_blasts" MongoDB collection.
 */
@Document(collection = "message_blasts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageBlast {

    @Id
    private String id;

    private String senderId;
    private ArrayList<String> messageIds;
    private String subject;
    private LocalDateTime sentAt;
}
