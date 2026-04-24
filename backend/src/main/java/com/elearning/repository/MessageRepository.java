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

    public List<Message> findByReceiverId(String receiverId);

    public List<Message> findBySenderId(String senderId);

    public List<Message> findByReceiverIdAndRead(String receiverId, boolean read);

    public List<Message> findBySenderIdAndRead(String senderId, boolean read);

    public List<Message> findBySenderIdAndReceiverId(String senderId, String receiverId);

    public List<Message> findByIdIn(List<String> ids);

    public List<Message> findBySentAt(LocalDateTime sentAt);

    public List<Message> findBySentAtBefore(LocalDateTime date);

    public List<Message> findBySentAtAfter(LocalDateTime date);

    @Query("{ '$or': [ { 'senderId': ?0 }, { 'receiverId': ?0 } ] }")
    public List<Message> findBySenderIdOrReceiverId(String userId);
}
