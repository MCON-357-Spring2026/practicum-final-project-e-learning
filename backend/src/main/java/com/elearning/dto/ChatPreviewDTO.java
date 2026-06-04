package com.elearning.dto;

import com.elearning.enums.Department;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import com.elearning.model.ChatConversation;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatPreviewDTO {

    private String conversationId;
    private String title;
    private Department subject;

    public ChatPreviewDTO(ChatConversation conversation) {
        this.conversationId = conversation.getConversationId();
        this.title = conversation.getTitle();
        this.subject = conversation.getSubject();
    }

}