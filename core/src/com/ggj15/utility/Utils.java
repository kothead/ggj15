package com.ggj15.utility;

import java.util.Random;

/**
 * Created by st on 1/26/15.
 */
public class Utils {

    private static Random random;

    private Utils() {

    }

    public static Random getRandom() {
        if (random == null) {
            random = new Random();
        }
        return random;
    }
}
