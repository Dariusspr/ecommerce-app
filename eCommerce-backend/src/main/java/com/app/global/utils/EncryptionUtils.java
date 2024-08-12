package com.app.global.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncryptionUtils {
    private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public static String getEncodedPassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    public static boolean passwordsMatch(String rawPassword, String encodedPassword) {
        return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
    }

    private EncryptionUtils() {}
}
