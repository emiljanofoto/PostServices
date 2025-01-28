package com.example.postservice.post_service.service;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.stereotype.Service;

@Service
public class WhatsAppNotificationService {

    private final String accountSid = System.getenv("TWILIO_ACCOUNT_SID"); // Get from environment variables
    private final String authToken = System.getenv("TWILIO_AUTH_TOKEN");   // Get from environment variables
    private final String fromNumber = "whatsapp:+14155238886";            // Twilio WhatsApp sandbox number

    public WhatsAppNotificationService() {
        try {
            if (accountSid != null && authToken != null) {
                Twilio.init(accountSid, authToken);
            } else {
                System.err.println("Twilio credentials are missing. Notifications will be disabled.");
            }
        } catch (Exception e) {
            System.err.println("Failed to initialize Twilio: " + e.getMessage());
        }
    }

    public void sendWhatsAppMessage(String toNumber, String message) {
        try {
            if (accountSid == null || authToken == null) {
                System.err.println("Twilio credentials are not set. Skipping WhatsApp notification.");
                return;
            }

            Message.creator(
                    new com.twilio.type.PhoneNumber("whatsapp:" + toNumber),
                    new com.twilio.type.PhoneNumber(fromNumber),
                    message
            ).create();

            System.out.println("WhatsApp message sent successfully to " + toNumber);
        } catch (ApiException e) {
            System.err.println("Failed to send WhatsApp message: " + e.getMessage());
        }
    }
}