package com.app.domain.member.services;

import com.app.domain.member.dtos.responses.AuthenticationResponse;
import com.app.domain.member.dtos.requests.AuthenticationRequest;
import com.app.domain.member.dtos.requests.NewMemberRequest;
import com.app.domain.member.entities.Member;
import com.app.domain.member.entities.Role;
import com.app.domain.member.exceptions.BadMemberCredentialsException;
import com.app.domain.member.exceptions.MemberAlreadyExistsException;
import com.app.domain.member.mappers.MemberMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

import static com.app.global.constants.UserInputConstants.DEFAULT_MEMBER_ROLE;

@Service
public class AuthenticationService {

    private final MemberService memberService;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationService(MemberService memberService, RoleService roleService, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.memberService = memberService;
        this.roleService = roleService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public void registerNewMember(NewMemberRequest request) {
        if (memberService.memberExists(request.username())) {
            throw new MemberAlreadyExistsException();
        }
        Role memberRole = roleService.findByTitle(DEFAULT_MEMBER_ROLE);
        Member member = MemberMapper.toMember(request);
        member.setRole(memberRole);
        member.setAccountEnabled(true);
        memberService.save(member);
    }

    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );
            Member member = ((Member) authentication.getPrincipal());
            String jwtToken = getToken(member);
            return new AuthenticationResponse(MemberMapper.toMemberSummaryDTO(member), jwtToken);
        } catch (AuthenticationException e) {
            throw new BadMemberCredentialsException();
        }
    }

    private String getToken(Member member) {
        var claims = new HashMap<String, Object>();
        claims.put("username", member.getUsername());
        return jwtService.generateToken(claims, member);
    }
}
