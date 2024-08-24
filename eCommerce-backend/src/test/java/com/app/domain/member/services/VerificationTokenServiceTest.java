package com.app.domain.member.services;

import com.app.domain.member.entities.Member;
import com.app.domain.member.entities.VerificationToken;
import com.app.domain.member.exceptions.InvalidVerificationTokenException;
import com.app.domain.member.exceptions.VerificationTokenNotFoundException;
import com.app.utils.domain.member.RandomMemberBuilder;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.app.global.constants.UserInputConstants.VERIFICATION_TOKEN_CODE_LENGTH;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class VerificationTokenServiceTest {

    @Value("${security.verification.codeExpiresAfter}")
    private int codeExpireAfter;

    @Autowired
    private MemberService memberService;

    @Autowired
    private VerificationTokenService tokenService;

    @Test
    void createAndSave_ok() {
        Member member = new RandomMemberBuilder().create();
        memberService.save(member);

        VerificationToken token = tokenService.createAndSave(member);

        LocalDateTime expiredTime = LocalDateTime.now().plus(codeExpireAfter + 1, ChronoUnit.MILLIS);
        assertTrue(expiredTime.isAfter(token.getExpiresAt()));
        assertTrue(token.getCode().length() == VERIFICATION_TOKEN_CODE_LENGTH);
    }

    @Test
    void findByCode_ok() {
        Member member = new RandomMemberBuilder().create();
        memberService.save(member);
        VerificationToken token = tokenService.createAndSave(member);

        VerificationToken returnedToken = tokenService.findByCode(token.getCode());

        assertEquals(token.getCode(), returnedToken.getCode());
        assertEquals(
                token.getExpiresAt().truncatedTo(ChronoUnit.MILLIS),
                returnedToken.getExpiresAt().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(token.getMember(), returnedToken.getMember());
    }

    @Test
    void findByCode_throwInvalidVerificationTokenException() {
        String code = RandomStringUtils.randomNumeric(VERIFICATION_TOKEN_CODE_LENGTH);

        assertThrows(InvalidVerificationTokenException.class, () -> tokenService.findByCode(code));
    }

    @Test
    void findByMember_ok() {
        Member member = new RandomMemberBuilder().create();
        memberService.save(member);
        VerificationToken token = tokenService.createAndSave(member);

        VerificationToken returnedToken = tokenService.findByMember(token.getMember());

        assertEquals(token.getCode(), returnedToken.getCode());
        assertEquals(
                token.getExpiresAt().truncatedTo(ChronoUnit.MILLIS),
                returnedToken.getExpiresAt().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(token.getMember(), returnedToken.getMember());
    }

    @Test
    void findByMember_throwVerificationTokenNotFoundException() {
        Member member = new RandomMemberBuilder().create();
        memberService.save(member);

        assertThrows(VerificationTokenNotFoundException.class, () -> tokenService.findByMember(member));
    }

    @Test
    void deleteByMember_ok() {
        Member member = new RandomMemberBuilder().create();
        memberService.save(member);
        VerificationToken token = tokenService.createAndSave(member);
        assertDoesNotThrow(() -> tokenService.findByCode(token.getCode()));

        tokenService.deleteByMember(member);

        assertThrows(InvalidVerificationTokenException.class, () -> tokenService.findByCode(token.getCode()));
    }
}
