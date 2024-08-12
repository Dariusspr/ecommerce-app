package com.app.utils.global;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
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

            int randomLength = NumberUtils.getIntegerInRange(lengthMin, lengthMax);
            generatedString = RandomStringUtils.random(randomLength, letters, numeric);
        } while (presentStrings.contains(generatedString));

        return generatedString;
    }

    public static String toJSON(Object o) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        return objectWriter.writeValueAsString(o);
    }


    public static String getText(int lengthMax) {
        final int TEXT_LENGTH_MIN = 1;
        return RandomStringUtils.randomAlphanumeric(TEXT_LENGTH_MIN, lengthMax);
    }

    private StringUtils() {}
}
