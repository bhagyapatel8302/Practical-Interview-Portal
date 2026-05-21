package com.tatvasoft.interview_portal.service.impl;

import com.tatvasoft.interview_portal.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendResetPasswordEmail(String toEmail,
                                       String resetLink) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);

        message.setSubject("Reset Password - Interview Portal");

        message.setText(
                "Click below link to reset your password:\n\n"
                        + resetLink
        );

        mailSender.send(message);
    }
}