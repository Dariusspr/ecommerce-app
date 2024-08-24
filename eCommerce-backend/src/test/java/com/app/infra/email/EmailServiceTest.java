package com.app.infra.email;

import com.app.domain.member.entities.Member;
import com.app.domain.member.entities.VerificationToken;
import com.app.utils.domain.member.RandomMemberBuilder;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;

import static com.app.global.constants.UserInputConstants.VERIFICATION_TOKEN_CODE_LENGTH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @MockBean
    private JavaMailSender mailSender;

    @Test
    void sendVerificationEmail() {
        Member member = new RandomMemberBuilder().create();
        VerificationToken token = new VerificationToken();
        token.setMember(member);
        token.setExpiresAt(LocalDateTime.now());
        token.setCode(RandomStringUtils.randomNumeric(VERIFICATION_TOKEN_CODE_LENGTH));
        MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);
        Mockito.when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendVerificationEmail("test@example.com", token);

        verify(mailSender, Mockito.times(1)).send(any(MimeMessage.class));
    }
}
