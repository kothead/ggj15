package com.ggj15.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.ggj15.data.Configuration;
import com.ggj15.data.ImageCache;

import javax.xml.soap.Text;
import java.awt.*;

/**
 * Created by kettricken on 24.01.2015.
 */
public class Player extends Sprite {

    enum State {
        WALK("char-walking", 4, 0.1f),
        STAND("char-stand", 2, 0.2f),
        SUCK("char-suck", 1, 0);

        private boolean animated;
        private Animation animation;
        private TextureRegion region;

        State(String texture, int count, float duration) {
            if (count > 1) {
                animated = true;
                animation = new Animation(duration, ImageCache.getFrames(texture, 1, count));
            } else {
                region = ImageCache.getTexture(texture);
            }
        }

        public TextureRegion getFrame(float stateTime) {
            if (animated) {
                return animation.getKeyFrame(stateTime, true);
            } else {
                return region;
            }
        }
    }

    private final float FLY_THRESHOLD = 1;
    private final float WALK_SPEED = 100f;
    private final float JUMP_ACCEL = 300f;
    private final float INK_ACCEL = 10f;

    private float vx, vy;
    private boolean flying = false;
    private Direction gravity = Direction.DOWN;
    private Planet.Block block;

    private PlayerActor actor;

    private State state;
    private float stateTime;

    public Player() {
        setState(State.STAND);
        setX(50);
        setY(500);

        actor = new PlayerActor();
    }

    public void process(float delta, Array<Planet> planets) {
        updateState(delta);
        processInputOnce();
        processState();

        float force = 0;
        Planet planet = null;
        for (Planet cur: planets) {
            float temp = cur.getForce(getX(), getY());
            if (temp > force) {
                force = temp;
                planet = cur;
            }
        }

        if (planet != null) {
            gravity = planet.getNewGravity(gravity, getCenterX(), getCenterY());
            processInput(planet);

            switch (gravity) {
                case DOWN:
                    vy -= force;
                    break;

                case UP:
                    vy += force;
                    break;

                case LEFT:
                    vx -= force;
                    break;

                case RIGHT:
                    vx += force;
                    break;
            }
        }

        float dx = vx * delta;
        float dy = vy * delta;
        for (Planet cur: planets) {
            float[] ds = cur.collide(getX(), getY(),
                    getRegionWidth(), getRegionHeight(), dx, dy);
            if (ds[0] != dx) vx = 0;
            if (ds[1] != dy) vy = 0;
            dx = ds[0];
            dy = ds[1];
        }
        setX(getX() + dx);
        setY(getY() + dy);

        setRotation();
    }

    @Override
    public void draw(Batch batch) {
        setRegion(getStateFrame());
        setSize(getRegionWidth(), getRegionHeight());
        setOriginCenter();
        super.draw(batch);
    }

    private void setRotation() {
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
    }

    private void startFly() {
        flying = true;
    }

    private void setState(State state) {
        if (this.state != state) {
            this.state = state;
            stateTime = 0;
        }
    }

    private void updateState(float delta) {
        stateTime += delta;
    }

    private TextureRegion getStateFrame() {
        return state.getFrame(stateTime);
    }

    private void processInputOnce() {
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

        if (!Gdx.input.isKeyPressed(Input.Keys.D) && !Gdx.input.isKeyPressed(Input.Keys.A)
                || Gdx.input.isKeyPressed(Input.Keys.S)) {
            if (gravity == Direction.DOWN || gravity == Direction.UP) {
                vx = 0;
            } else {
                vy = 0;
            }
        }

        boolean hitUp = Gdx.input.isKeyJustPressed(Input.Keys.W);
        boolean holdingUp = !hitUp && Gdx.input.isKeyPressed(Input.Keys.W);

        switch (gravity) {
            case DOWN:
                vy += (hitUp ? JUMP_ACCEL : 0) + (holdingUp ? INK_ACCEL : 0);
                break;

            case UP:
                vy += (hitUp ? -JUMP_ACCEL : 0) + (holdingUp ? -INK_ACCEL : 0);
                break;

            case LEFT:
                vx += (hitUp ? JUMP_ACCEL : 0) + (holdingUp ? INK_ACCEL : 0);
                break;

            case RIGHT:
                vx += (hitUp ? -JUMP_ACCEL : 0) + (holdingUp ? -INK_ACCEL : 0);
                break;
        }
    }

    private void processInput(Planet planet) {
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

    private void processState() {
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            setState(State.SUCK);
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)
                || Gdx.input.isKeyPressed(Input.Keys.D)) {
            setState(State.WALK);
        } else {
            setState(State.STAND);
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

        private TextureRegion textureRegion;

        public PlayerActor() {
            textureRegion = ImageCache.getTexture("octopus-icon");
            setSize(textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
            setOrigin(textureRegion.getRegionWidth() / 2f,
                    textureRegion.getRegionHeight() / 2f);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            setRotation(Player.this.getRotation());


            float x = Player.this.getX() * Configuration.scaleFactorX;
            float y = Player.this.getY() * Configuration.scaleFactorY;

            float offsetX = getStage().getWidth() / 2f;
            float offsetY = getStage().getHeight() / 2f;

            setPosition(x + offsetX, y + offsetY);

            /*
             * TODO: do something with this wtf
             */
            if (gravity == Direction.RIGHT)
                x -= getWidth() / 2f;
            if (gravity == Direction.UP)
                y -= getHeight() / 2f;

            batch.draw(textureRegion,
                 x + offsetX, y + offsetY,
                 getOriginX(),
                 getOriginY(),
                 getWidth(),
                 getHeight(),
                 getScaleX(),
                 getScaleY(),
                 getRotation());
        }
    }
}
