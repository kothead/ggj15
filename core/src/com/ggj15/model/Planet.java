package com.ggj15.model;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.ggj15.data.ImageCache;

/**
 * Created by st on 1/24/15.
 */
public class Planet {

    private static final int DEFAULT_SIZE = 11;
    private static final int SAFE_SIDE_SIZE = 5;
    private static final int BLOCK_SIZE = 64;

    public enum Block {
        ROCK, SOIL, GRASS;

        private TextureRegion texture;

        Block() {
            this.texture = ImageCache.getTexture(name().toLowerCase());
        }

        public TextureRegion getTexture() {
            return texture;
        }
    }

    private int width, height;
    private float x, y;
    private float minX, minY, maxX, maxY;
    private Block[][] tiles;
    private boolean[][] inked;
    private Rectangle blockRect, intersectionRect;

    private Planet() {
        blockRect = new Rectangle();
        intersectionRect = new Rectangle();
    }

    public int getWidth() {
        return width * BLOCK_SIZE;
    }

    public int getHeight() {
        return height * BLOCK_SIZE;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void draw(float delta, SpriteBatch batch) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Block block = tiles[i][j];
                if (block == null) continue;
                int x = (int) (j * BLOCK_SIZE + this.x);
                int y = (int) (i * BLOCK_SIZE + this.y);
                batch.draw(block.getTexture(), x, y, BLOCK_SIZE, BLOCK_SIZE);
            }
        }
    }

    /*
    TODO: change collision check. Do not iterate over all blocks. Choose wisely
     */
    public float[] collide(Rectangle rect, float vx, float vy) {

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Block block = tiles[i][j];
                if (block == null) continue;
                blockRect.set(x + j * BLOCK_SIZE, y + i * BLOCK_SIZE, width, height);
                if (Intersector.intersectRectangles(rect, blockRect, intersectionRect)) {
                    return new float[] {0, 0};
                }
            }
        }
        return new float[] {vx, vy};
    }


    private int getIndex(int component) {
        return component / BLOCK_SIZE + SAFE_SIDE_SIZE;
    }

    public static class Builder {

        private static final int DEFAULT_INKED_COUNT = 10;

        private Planet instance;
        private int inkedCount;

        public Builder() {
            instance = new Planet();
            instance.width = DEFAULT_SIZE + SAFE_SIDE_SIZE * 2;
            instance.height = DEFAULT_SIZE + SAFE_SIDE_SIZE * 2;
            inkedCount = DEFAULT_INKED_COUNT;
        }

        public Builder width(int width) {
            instance.width = width;
            return this;
        }

        public Builder height(int height) {
            instance.height = height;
            return this;
        }

        public Builder inkedCount(int inkedCount) {
            this.inkedCount = inkedCount;
            return this;
        }

        public Planet build() {
            instance.tiles = new Block[instance.height][instance.width];

            // TODO: delete
            for (int i = 0; i < instance.height; i++) {
                for (int j = 0; j < instance.width; j++) {
                    instance.tiles[i][j] = Block.ROCK;
                }
            }

            return instance;
        }
    }
}
