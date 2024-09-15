package com.app.domain.member.services;

import com.app.domain.member.entities.Member;
import com.app.domain.member.entities.VerificationToken;
import com.app.domain.member.exceptions.InvalidVerificationTokenException;
import com.app.domain.member.exceptions.VerificationTokenNotFoundException;
import com.app.domain.member.repositories.MemberRepository;
import com.app.domain.member.repositories.VerificationTokenRepository;
import com.app.utils.domain.member.RandomMemberBuilder;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.app.global.constants.UserInputConstants.VERIFICATION_TOKEN_CODE_LENGTH;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VerificationTokenServiceTest {

    @Value("${security.verification.codeExpiresAfter}")
    private int codeExpiresAfter;

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private VerificationTokenService tokenService;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    private Member member;
    private VerificationToken token;

    @BeforeEach
    void setup() {
        member = new RandomMemberBuilder().create();
        memberService.save(member);
        token = tokenService.createAndSave(member);
    }

    @AfterEach
    void clear() {
        verificationTokenRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    void createAndSave_ok() {
        LocalDateTime expiredTime = LocalDateTime.now().plus(codeExpiresAfter + 1, ChronoUnit.MILLIS);
        assertTrue(expiredTime.isAfter(token.getExpiresAt()));
        assertTrue(token.getCode().length() == VERIFICATION_TOKEN_CODE_LENGTH);
    }

    @Test
    void findByCode_ok() {
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
        assertDoesNotThrow(() -> tokenService.findByCode(token.getCode()));

        tokenService.deleteByMember(member);

        assertThrows(InvalidVerificationTokenException.class, () -> tokenService.findByCode(token.getCode()));
    }
}
