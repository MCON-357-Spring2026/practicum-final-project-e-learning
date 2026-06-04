package com.elearning.service;

import com.elearning.dto.MessageBlastDTO;
import com.elearning.model.Message;
import org.springframework.stereotype.Service;

/**
 * Service for sending email notifications.
 * Reserved for future implementation.
 */
@Service
public class EmailService {

    public void sendEmail(Message message) {
        // Placeholder for email sending logic using SendGrid or another provider
    }

    public void sendBlast(MessageBlastDTO blast) {
        // Placeholder for sending bulk email blasts to users
    }   
    
}
