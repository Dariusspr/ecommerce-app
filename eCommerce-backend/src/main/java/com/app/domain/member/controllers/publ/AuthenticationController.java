package com.app.domain.member.controllers.publ;

import com.app.domain.member.dtos.requests.AuthenticationRequest;
import com.app.domain.member.dtos.requests.NewMemberRequest;
import com.app.domain.member.dtos.responses.AuthenticationResponse;
import com.app.domain.member.services.AuthenticationService;
import com.app.global.constants.RestEndpoints;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.app.global.constants.UserInputConstants.VERIFICATION_TOKEN_CODE_LENGTH;

@RestController
@RequestMapping(AuthenticationController.BASE_URL)
public class AuthenticationController {
    public static final String BASE_URL = RestEndpoints.PUBLIC_API + "/auth";

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerNewMember(
            @Validated
            @RequestBody
            NewMemberRequest request) {
        authenticationService.registerNewMember(request);
        ResponseEntity<?> response = ResponseEntity.accepted().build();
        return response;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(
            @Validated
            @RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = authenticationService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify/{code}")
    public ResponseEntity<?> verify(
            @NotBlank
            @Size(min = VERIFICATION_TOKEN_CODE_LENGTH, max = VERIFICATION_TOKEN_CODE_LENGTH)
            @PathVariable String code) {
        authenticationService.verify(code);
        return ResponseEntity.accepted().build();
    }
}
