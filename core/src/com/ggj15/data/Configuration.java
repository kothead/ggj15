package com.ggj15.data;

/**
 * Created by st on 1/23/15.
 */
public class Configuration {
    public static final int MULTIPLIER = 1;
    public static final int WORLD_WIDTH = 1800 * MULTIPLIER;
    public static final int WORLD_HEIGHT = 1000 * MULTIPLIER;
    public static final float SCALE_FACTOR = 0.75f / MULTIPLIER;
    public static float scaleFactorX;
    public static float scaleFactorY;

    public static final float worldMaxWidth = 10000;
    public static final float worldMaxHeight = 6000;
    public static final float worldMinWidth = -worldMaxWidth;
    public static final float worldMinHeight = -worldMaxHeight;

}
