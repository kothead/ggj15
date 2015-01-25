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

/**
 * Created by kettricken on 24.01.2015.
 */
public class Player extends Sprite {

    private static final int MAX_INK_LEVEL = 100;
    private static final float INK_DECREASE = 0.5f;

    private static final int DEFAULT_HEIGHT = 64;
    private final float SIZE = 60;

    private final float TIME_HOLD_BEFORE_FLY = 0.5f;
    private final float FLY_THRESHOLD = 1;
    private final float WALK_SPEED = 100f;
    private final float JUMP_ACCEL = 300f;
    private final float INK_ACCEL = 30f;
    private final float START_INK_LEVEL = 25;

    enum State {
        WALK("char-walking", 4, 0.1f),
        WALK_HOLD("char-hold-walk", 2, 0.2f),
        STAND_HOLD("char-hold-stand", 1, 0),
        STAND("char-stand", 2, 0.2f),
        SUCK("char-suck", 1, 0),
        JUMP("char-jump", 1, 0),
        JUMP_HOLD("char-hold-jump", 1, 0),
        FLY("char-fly", 3, 0.1f);

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

    private float vx, vy;
    private boolean flying = false;
    private Direction gravity = Direction.DOWN;
    private Planet.Block block;
    private boolean standing = false;
    private float timeHoldingUp = 0;

    private PlayerActor actor;

    private State state;
    private float stateTime;

    private float inkLevel = 0;

    public Player() {
        inkLevel = START_INK_LEVEL;
        setState(State.STAND);
        actor = new PlayerActor();
    }

    public float getInkLevel() {
        return inkLevel;
    }

    public void process(float delta, Array<Planet> planets) {
        updateState(delta);

        processInputOnce(delta);
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

        float dx = 0;
        float dy = 0;
        if (planet != null) {
            gravity = planet.getNewGravity(gravity, getCenterX(), getCenterY());
            standing = planet.hasBlock(getCenterX(), getCenterY(), gravity);
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
//            dx = planet.getDx();
//            dy = planet.getDy();
        }

        dx += vx * delta;
        dy += vy * delta;
        for (Planet cur: planets) {
            float[] ds = cur.collide(getX(), getY(),
                    getWidth(), getHeight(), dx, dy);
            if (ds[0] != dx) vx = 0;
            if (ds[1] != dy) vy = 0;
            dx = ds[0];
            dy = ds[1];
        }
        setX(getX() + dx);
        setY(getY() + dy);

        setRotation();
    }

    public boolean isAlive() {
        return inkLevel > 0;
    }

    @Override
    public void draw(Batch batch) {
        setRegion(getStateFrame());
        //setSize(getRegionWidth(), getRegionHeight());

        // for jumping/flying animation with non-standart height
        setSize(SIZE, getRegionHeight() == DEFAULT_HEIGHT ? SIZE : getRegionHeight());
        setOrigin(SIZE / 2f, SIZE / 2f);
        super.draw(batch);

        if (block != null) {
            float x = getX() + Direction.getDx(gravity.getOpposite()) * Planet.BLOCK_SIZE;
            float y = getY() + Direction.getDy(gravity.getOpposite()) * Planet.BLOCK_SIZE;
            batch.draw(block.getTexture(), x, y);
        }
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

    private void processInputOnce(float delta) {
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

        boolean hitUp = Gdx.input.isKeyJustPressed(Input.Keys.W) && standing;
        boolean holdingUp = !hitUp && Gdx.input.isKeyPressed(Input.Keys.W) && block == null;
        flying = false;
        if (holdingUp) {
            timeHoldingUp += delta;
            flying = timeHoldingUp > TIME_HOLD_BEFORE_FLY && inkLevel > 0;
        } else {
            timeHoldingUp = 0;
        }

        switch (gravity) {
            case DOWN:
                vy += (hitUp ? JUMP_ACCEL : 0) + (flying ? INK_ACCEL : 0);
                break;

            case UP:
                vy += (hitUp ? -JUMP_ACCEL : 0) + (flying ? -INK_ACCEL : 0);
                break;

            case LEFT:
                vx += (hitUp ? JUMP_ACCEL : 0) + (flying ? INK_ACCEL : 0);
                break;

            case RIGHT:
                vx += (hitUp ? -JUMP_ACCEL : 0) + (flying ? -INK_ACCEL : 0);
                break;
        }
        if (flying && inkLevel > 0) {
            inkLevel -= INK_DECREASE;
        }
    }

    private void processInput(Planet planet) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            if (block == null) {
                if (planet.hasInk(gravity, getCenterX(), getCenterY())){
                    inkLevel = Math.min(inkLevel + planet.getInkAmount(gravity, getCenterX(), getCenterY()), MAX_INK_LEVEL);
                } else {
                    block = planet.takeBlock(gravity, getCenterX(), getCenterY());
                }
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
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            if (block != null) {
                setState(State.JUMP_HOLD);
            } else {
                setState(State.JUMP);
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            if (block != null) {
                setState(State.JUMP_HOLD);
            } else if (flying) {
                setState(State.FLY);
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)
                || Gdx.input.isKeyPressed(Input.Keys.D)) {
            if (block != null) {
                setState(State.WALK_HOLD);
            } else {
                setState(State.WALK);
            }
        } else {
            if (block != null) {
                setState(State.STAND_HOLD);
            } else {
                setState(State.STAND);
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
