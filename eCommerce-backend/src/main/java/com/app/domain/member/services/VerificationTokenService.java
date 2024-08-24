package com.app.domain.member.services;

import com.app.domain.member.entities.Member;
import com.app.domain.member.entities.VerificationToken;
import com.app.domain.member.exceptions.InvalidVerificationTokenException;
import com.app.domain.member.exceptions.UnableToGenerateVerificationCode;
import com.app.domain.member.exceptions.VerificationTokenNotFoundException;
import com.app.domain.member.repositories.VerificationTokenRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.app.global.constants.UserInputConstants.VERIFICATION_TOKEN_CODE_LENGTH;

@Service
public class VerificationTokenService {
    private final static int MAX_GENERATION_ATTEMPTS = 123;

    @Value("${security.verification.codeExpiresAfter}")
    private int codeExpiresAfter;

    private final VerificationTokenRepository verificationTokenRepository;

    public VerificationTokenService(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Transactional
    public VerificationToken createAndSave(Member member) {
        String uniqueCode = createUniqueCode();
        VerificationToken token = new VerificationToken(
                uniqueCode,
                member,
                LocalDateTime.now().plus(codeExpiresAfter, ChronoUnit.MILLIS));
        save(token);
        return token;
    }

    @Transactional
    public void delete(VerificationToken token) {
        verificationTokenRepository.delete(token);
    }

    private String createUniqueCode() {
        String code;
        int attempt = 0;
        do {
            if (++attempt > MAX_GENERATION_ATTEMPTS)
                throw new UnableToGenerateVerificationCode();

            code = RandomStringUtils.randomNumeric(VERIFICATION_TOKEN_CODE_LENGTH);
        } while (!isUnique(code));
        return code;
    }

    private boolean isUnique(String code) {
        return verificationTokenRepository.findByCode(code).isEmpty();
    }

    @Transactional
    public VerificationToken save(VerificationToken token) {
        return verificationTokenRepository.save(token);
    }

    public VerificationToken findByCode(String code) {
        return verificationTokenRepository.findByCode(code).orElseThrow(InvalidVerificationTokenException::new);
    }

    @Transactional
    public void deleteByMember(Member member) {
        verificationTokenRepository.deleteByMember(member);
    }

    public VerificationToken findByMember(Member member) {
        return verificationTokenRepository.findByMember(member).orElseThrow(VerificationTokenNotFoundException::new);
    }
}
