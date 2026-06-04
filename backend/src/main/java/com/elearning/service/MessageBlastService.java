package com.elearning.service;

import com.elearning.enums.Role;
import com.elearning.model.Enrollment;
import com.elearning.model.Message;
import com.elearning.model.MessageBlast;
import com.elearning.model.User;
import com.elearning.repository.CourseRepository;
import com.elearning.repository.EnrollmentRepository;
import com.elearning.repository.MessageBlastRepository;
import com.elearning.repository.MessageRepository;
import com.elearning.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MessageBlastService {

    private final MessageBlastRepository blastRepo;
    private final MessageRepository messageRepo;
    private final PersonRepository personRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final CourseRepository courseRepo;

    public MessageBlastService(MessageBlastRepository blastRepo, MessageRepository messageRepo,
                               PersonRepository personRepo, EnrollmentRepository enrollmentRepo,
                               CourseRepository courseRepo) {
        this.blastRepo = blastRepo;
        this.messageRepo = messageRepo;
        this.personRepo = personRepo;
        this.enrollmentRepo = enrollmentRepo;
        this.courseRepo = courseRepo;
    }

    public List<MessageBlast> getAll() {
        return blastRepo.findAll();
    }

    public Optional<MessageBlast> getById(String id) {
        return blastRepo.findById(id);
    }

    public List<MessageBlast> getBySenderId(String senderId) {
        return blastRepo.findBySenderId(senderId);
    }

    /**
     * Resolves a recipient string to a list of user IDs.
     * Checks (case-insensitive): "teachers", "admin", "teachers and admin",
     * otherwise treats input as a courseId and returns enrolled student IDs.
     *
     * @param recipients the recipient descriptor string
     * @return list of resolved user IDs
     * @throws IllegalArgumentException if the string is not a recognized group or valid course ID
     */
    public List<String> resolveRecipients(String recipients) {
        String normalized = recipients.trim().toLowerCase();
        List<String> ids = new ArrayList<>();

        if (normalized.equals("teachers")) {
            List<User> teachers = personRepo.findByRole(Role.TEACHER);
            for (User u : teachers) ids.add(u.getId());
        } else if (normalized.equals("admin")) {
            List<User> admins = personRepo.findByRole(Role.ADMIN);
            for (User u : admins) ids.add(u.getId());
        } else if (normalized.equals("teachers and admin")) {
            List<User> teachers = personRepo.findByRole(Role.TEACHER);
            List<User> admins = personRepo.findByRole(Role.ADMIN);
            for (User u : teachers) ids.add(u.getId());
            for (User u : admins) {
                if (!ids.contains(u.getId())) ids.add(u.getId());
            }
        } else {
            // Treat as a course ID — get enrolled students
            if (!courseRepo.existsById(recipients.trim())) {
                throw new IllegalArgumentException("Invalid recipient: not a recognized group or course ID");
            }
            List<Enrollment> enrollments = enrollmentRepo.findByCourseId(recipients.trim());
            for (Enrollment e : enrollments) ids.add(e.getStudentId());
        }
        return ids;
    }

    /**
     * Creates a blast by resolving the recipients string and sending individual messages.
     */
    public MessageBlast createBlastFromString(String senderId, String recipients, String subject, String body) {
        List<String> recipientIds = resolveRecipients(recipients);
        if (recipientIds.isEmpty()) {
            throw new IllegalArgumentException("No recipients found for: " + recipients);
        }
        return createBlast(senderId, recipientIds, subject, body);
    }

    /**
     * Creates a blast by sending individual messages to each recipient.
     *
     * @param senderId     the sender's ID
     * @param recipientIds list of recipient IDs
     * @param subject      the message subject
     * @param body         the message body
     * @return the saved MessageBlast with all message IDs
     */
    public MessageBlast createBlast(String senderId, List<String> recipientIds, String subject, String body) {
        MessageBlast blast = new MessageBlast();
        blast.setSenderId(senderId);
        blast.setSubject(subject);
        blast.setSentAt(LocalDateTime.now());
        blast.setMessageIds(new ArrayList<>());

        // Save blast first to get its ID
        blast = blastRepo.save(blast);

        ArrayList<String> messageIds = new ArrayList<>();
        for (String recipientId : recipientIds) {
            Message message = Message.builder()
                    .senderId(senderId)
                    .receiverId(recipientId)
                    .subject(subject)
                    .body(body)
                    .read(false)
                    .blast(true)
                    .blastId(blast.getId())
                    .sentAt(LocalDateTime.now())
                    .build();
            Message saved = messageRepo.save(message);
            messageIds.add(saved.getId());
        }

        blast.setMessageIds(messageIds);
        return blastRepo.save(blast);
    }

    /**
     * Returns all individual messages belonging to a blast.
     */
    public List<Message> getBlastMessages(String blastId) {
        return blastRepo.findById(blastId)
                .map(blast -> messageRepo.findByIdIn(blast.getMessageIds()))
                .orElse(List.of());
    }

    /**
     * Returns the recipient IDs for all messages in a blast.
     */
    public ArrayList<String> getBlastRecipientIds(String blastId) {
        return blastRepo.findById(blastId)
                .map(blast -> {
                    List<Message> messages = messageRepo.findByIdIn(blast.getMessageIds());
                    ArrayList<String> recipientIds = new ArrayList<>();
                    for (Message m : messages) {
                        recipientIds.add(m.getReceiverId());
                    }
                    return recipientIds;
                })
                .orElse(new ArrayList<>());
    }

    public boolean delete(String id) {
        if (blastRepo.existsById(id)) {
            blastRepo.deleteById(id);
            return true;
        }
        return false;
    }
}
