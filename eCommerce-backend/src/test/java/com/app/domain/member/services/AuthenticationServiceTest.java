package com.app.domain.member.services;

import com.app.domain.member.dtos.requests.AuthenticationRequest;
import com.app.domain.member.dtos.requests.NewMemberRequest;
import com.app.domain.member.dtos.responses.AuthenticationResponse;
import com.app.domain.member.entities.Member;
import com.app.domain.member.exceptions.BadMemberCredentialsException;
import com.app.domain.member.exceptions.MemberAlreadyExistsException;
import com.app.utils.domain.member.RandomMemberBuilder;
import com.app.utils.global.StringUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static com.app.global.constants.UserInputConstants.DEFAULT_MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Tag("IntegrationTest")
public class AuthenticationServiceTest {

    @Autowired
    private RoleService roleService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private AuthenticationService authenticationService;

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
    }

    @Test
    void registerNewMember_throwMemberAlreadyExists() {
        Member member = new RandomMemberBuilder().create();
        memberService.save(member);
        NewMemberRequest request = new NewMemberRequest(
                member.getUsername(),
                RandomMemberBuilder.getPassword(),
                RandomMemberBuilder.getEmail());

        assertThrows(MemberAlreadyExistsException.class, () -> authenticationService.registerNewMember(request));
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

        AuthenticationResponse authenticationResponse = authenticationService.authenticate(authenticationRequest);

        assertEquals(newMemberRequestRequest.username(), authenticationResponse.member().username());
        assertNotNull(authenticationResponse.token());
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
}

