package com.app.global.utils;

import com.app.domain.member.entities.Member;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtils {

    public static Member getAuthenticated() {
        return (Member) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static boolean isNotAllowedModifier(Member author) {
        Member modifier = AuthUtils.getAuthenticated();
        return !modifier.equals(author) && !modifier.isAdmin();
    }

    private AuthUtils() {}
}
