package com.app.infra.email;

import com.app.domain.member.entities.VerificationToken;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private final JavaMailSender sender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String from;

    public EmailService(JavaMailSender sender, TemplateEngine templateEngine) {
        this.sender = sender;
        this.templateEngine = templateEngine;

    }

    public void sendVerificationEmail(String recipient, VerificationToken token) {
        final String verificationSubject = "Account verification";
        String htmlContent = getVerificationHtmlContent(token);
        sendHtmlEmail(recipient, verificationSubject, htmlContent);
    }

    public void sendHtmlEmail(String recipient, String subject, String htmlContent) {
        try {
            MimeMessage message = sender.createMimeMessage();
            message.setFrom(from);
            message.setRecipients(MimeMessage.RecipientType.TO, recipient);
            message.setSubject(subject);
            message.setContent(htmlContent, "text/html; charset=utf-8");

            sender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getVerificationHtmlContent(VerificationToken token) {
        String formattedDateTime = getFormattedDateTime(token.getExpiresAt());
        Context context = new Context();
        context.setVariable("code", token.getCode());
        context.setVariable("expiresAt", formattedDateTime);
        return templateEngine.process("verification_template", context);
    }

    private String getFormattedDateTime(LocalDateTime date) {
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return date.format(formatters);
    }

}
