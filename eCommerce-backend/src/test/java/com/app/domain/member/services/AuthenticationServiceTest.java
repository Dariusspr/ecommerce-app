package com.app.domain.member.services;

import com.app.domain.member.dtos.requests.AuthenticationRequest;
import com.app.domain.member.dtos.requests.NewMemberRequest;
import com.app.domain.member.dtos.responses.AuthenticationResponse;
import com.app.domain.member.entities.Member;
import com.app.domain.member.entities.VerificationToken;
import com.app.domain.member.exceptions.*;
import com.app.infra.email.EmailService;
import com.app.utils.domain.member.RandomMemberBuilder;
import com.app.utils.global.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;

import static com.app.global.constants.UserInputConstants.DEFAULT_MEMBER_ROLE;
import static com.app.global.constants.UserInputConstants.VERIFICATION_TOKEN_CODE_LENGTH;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Autowired
    private RoleService roleService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private VerificationTokenService verificationTokenService;

    @MockBean
    private EmailService emailService;

    @Test
    void registerNewMember_ok() {
        roleService.create(DEFAULT_MEMBER_ROLE);
        NewMemberRequest newMemberRequestRequest = new NewMemberRequest(
                RandomMemberBuilder.getUsername(),
                RandomMemberBuilder.getPassword(),
                RandomMemberBuilder.getEmail());

        authenticationService.registerNewMember(newMemberRequestRequest);

        Member member = memberService.findByUsername(newMemberRequestRequest.username());
        assertEquals(newMemberRequestRequest.username(), member.getUsername());
        assertEquals(DEFAULT_MEMBER_ROLE, member.getRole().getTitle());
        assertNotNull(member.getProfile());
        assertNotNull(member.getId());
        verify(emailService, Mockito.times(1)).sendVerificationEmail(anyString(), any(VerificationToken.class));
    }

    @Test
    void registerNewMember_throwMemberAlreadyExists() {
        roleService.create(DEFAULT_MEMBER_ROLE);
        Member member = new RandomMemberBuilder().create();
        memberService.save(member);
        NewMemberRequest request = new NewMemberRequest(
                member.getUsername(),
                RandomMemberBuilder.getPassword(),
                RandomMemberBuilder.getEmail());

        assertThrows(MemberAlreadyExistsException.class, () -> authenticationService.registerNewMember(request));
        verify(emailService, Mockito.times(0)).sendVerificationEmail(anyString(), any(VerificationToken.class));
    }

    @Test
    void authenticate_ok() {
        roleService.create(DEFAULT_MEMBER_ROLE);
        NewMemberRequest newMemberRequestRequest = new NewMemberRequest(
                RandomMemberBuilder.getUsername(),
                RandomMemberBuilder.getPassword(),
                RandomMemberBuilder.getEmail()
        );
        authenticationService.registerNewMember(newMemberRequestRequest);
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(
                newMemberRequestRequest.username(),
                newMemberRequestRequest.password()
        );
        // Enable an account
        Member member = memberService.findByUsername(newMemberRequestRequest.username());
        member.setAccountEnabled(true);
        memberService.save(member);

        AuthenticationResponse authenticationResponse = authenticationService.authenticate(authenticationRequest);

        assertEquals(newMemberRequestRequest.username(), authenticationResponse.member().username());
        assertNotNull(authenticationResponse.token());
    }

    @Test
    void authenticate_isNotEnabled_throwAccountNotEnabledException() {
        roleService.create(DEFAULT_MEMBER_ROLE);
        NewMemberRequest newMemberRequestRequest = new NewMemberRequest(
                RandomMemberBuilder.getUsername(),
                RandomMemberBuilder.getPassword(),
                RandomMemberBuilder.getEmail()
        );
        authenticationService.registerNewMember(newMemberRequestRequest);
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(
                newMemberRequestRequest.username(),
                newMemberRequestRequest.password()
        );

        verify(emailService, Mockito.times(1)).sendVerificationEmail(anyString(), any(VerificationToken.class));
        assertThrows(AccountNotEnabledException.class, () -> authenticationService.authenticate(authenticationRequest));
        verify(emailService, Mockito.times(1)).sendVerificationEmail(anyString(), any(VerificationToken.class));
    }

    @Test
    void authenticate_isNotEnabledAndExpired_throwAccountNotEnabledException() {
        roleService.create(DEFAULT_MEMBER_ROLE);
        NewMemberRequest newMemberRequestRequest = new NewMemberRequest(
                RandomMemberBuilder.getUsername(),
                RandomMemberBuilder.getPassword(),
                RandomMemberBuilder.getEmail()
        );
        authenticationService.registerNewMember(newMemberRequestRequest);
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(
                newMemberRequestRequest.username(),
                newMemberRequestRequest.password()
        );
        Member member = memberService.findByUsername(newMemberRequestRequest.username());
        VerificationToken token = verificationTokenService.findByMember(member);
        token.setExpiresAt(LocalDateTime.now().minusDays(1));
        verificationTokenService.save(token);


        verify(emailService, Mockito.times(1)).sendVerificationEmail(anyString(), any(VerificationToken.class));
        assertThrows(AccountNotEnabledException.class, () -> authenticationService.authenticate(authenticationRequest));
        verify(emailService, Mockito.times(2)).sendVerificationEmail(anyString(), any(VerificationToken.class));
    }

    @Test
    void authenticate_invalidPassword_throwBadMemberCredentialsException() {
        roleService.create(DEFAULT_MEMBER_ROLE);
        NewMemberRequest newMemberRequestRequest = new NewMemberRequest(
                RandomMemberBuilder.getUsername(),
                RandomMemberBuilder.getPassword(),
                RandomMemberBuilder.getEmail()
        );
        authenticationService.registerNewMember(newMemberRequestRequest);
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(
                newMemberRequestRequest.username(),
                newMemberRequestRequest.password() + StringUtils.getText(1)
        );

        assertThrows(BadMemberCredentialsException.class, () -> authenticationService.authenticate(authenticationRequest));
    }

    @Test
    void authenticate_invalidUsername_throwBadMemberCredentialsException() {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(
                RandomMemberBuilder.getUsername(),
                RandomMemberBuilder.getPassword()
        );

        assertThrows(BadMemberCredentialsException.class, () -> authenticationService.authenticate(authenticationRequest));
    }

    @Test
    void verify_ok() {
        roleService.create(DEFAULT_MEMBER_ROLE);
        NewMemberRequest newMemberRequestRequest = new NewMemberRequest(
                RandomMemberBuilder.getUsername(),
                RandomMemberBuilder.getPassword(),
                RandomMemberBuilder.getEmail()
        );
        authenticationService.registerNewMember(newMemberRequestRequest);
        Member member = memberService.findByUsername(newMemberRequestRequest.username());
        VerificationToken token = verificationTokenService.findByMember(member);

        authenticationService.verify(token.getCode());

        Member updatedMember = memberService.findById(member.getId());
        assertTrue(updatedMember.isAccountEnabled());
    }

    @Test
    void verify_expiredToken_throwExpiredVerificationCodeException() {
        roleService.create(DEFAULT_MEMBER_ROLE);
        NewMemberRequest newMemberRequestRequest = new NewMemberRequest(
                RandomMemberBuilder.getUsername(),
                RandomMemberBuilder.getPassword(),
                RandomMemberBuilder.getEmail()
        );
        authenticationService.registerNewMember(newMemberRequestRequest);
        Member member = memberService.findByUsername(newMemberRequestRequest.username());
        VerificationToken token = verificationTokenService.findByMember(member);
        token.setExpiresAt(token.getExpiresAt().minusDays(1));
        verificationTokenService.save(token);

        assertThrows(ExpiredVerificationCodeException.class, () -> authenticationService.verify(token.getCode()));
    }

    @Test
    void verify_invalidToken_throwExpiredVerificationCodeException() {
        String code = RandomStringUtils.randomNumeric(VERIFICATION_TOKEN_CODE_LENGTH);

        assertThrows(InvalidVerificationTokenException.class, () -> authenticationService.verify(code));
    }
}

