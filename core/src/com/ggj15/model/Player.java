package com.ggj15.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.ggj15.data.ImageCache;
import com.ggj15.screen.GameScreen;

/**
 * Created by kettricken on 24.01.2015.
 */
public class Player extends Sprite {

    private static final String GRASS = "grass";

    private final float FLY_THRESHOLD = 1;
    private final float WALK_SPEED = 100f;
    private final float JUMP_ACCEL = 300f;
    private final float INK_ACCEL = 10f;

    private static final String CHAR_STAND = "char-stand";

    private float vx, vy;
    private boolean flying = false;
    private Direction gravity = Direction.DOWN;
    private Planet.Block block;

    private PlayerActor actor;

    public Player() {
        setRegion(ImageCache.getFrame(CHAR_STAND, 1));
        setSize(getRegionWidth(), getRegionHeight());
        setX(50);
        setY(500);
        setOriginCenter();

        actor = new PlayerActor();
    }

    public void process(float delta, Planet planet) {
        gravity = planet.getNewGravity(gravity, getCenterX(), getCenterY());
        processInput(planet);
        boolean hitUp = Gdx.input.isKeyJustPressed(Input.Keys.W);
        boolean holdingUp = !hitUp && Gdx.input.isKeyPressed(Input.Keys.W);

        float force = planet.getForce(getX(), getY());
        switch (gravity) {
            case DOWN:
                vy += (hitUp ? JUMP_ACCEL : 0) + (holdingUp ? INK_ACCEL : 0) - force;
                break;

            case UP:
                vy += (hitUp ? -JUMP_ACCEL : 0) + (holdingUp ? -INK_ACCEL : 0) + force;
                break;

            case LEFT:
                vx += (hitUp ? JUMP_ACCEL : 0) + (holdingUp ? INK_ACCEL : 0) - force;
                break;

            case RIGHT:
                vx += (hitUp ? -JUMP_ACCEL : 0) + (holdingUp ? -INK_ACCEL : 0) + force;
                break;
        }
        float dx = vx * delta;
        float dy = vy * delta;
        float[] ds = planet.collide(getX(), getY(),
                getRegionWidth(), getRegionHeight(), dx, dy);
        if (ds[0] != dx) vx = 0;
        if (ds[1] != dy) vy = 0;
        dx = ds[0];
        dy = ds[1];
        setX(getX() + dx);
        setY(getY() + dy);
    }

    @Override
    public void draw(Batch batch) {
        switch (gravity) {
            case DOWN:
                setRotation(0);
                break;

            case UP:
                setRotation(180);
                break;

            case LEFT:
                setRotation(270);
                break;

            case RIGHT:
                setRotation(90);
                break;
        }

        super.draw(batch);
        batch.draw(this, getX(), getY(), getOriginX(), getOriginY(), getWidth(),
                getHeight(), getScaleX(), getScaleY(), getRotation());
    }

    private void startFly() {
        flying = true;
    }

    private void processInput(Planet planet) {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            if (gravity == Direction.DOWN) {
                vx = -WALK_SPEED;
            } else if (gravity == Direction.UP) {
                vx = WALK_SPEED;
            } else if (gravity == Direction.LEFT) {
                vy = WALK_SPEED;
            } else if (gravity == Direction.RIGHT) {
                vy = -WALK_SPEED;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            if (gravity == Direction.DOWN) {
                vx = WALK_SPEED;
            } else if (gravity == Direction.UP) {
                vx = -WALK_SPEED;
            } else if (gravity == Direction.LEFT) {
                vy = -WALK_SPEED;
            } else if (gravity == Direction.RIGHT) {
                vy = +WALK_SPEED;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            if (block == null) {
                block = planet.takeBlock(gravity, getCenterX(), getCenterY());
            } else {
                Gdx.app.log("test", "trying to put block");
                boolean success = planet.putBlock(block, getCenterX(), getCenterY());
                Gdx.app.log("test", "success: " + success);
                if (!success) return;
                if (gravity == Direction.DOWN) {
                    setY(getY() + Planet.BLOCK_SIZE);
                } else if (gravity == Direction.UP) {
                    setY(getY() - Planet.BLOCK_SIZE);
                } else if (gravity == Direction.LEFT) {
                    setX(getX() + Planet.BLOCK_SIZE);
                } else if (gravity == Direction.RIGHT) {
                    setX(getX() - Planet.BLOCK_SIZE);
                }
                block = null;
            }
        }
    }

    private int getCenterX() {
        return (int) (getX() + getRegionWidth() / 2f);
    }

    private int getCenterY() {
        return (int) (getY() + getRegionHeight() / 2f);
    }

    public PlayerActor getActor() {
        return actor;
    }

    private class PlayerActor extends Actor {

        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
            batch.draw(getTexture(),
                 Player.this.getX() * GameScreen.scaleFactorX,
                 getStage().getHeight() - Player.this.getY() * GameScreen.scaleFactorY - Player.this.getHeight());
        }
    }
}
