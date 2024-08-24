package com.app.domain.member.services;

import com.app.domain.member.dtos.responses.AuthenticationResponse;
import com.app.domain.member.dtos.requests.AuthenticationRequest;
import com.app.domain.member.dtos.requests.NewMemberRequest;
import com.app.domain.member.entities.Member;
import com.app.domain.member.entities.Role;
import com.app.domain.member.entities.VerificationToken;
import com.app.domain.member.exceptions.AccountNotEnabledException;
import com.app.domain.member.exceptions.BadMemberCredentialsException;
import com.app.domain.member.exceptions.ExpiredVerificationCodeException;
import com.app.domain.member.exceptions.MemberAlreadyExistsException;
import com.app.domain.member.mappers.MemberMapper;
import com.app.infra.email.EmailService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

import static com.app.global.constants.UserInputConstants.DEFAULT_MEMBER_ROLE;

@Service
public class AuthenticationService {

    private final MemberService memberService;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;

    public AuthenticationService(MemberService memberService, RoleService roleService, AuthenticationManager authenticationManager, JwtService jwtService, VerificationTokenService verificationTokenService, EmailService emailService) {
        this.memberService = memberService;
        this.roleService = roleService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.verificationTokenService = verificationTokenService;
        this.emailService = emailService;
    }

    @Transactional
    public void registerNewMember(NewMemberRequest request) {
        if (memberService.memberExists(request.username())) {
            throw new MemberAlreadyExistsException();
        }
        Role memberRole = roleService.findByTitle(DEFAULT_MEMBER_ROLE);
        Member member = MemberMapper.toMember(request);
        member.setRole(memberRole);
        member.setAccountEnabled(false);
        memberService.save(member);

        createAndSendVerification(member);
    }

    private void createAndSendVerification(Member member) {
        VerificationToken verificationToken = verificationTokenService.createAndSave(member);
        emailService.sendVerificationEmail(member.getEmail(), verificationToken);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );
            Member member = ((Member) authentication.getPrincipal());
            if (!member.isAccountEnabled()) {
                VerificationToken token = verificationTokenService.findByMember(member);
                if (token.isExpired()) {
                    verificationTokenService.delete(token);
                    createAndSendVerification(member);
                }
                throw new AccountNotEnabledException();
            }
            String jwtToken = getJwtToken(member);
            return new AuthenticationResponse(MemberMapper.toMemberSummaryDTO(member), jwtToken);
        } catch (AuthenticationException e) {
            throw new BadMemberCredentialsException();
        }
    }

    @Transactional
    public void verify(String code) {
        VerificationToken token = verificationTokenService.findByCode(code);
        Member member = token.getMember();
        if (token.isExpired()) {
            verificationTokenService.deleteByMember(member);
            createAndSendVerification(member);
            throw new ExpiredVerificationCodeException();
        }
        member.setAccountEnabled(true);
        memberService.save(member);
        verificationTokenService.deleteByMember(member);
    }

    private String getJwtToken(Member member) {
        var claims = new HashMap<String, Object>();
        claims.put("username", member.getUsername());
        return jwtService.generateToken(claims, member);
    }
}
