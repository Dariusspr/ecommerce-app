package com.app.domain.member.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static com.app.global.constants.UserInputConstants.*;
import static com.app.global.constants.UserInputConstants.PASSWORD_LENGTH_MAX;

public record NewMemberRequest(

        @NotBlank
        @Size(min = USERNAME_LENGTH_MIN, max = USERNAME_LENGTH_MAX)
        @Pattern(regexp = USERNAME_REGEX)
        String username,

        @NotBlank
        @Size(min= PASSWORD_LENGTH_MIN, max=PASSWORD_LENGTH_MAX)
        String password,

        @NotBlank
        @Email
        String email) {
}