package com.elearning.repository;

import com.elearning.enums.Department;
import com.elearning.model.ChatConversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.List;

public interface ChatConversationRepository extends MongoRepository<ChatConversation, String> {

    List<ChatConversation> findByPersonId(String personId);

    @Query(value = "{ 'personId': ?0 }", fields = "{ '_id': 1, 'title': 1, 'subject': 1 }")
    List<ChatConversation> findAllPreviewsByPersonId(String personId);

    List<ChatConversation> findByPersonIdAndSubject(String personId, Department subject);

    @Query("{ '_id': ?0 }")
    @Update("{ '$set': { 'title': ?1 } }")
    long renameById(String id, String title);
}
