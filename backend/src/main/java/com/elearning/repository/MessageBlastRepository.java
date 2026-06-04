package com.elearning.repository;

import com.elearning.model.MessageBlast;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageBlastRepository extends MongoRepository<MessageBlast, String> {

    List<MessageBlast> findBySenderId(String senderId);
}
