package com.ggj15.model;

/**
 * Created by st on 12/7/14.
 */
public enum Direction {
    UP, RIGHT, DOWN, LEFT;

    private static Direction[] directions;

    private Direction opposite;

    static {
        UP.opposite = DOWN;
        RIGHT.opposite = LEFT;
        DOWN.opposite = UP;
        LEFT.opposite = RIGHT;
        directions = new Direction[] {UP, RIGHT, DOWN, LEFT};
    }

    public static Direction getByOffset(int dx, int dy) {
        if (dx > 0) return RIGHT;
        if (dx < 0) return LEFT;
        if (dy > 0) return UP;
        if (dy < 0) return DOWN;
        return null;
    }

    public static int getDx(Direction dir) {
        if (dir == RIGHT) return 1;
        if (dir == LEFT) return -1;
        return 0;
    }

    public static int getDy(Direction dir) {
        if (dir == DOWN) return -1;
        if (dir == UP) return 1;
        return 0;
    }

    public static Direction[] getDirections() {
        return directions;
    }

    public Direction getOpposite() {
        return opposite;
    }
};
