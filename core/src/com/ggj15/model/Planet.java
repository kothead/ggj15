package com.ggj15.model;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.ggj15.data.Configuration;
import com.ggj15.data.ImageCache;

import java.util.Random;

/**
 * Created by st on 1/24/15.
 */
public class Planet {

    private static final int DEFAULT_SIZE = 11;
    private static final int SAFE_SIDE_SIZE = 5;
    public static final int BLOCK_SIZE = 64;

    private static final float DEFAULT_GRAVITY_FORCE = 4000f;

    public PlanetActor actor;

    public PlanetActor getActor() {
        return actor;
    }

    public enum Block {
        ROCK, TERRA, GRASS;

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
    private Rectangle objRect, blockRect, intersectionRect, planetRectangle;
    private float force;

    private Planet() {
        objRect = new Rectangle();
        blockRect = new Rectangle();
        intersectionRect = new Rectangle();
        planetRectangle = new Rectangle();
        actor = new PlanetActor();
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

    public Rectangle getRectangle() {
        planetRectangle.setX(this.x);
        planetRectangle.setY(this.y);
        planetRectangle.setWidth(width * BLOCK_SIZE);
        planetRectangle.setHeight(height * BLOCK_SIZE);
        return planetRectangle;
    }

    public float getForce(float x, float y) {
        float centerX = this.x + width * BLOCK_SIZE / 2f;
        float centerY = this.y + height * BLOCK_SIZE / 2f;
        float diffX = centerX - x;
        float diffY = centerY - y;
        return DEFAULT_GRAVITY_FORCE / (float) Math.sqrt(diffX * diffX + diffY * diffY);
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

    public Block takeBlock(Direction gravity, int x, int y) {
        int idx = getHorizontalIndex(x) + Direction.getDx(gravity);
        int idy = getVerticalIndex(y) + Direction.getDy(gravity);
        Block block = tiles[idy][idx];
        if (block != null) {
            tiles[idy][idx] = null;
            calcLimits();
        }
        return block;
    }

    public Block takeBlock(float x, float y) {
        int idx = getHorizontalIndex(x);
        int idy = getVerticalIndex(y);
        Block block = tiles[idy][idx];
        if (block != null) {
            tiles[idy][idx] = null;
            calcLimits();
        }
        return block;
    }

    public Vector2 getBlockCoordinates(float x, float y) {
        return new Vector2(getHorizontalIndex(x) * BLOCK_SIZE + this.x, getVerticalIndex(y) * BLOCK_SIZE + this.y);
    }

    public boolean putBlock(Block block, int x, int y) {
        int idx = getHorizontalIndex(x);
        int idy = getVerticalIndex(y);
        if (tiles[idy][idx] == null) {
            tiles[idy][idx] = block;
            calcLimits();
            return true;
        }
        return false;
    }

    public Direction getNewGravity(Direction gravity, int x, int y) {
        int idx = getHorizontalIndex(x);
        int idy = getVerticalIndex(y);

        switch (gravity) {
            case UP:
            case DOWN:
                if (idy >= minY && idy <= maxY) {
                    if (idx < minX) return Direction.RIGHT;
                    if (idx > maxX) return Direction.LEFT;
                }
                break;

            case LEFT:
            case RIGHT:
                if (idx >= minX && idx <= maxX) {
                    if (idy < minY) return Direction.UP;
                    if (idy > minY) return Direction.DOWN;
                }
                break;
        }
        return gravity;
    }

    /*
    TODO: change collision check. Do not iterate over all blocks. Choose wisely
     */
    public float[] collide(float x, float y, float width, float height, float dx, float dy) {
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                Block block = tiles[i][j];
                if (block == null) continue;
                float tileX = getX() + j * BLOCK_SIZE;
                float tileY = getY() + i * BLOCK_SIZE;
                blockRect.set(tileX, tileY, BLOCK_SIZE, BLOCK_SIZE);

                objRect.set(x + dx, y, width, height);
                if (Intersector.intersectRectangles(objRect, blockRect, intersectionRect)) {
                    dx = Math.signum(dx) * (Math.abs(dx) - intersectionRect.getWidth() - 1);
                }

                objRect.set(x, y + dy, width, height);
                if (Intersector.intersectRectangles(objRect, blockRect, intersectionRect)) {
                    dy = Math.signum(dy) * (Math.abs(dy) - intersectionRect.getHeight() - 1);
                }

//                objRect.set(x + dx, y + dy, width, height);
//                if (Intersector.intersectRectangles(objRect, blockRect, intersectionRect)) {
//                    return new float[] {0, 0};
//                }
            }
        }
        return new float[] {dx, dy};
    }

    private int getHorizontalIndex(float x) {
        return (int) ((x - this.x) / BLOCK_SIZE);
    }

    private int getVerticalIndex(float y) {
        return (int) ((y - this.y) / BLOCK_SIZE);
    }

    private void calcLimits() {
        minX = Integer.MAX_VALUE;
        minY = Integer.MAX_VALUE;
        maxX = Integer.MIN_VALUE;
        maxY = Integer.MIN_VALUE;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (tiles[i][j] == null) continue;
                if (j < minX) minX = j;
                if (i < minY) minY = i;
                if (j > maxX) maxX = j;
                if (j > maxY) maxY = i;
            }
        }
    }

    private static Random random = new Random();

    static{
        random.setSeed(654654L);
    }

    public static class Builder {

        private static final int DEFAULT_INKED_COUNT = 10;
        public enum PlanetType {ROCK, GRASS};

        private Planet instance;
        private int inkedCount;
        private PlanetType planetType;

        public Builder() {
            instance = new Planet();
            instance.width = DEFAULT_SIZE + SAFE_SIDE_SIZE * 2;
            instance.height = DEFAULT_SIZE + SAFE_SIDE_SIZE * 2;
            instance.force = DEFAULT_GRAVITY_FORCE;
            inkedCount = DEFAULT_INKED_COUNT;
            planetType = PlanetType.GRASS;
        }

        public Builder width(int width) {
            instance.width = width + SAFE_SIDE_SIZE * 2;
            return this;
        }

        public Builder height(int height) {
            instance.height = height + SAFE_SIDE_SIZE * 2;
            return this;
        }

        public Builder inkedCount(int inkedCount) {
            this.inkedCount = inkedCount;
            return this;
        }

        public Builder force(float force) {
            instance.force = force;
            return this;
        }

        public Planet build() {
            instance.tiles = new Block[instance.height][instance.width];

            // TODO: delete
            switch (planetType){
                case ROCK:
                    rockStrategy();
                    break;
                case GRASS:
                    grassStrategy();
                    break;
                default:
                    rockStrategy();
            }

            instance.calcLimits();
            return instance;
        }

        private void rockStrategy() {
            for (int i = SAFE_SIDE_SIZE; i < instance.height - SAFE_SIDE_SIZE; i++) {
                for (int j = SAFE_SIDE_SIZE; j < instance.width - SAFE_SIDE_SIZE; j++) {
                    instance.tiles[i][j] = Block.ROCK;
                }
            }
        }

        private void grassStrategy() {
            for (int i = SAFE_SIDE_SIZE; i < instance.height - SAFE_SIDE_SIZE; i++) {
                for (int j = SAFE_SIDE_SIZE; j < instance.width - SAFE_SIDE_SIZE; j++) {
                    if(getBoundaryLevel(i,j)==0){
                        if(random.nextInt(10)!=0){
                            instance.tiles[i][j] = Block.GRASS;
                        } else {
                            instance.tiles[i][j] = Block.TERRA;
                        }
                    } else if (getBoundaryLevel(i,j)>0 && getBoundaryLevel(i,j)<3){
                        if(random.nextInt(4)!=0){
                            instance.tiles[i][j] = Block.TERRA;
                        } else {
                            instance.tiles[i][j] = Block.ROCK;
                        }
                    } else {
                        instance.tiles[i][j] = Block.ROCK;
                    }
                }
            }
        }

        public int getBoundaryLevel(int i, int j){
            int[] boundaryLevels = new int[]{Math.abs(i-SAFE_SIDE_SIZE),
                    Math.abs(j-SAFE_SIDE_SIZE),
                    Math.abs(i-instance.height + SAFE_SIDE_SIZE+1),
                    Math.abs(j-instance.width + SAFE_SIDE_SIZE+1) };
            int min = boundaryLevels [0];
            for (int k = 1; k < 4; k++) {
                if(min>boundaryLevels[k]){
                    min = boundaryLevels[k];
                }
            }
            return min;
        }
    }

    public class PlanetActor extends Actor {

        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);

            float offsetX = getStage().getWidth() / 2f;
            float offsetY = getStage().getHeight() / 2f;

            float width = Planet.this.getCleanWidth() * Configuration.scaleFactorX;
            float height = Planet.this.getCleanHeight() * Configuration.scaleFactorY;

            batch.draw(ImageCache.getTexture("rock"),
                    (Planet.this.x + SAFE_SIDE_SIZE * BLOCK_SIZE) * Configuration.scaleFactorX + offsetX,
                    (Planet.this.y + SAFE_SIDE_SIZE * BLOCK_SIZE) * Configuration.scaleFactorY + offsetY,
                    width, height);
        }

        @Override
        public void act(float delta) {
            super.act(delta);
        }

    }

    private float getCleanHeight() {
        return getHeight() - BLOCK_SIZE * 2 * SAFE_SIDE_SIZE;
    }

    private float getCleanWidth() {
        return getWidth() - BLOCK_SIZE * 2 * SAFE_SIDE_SIZE;
    }
}
