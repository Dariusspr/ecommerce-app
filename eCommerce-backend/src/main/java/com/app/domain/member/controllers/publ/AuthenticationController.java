package com.app.domain.member.controllers.publ;

import com.app.domain.member.dtos.requests.AuthenticationRequest;
import com.app.domain.member.dtos.requests.NewMemberRequest;
import com.app.domain.member.dtos.responses.AuthenticationResponse;
import com.app.domain.member.services.AuthenticationService;
import com.app.global.constants.RestEndpoints;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(AuthenticationController.BASE_URL)
public class AuthenticationController {
    public static final String BASE_URL = RestEndpoints.PUBLIC_API + "/auth";

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerNewMember(@RequestBody NewMemberRequest request) {
        authenticationService.registerNewMember(request);
        ResponseEntity<?> response = ResponseEntity.accepted().build();
        return response;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = authenticationService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify/{code}")
    public ResponseEntity<?> verify(@PathVariable String code) {
        authenticationService.verify(code);
        return ResponseEntity.accepted().build();
    }
}
