package com.elearning.service;

import com.elearning.model.Message;
import com.elearning.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing internal messages between users.
 */
@Service
public class MessageService implements ServiceInterface<Message> {

    private final MessageRepository repo;

    public MessageService(MessageRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<Message> getAll() {
        return repo.findAll();
    }

    @Override
    public Optional<Message> getById(String id) {
        return repo.findById(id);
    }

    @Override
    public Message create(Message message) {
        message.setSentAt(LocalDateTime.now());
        message.setRead(false);
        return repo.save(message);
    }

    @Override
    public Optional<Message> update(String id, Message message) {
        Optional<Message> existing = repo.findById(id);
        if (existing.isPresent()) {
            Message updated = existing.get();
            if (message.getSubject() != null) {
                updated.setSubject(message.getSubject());
            }
            if (message.getBody() != null) {
                updated.setBody(message.getBody());
            }
            updated.setRead(message.isRead());
            return Optional.of(repo.save(updated));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Message> replace(String id, Message message) {
        if (repo.existsById(id)) {
            message.setId(id);
            return Optional.of(repo.save(message));
        }
        return Optional.empty();
    }

    @Override
    public boolean delete(String id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Message> getByRecipientId(String recipientId) {
        return repo.findByRecipientId(recipientId);
    }

    public List<Message> getBySenderId(String senderId) {
        return repo.findBySenderId(senderId);
    }

    public List<Message> getBySenderAndRecipient(String senderId, String recipientId) {
        return repo.findBySenderIdAndRecipientId(senderId, recipientId);
    }

    public List<Message> getSentBefore(LocalDateTime date) {
        return repo.findBySentAtBefore(date);
    }

    public List<Message> getSentAfter(LocalDateTime date) {
        return repo.findBySentAtAfter(date);
    }

    public List<Message> getByUserId(String userId) {
        return repo.findBySenderIdOrReceiverId(userId);
    }

    public List<Message> getConversation(String personId1, String personId2) {
        List<Message> combined = new ArrayList<>();
        combined.addAll(repo.findBySenderIdAndRecipientId(personId1, personId2));
        combined.addAll(repo.findBySenderIdAndRecipientId(personId2, personId1));
        combined.sort(Comparator.comparing(Message::getSentAt).reversed());
        return combined;
    }
}
