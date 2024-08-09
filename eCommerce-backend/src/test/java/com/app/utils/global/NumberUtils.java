package com.app.utils.global;

import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.Set;

import static com.app.global.constants.UserInputConstants.PRICE_PRECISION;
import static com.app.global.constants.UserInputConstants.PRICE_SCALE;

public class NumberUtils {
    private static final int MAX_ATTEMPTS = 10;
    private static final Random random = new Random();

    public static int genIntegerInRange(int min, int max) {
        return random.nextInt(min, max + 1);
    }

    public static long getId() {
        return random.nextLong();
    }

    public static long getLong(long min, long max) {
        return random.nextLong(min, max + 1);
    }

    public static BigDecimal getPrice() {
        long maxPrice = (long) Math.pow(10, PRICE_PRECISION);
        BigDecimal bigDecimal = getBigDecimal(0, maxPrice);
        return bigDecimal.setScale(PRICE_SCALE, RoundingMode.HALF_DOWN);
    }

    public static BigDecimal getBigDecimal(long min, long max) {
        long big = random.nextLong(min, max);
        double decimal = random.nextInt(0, 101) / 100.0; // [0,1] of scale 2
        return new BigDecimal(big).add(BigDecimal.valueOf(decimal));
    }

    private NumberUtils() {
    }

    public static Long getDistinctId(Set<Long> existingIds) {
        int attempt = 0;
        Long generatedId;
        do {
            if (MAX_ATTEMPTS <= attempt++) {
                throw new IllegalStateException("Failed to generate unique id");
            }
            generatedId = getId();
        } while (existingIds.contains(generatedId));
        return generatedId;
    }

    public static String getDistinct(Set<String> presentStrings, int lengthMin, int lengthMax, boolean letters, boolean numeric) {
        int attempt = 0;
        String generatedString;

        do {
            if (MAX_ATTEMPTS <= attempt++) {
                throw new IllegalStateException("Failed to generate unique id");
            }

            int randomLength = NumberUtils.genIntegerInRange(lengthMin, lengthMax);
            generatedString = RandomStringUtils.random(randomLength, letters, numeric);
        } while (presentStrings.contains(generatedString));

        return generatedString;
    }
}
