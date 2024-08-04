package com.app.utils.global;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

import static com.app.global.constants.UserInputConstants.PRICE_PRECISION;
import static com.app.global.constants.UserInputConstants.PRICE_SCALE;

public class NumberUtils {
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
        double decimal  = random.nextInt(0, 101) / 100.0; // [0,1] of scale 2
        return new BigDecimal(big).add(BigDecimal.valueOf(decimal));
    }

    private NumberUtils() {}
}
