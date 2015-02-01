package com.ggj15.utility;

import java.util.Random;

/**
 * Created by Тимофей on 01.02.2015.
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