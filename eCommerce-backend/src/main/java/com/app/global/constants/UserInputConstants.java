package com.app.global.constants;

import com.app.global.enums.FileFormat;
import com.app.global.enums.Gender;

public class UserInputConstants {
    private UserInputConstants() {}

    public static final int PRICE_PRECISION = 8;
    public static final int PRICE_SCALE = 2;

    public static final int TITLE_LENGTH = 40;

    public static final int USERNAME_LENGTH = 30;

    public static final Gender DEFAULT_MEMBER_GENDER = Gender.OTHER;

    public static final String DEFAULT_MEMBER_PROFILE_TITLE = "default-profile";
    public static final String DEFAULT_MEMBER_PROFILE_URL = "default-member-url";
    public static final FileFormat DEFAULT_MEMBER_PROFILE_FORMAT = FileFormat.PNG;

    public static final int RATING_MIN = 1;
    public static final int RATING_MAX = 10;

    public static final int COMMENT_CONTENT_LENGTH_MIN = 3;

}
