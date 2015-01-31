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
import com.ggj15.screen.GameScreen;
import com.ggj15.utility.Utils;

/**
 * Created by st on 1/24/15.
 */
public class Planet {

    public static final int INK_AMOUNT = 10;

    private static final int DEFAULT_SIZE = 11;
    private static final int SAFE_SIDE_SIZE = 5;
    public static final int BLOCK_SIZE = 64;

    private static final float DEFAULT_GRAVITY_FORCE = 10000f;
    private static final float BASE_GRAVITY_DIVIDER = 900f;
    private static final float DEFAULT_SPEED = 10f;

    private static final Vector2 SYSTEM_CENTER = new Vector2(0, 0);

    private static final String TEXTURE_INK = "ink";

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

    public PlanetActor actor;

    private int width, height;
    private float x, y;
    private float dx, dy;
    private float minX, minY, maxX, maxY;
    private Block[][] tiles;
    private boolean[][] inked;
    private Rectangle objRect, blockRect, intersectionRect, planetRectangle;
    private float force;
    private float speed;
    private float orbitRadius;
    private Orbiter.Orbit orbit;

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

    public float getDx(){
        return dx;
    }

    public float getDy() {
        return dy;
    }

    public float getSpeed() {
        return speed;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getCenterX(){
        return (x + getWidth() / 2) * 1;
    }

    public float getCenterY(){
        return (y + getHeight() / 2) * 1;
    }

    public float getOrbitRadius() {
        return orbitRadius;
    }

    public Orbiter.Orbit getOrbit() {
        return orbit;
    }

    public PlanetActor getActor() {
        return actor;
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
        return DEFAULT_GRAVITY_FORCE / (float) (Math.sqrt(diffX * diffX + diffY * diffY) + BASE_GRAVITY_DIVIDER);
    }

    public void move(float dx, float dy) {
        setPosition(getX() + dx, getY() + dy);
    }

    public void setCenteredPosition(float x, float y) {
        setPosition(x - getWidth() / 2f + x, y - getHeight() / 2f);
    }

    public void draw(float delta, SpriteBatch batch) {
        TextureRegion ink = ImageCache.getTexture(TEXTURE_INK);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Block block = tiles[i][j];
                if (block == null) continue;
                int x = (int) (j * BLOCK_SIZE + this.x);
                int y = (int) (i * BLOCK_SIZE + this.y);
                batch.draw(block.getTexture(), x, y, BLOCK_SIZE, BLOCK_SIZE);
                if (inked[i][j]) batch.draw(ink, x, y);
            }
        }
    }

    public float getInkAmount(Direction gravity, int x, int y) {
        int idx = getHorizontalIndex(x) + Direction.getDx(gravity);
        int idy = getVerticalIndex(y) + Direction.getDy(gravity);
        if(idy > inked.length || idy < 0 || idx <0 || idx > inked[0].length){
            return INK_AMOUNT;
        }
        inked[idy][idx] = false;
        return INK_AMOUNT;
    }

    public boolean hasInk(Direction gravity, int x, int y) {
        int idx = getHorizontalIndex(x) + Direction.getDx(gravity);
        int idy = getVerticalIndex(y) + Direction.getDy(gravity);
        if(idy > inked.length || idy < 0 || idx <0 || idx > inked[0].length){
            return false;
        }
        return inked[idy][idx];
    }

    public Block takeBlock(Direction gravity, int x, int y) {
        int idx = getHorizontalIndex(x) + Direction.getDx(gravity);
        int idy = getVerticalIndex(y) + Direction.getDy(gravity);
        return takeBlockTiled(idx, idy);
    }

    public Block takeBlock(float x, float y) {
        int idx = getHorizontalIndex(x);
        int idy = getVerticalIndex(y);
        return takeBlockTiled(idx, idy);
    }

    private Block takeBlockTiled(int x, int y) {
        if(y >= height || y < 0 || x <0 || x >= width){
            return null;
        }
        Block block = tiles[y][x];
        if (block != null) {
            tiles[y][x] = null;
            calcLimits();
        }
        return block;
    }

    public boolean hasBlock(float x, float y, Direction gravity) {
        int idx = getHorizontalIndex(x) + Direction.getDx(gravity);
        int idy = getVerticalIndex(y) + Direction.getDy(gravity);
        return idx >= 0 && idx < width && idy >=0 && idy < height && tiles[idy][idx] != null;
    }

    public Vector2 getBlockCoordinates(float x, float y) {
        return new Vector2(getHorizontalIndex(x) * BLOCK_SIZE + this.x, getVerticalIndex(y) * BLOCK_SIZE + this.y);
    }

    public boolean putBlock(Block block, int x, int y) {
        int idx = getHorizontalIndex(x);
        int idy = getVerticalIndex(y);
        if (idx >=0 && idy >= 0 && idx < width && idy < height
                && tiles[idy][idx] == null) {
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
                    if (idy > maxY) return Direction.DOWN;
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

    public static class Builder {

        private static final int DEFAULT_INKED_COUNT = 10;
        private static final float DEFAULT_ORBIT_RADIUS = 1000f;
        private static final Orbiter.Orbit DEFAULT_ORBIT = Orbiter.Orbit.CLOCKWISE;

        public enum PlanetType {ROCK, GRASS};

        private Planet instance;
        private int inkedCount;
        private PlanetType planetType;
        private Orbiter.Orbit orbit;

        public Builder() {
            instance = new Planet();
            instance.width = DEFAULT_SIZE + SAFE_SIDE_SIZE * 2;
            instance.height = DEFAULT_SIZE + SAFE_SIDE_SIZE * 2;
            instance.force = DEFAULT_GRAVITY_FORCE;
            instance.speed = DEFAULT_SPEED;
            instance.orbitRadius = DEFAULT_ORBIT_RADIUS;
            instance.orbit = DEFAULT_ORBIT;
            instance.setCenteredPosition(instance.orbitRadius, instance.orbitRadius);

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

        public Builder orbit(Orbiter.Orbit orbit) {
            instance.orbit = orbit;
            return this;
        }

        public Builder locate(Vector2 pos) {
            return locate(pos.x, pos.y);
        }

        public Builder locate(float x, float y) {
            instance.setCenteredPosition(x, y);
            return this;
        }

        public Builder speed(float speed) {
            instance.speed = speed;
            return this;
        }

        public Builder orbitRadius(float orbitRadius) {
            instance.orbitRadius = orbitRadius;
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

            inkedCount = (int) instance.orbitRadius / 100;
            if(inkedCount > (instance.width - 2 * SAFE_SIDE_SIZE) * (instance.height - 2 * SAFE_SIDE_SIZE)) {
                inkedCount = (instance.width - 2 * SAFE_SIDE_SIZE) * (instance.height - 2 * SAFE_SIDE_SIZE);
            }

            instance.inked = new boolean[instance.height][instance.width];
            for (int i = inkedCount; i >= 0 ;) {
                int x = Utils.getRandom().nextInt(instance.width - 2 * SAFE_SIDE_SIZE) + SAFE_SIDE_SIZE;
                int y = Utils.getRandom().nextInt(instance.height - 2 * SAFE_SIDE_SIZE) + SAFE_SIDE_SIZE;

                assert(instance.tiles[y][x] != null);
                if(!instance.inked[y][x]) {
                    instance.inked[y][x] = true;
                    i--;
                }
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
                    if(getBoundaryLevel(i,j) == 0) {
                        if(Utils.getRandom().nextInt(10) != 0) {
                            instance.tiles[i][j] = Block.GRASS;
                        } else {
                            instance.tiles[i][j] = Block.TERRA;
                        }
                    } else if (getBoundaryLevel(i,j) > 0 && getBoundaryLevel(i,j) < 3) {
                        if(Utils.getRandom().nextInt(4) != 0) {
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

        public int getBoundaryLevel(int i, int j) {
            int[] boundaryLevels = new int[] {
                    Math.abs(i - SAFE_SIDE_SIZE),
                    Math.abs(j - SAFE_SIDE_SIZE),
                    Math.abs(i - instance.height + SAFE_SIDE_SIZE + 1),
                    Math.abs(j - instance.width + SAFE_SIDE_SIZE + 1)
            };
            int min = boundaryLevels[0];
            for (int k = 1; k < 4; k++) {
                if (min > boundaryLevels[k]) {
                    min = boundaryLevels[k];
                }
            }
            return min;
        }
    }

    public class PlanetActor extends Actor {

        TextureRegion textureRegion;

        public  PlanetActor() {
            textureRegion = ImageCache.getTexture("planet-on-map-square");
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);

            float offsetX = getStage().getWidth() / 2f;
            float offsetY = getStage().getHeight() / 2f;

            float width = Planet.this.getCleanWidth() * Configuration.scaleFactorX;
            float height = Planet.this.getCleanHeight() * Configuration.scaleFactorY;

            batch.draw(textureRegion,
                    (Planet.this.x + SAFE_SIDE_SIZE * BLOCK_SIZE) * Configuration.scaleFactorX + offsetX,
                    (Planet.this.y + SAFE_SIDE_SIZE * BLOCK_SIZE) * Configuration.scaleFactorY + offsetY,
                    width, height);
        }

        @Override
        public void act(float delta) {
            super.act(delta);
        }

    }

    public float getCleanHeight() {
        return getHeight() - BLOCK_SIZE * 2 * SAFE_SIDE_SIZE;
    }

    public float getCleanWidth() {
        return getWidth() - BLOCK_SIZE * 2 * SAFE_SIDE_SIZE;
    }

    public float getCleanX() {
        return x + BLOCK_SIZE * SAFE_SIDE_SIZE;
    }

    public  float getCleanY() {
        return y + BLOCK_SIZE * SAFE_SIDE_SIZE;
    }
}
