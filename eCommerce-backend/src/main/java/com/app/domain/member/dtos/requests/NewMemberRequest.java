package com.app.domain.member.dtos.requests;

public record NewMemberRequest(String username, String password, String email) {
}