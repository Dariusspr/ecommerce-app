package com.app.global.utils;

import com.app.domain.member.entities.Member;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtil {

    public static Member getAuthenticated() {
        return (Member) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private AuthUtil() {}
}
