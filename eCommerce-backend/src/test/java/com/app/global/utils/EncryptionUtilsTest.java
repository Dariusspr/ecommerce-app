package com.app.global.utils;

import com.app.utils.global.NumberUtils;
import com.app.utils.global.StringUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import static com.app.global.constants.UserInputConstants.PASSWORD_LENGTH_MAX;
import static com.app.global.constants.UserInputConstants.PASSWORD_LENGTH_MIN;

@Tag("Unit test")
public class EncryptionUtilsTest {

    @Test
    void passwordsMatch_true() {
        final int randomLength = NumberUtils.getIntegerInRange(PASSWORD_LENGTH_MIN, PASSWORD_LENGTH_MAX);
        final String password = StringUtils.getText(randomLength);

        String encodedPassword = EncryptionUtils.getEncodedPassword(password);

        assertTrue(EncryptionUtils.passwordsMatch(password, encodedPassword));
    }

    @Test
    void passwordsMatch_false() {
        final int randomLength = NumberUtils.getIntegerInRange(PASSWORD_LENGTH_MIN, PASSWORD_LENGTH_MAX);
        final String password = StringUtils.getText(randomLength);
        final String otherPassword = StringUtils.getText(randomLength);

        String encodedPassword = EncryptionUtils.getEncodedPassword(password);

        assertFalse(EncryptionUtils.passwordsMatch(otherPassword, encodedPassword));
    }
}
