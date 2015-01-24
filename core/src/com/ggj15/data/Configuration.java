package com.ggj15.data;

/**
 * Created by st on 1/23/15.
 */
public class Configuration {
    public static final int MULTIPLIER = 3;
    public static final int WORLD_WIDTH = 2000 * MULTIPLIER;
    public static final int WORLD_HEIGHT = 1200 * MULTIPLIER;
    public static final float SCALE_FACTOR = 0.75f / MULTIPLIER;
    public static float scaleFactorX;
    public static float scaleFactorY;

    public static final float worldMaxWidth = 3000 * MULTIPLIER;
    public static final float worldMaxHeight = 1800 * MULTIPLIER;
    public static final float worldMinWidth = -worldMaxWidth;
    public static final float worldMinHeight = -worldMaxHeight;

}
