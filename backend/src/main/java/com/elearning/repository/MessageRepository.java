package com.elearning.repository;

import com.elearning.model.Message;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

/**
 * Repository for {@link com.elearning.model.Message} entities.
 * Reserved for future implementation.
 */
public interface MessageRepository extends MongoRepository<Message, String> {

    public List<Message> findByRecipientId(String recipientId);

    public List<Message> findBySenderId(String senderId);

    public List<Message> findByRecipientIdAndRead(String recipientId, boolean read);

    public List<Message> findBySenderIdAndRead(String senderId, boolean read);

    public List<Message> findBySenderIdAndRecipientId(String senderId, String recipientId);

    public List<Message> findByListOfIds(List<String> ids);

    public List<Message> findBySentAt(LocalDateTime sentAt);

    public List<Message> findBySentAtBefore(LocalDateTime date);

    public List<Message> findBySentAtAfter(LocalDateTime date);

    @Query("{ '$or': [ { 'senderId': ?0 }, { 'receiverId': ?0 } ] }")
    public List<Message> findBySenderIdOrReceiverId(String userId);
}
