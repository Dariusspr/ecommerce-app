package com.app.utils.global;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.HashSet;
import java.util.Set;

public class StringUtils {
    private static final int MAX_ATTEMPTS = 10000;

    public static String[] getDistinct(int count, int lengthMin, int lengthMax) {
        return getDistinct(count, lengthMin, lengthMax, true, true);
    }

    public static String getDistinct(Set<String> presentStrings, int lengthMin, int lengthMax) {
        return getDistinct(presentStrings, lengthMin, lengthMax, true, true);
    }

    public static String[] getDistinct(int count, int lengthMin, int lengthMax, boolean letters, boolean numeric) {
        if (count <= 0) {
            throw new IllegalArgumentException("'count' must be larger than 0");
        }

        Set<String> stringSet = new HashSet<>();

        for (int i = 0; i < count; i++) {
            String generated = getDistinct(stringSet, lengthMin, lengthMax, letters, numeric);
            stringSet.add(generated);
        }

        return stringSet.toArray(new String[0]);
    }

    public static String getDistinct(Set<String> presentStrings, int lengthMin, int lengthMax, boolean letters, boolean numeric) {
        int attempt = 0;
        String generatedString;

        do {
            if (MAX_ATTEMPTS <= attempt++) {
                throw new IllegalStateException("Failed to generate unique string");
            }

            int randomLength = NumberUtils.genIntegerInRange(lengthMin, lengthMax);
            generatedString = RandomStringUtils.random(randomLength, letters, numeric);
        } while (presentStrings.contains(generatedString));

        return generatedString;
    }

    private StringUtils() {}

    public static String getText(int lengthMax) {
        final int TEXT_LENGTH_MIN = 1;
        return RandomStringUtils.randomAlphanumeric(TEXT_LENGTH_MIN, lengthMax);
    }
}
