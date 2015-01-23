package com.ggj15.model;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ggj15.data.Direction;

/**
 * Created by kettricken on 24.01.2015.
 */
public class Player extends Sprite {

    private final float FLY_THRESHOLD = 1;
    private final float speed = 50;
    private final float jumpHeight = 70;

    float vx, vy;

    float upPressedTime;
    float maxHeight = -1;

    boolean goesUp = false;
    boolean goesDown = false;
    boolean isUpPressed = false;
    boolean flying = false;

    Direction gravity;

    public Player(TextureRegion texture) {
        setRegion(texture);
        setX(100);
        setY(40);
    }

    public void draw(float delta, SpriteBatch batch) {
        if (flying) {

        } else {
            if (isUpPressed)
                upPressedTime += delta;
            checkJumping();
            if (upPressedTime > FLY_THRESHOLD) {
                startFly();
            }
        }

        setX(getX() + vx * delta);
        setY(getY() + vy * delta);

        batch.draw(getTexture(), getX(), getY());
    }

    private void startFly() {
        flying = true;
        goesUp = false;
        vy = 100;
    }

    public void stopLeft() {
        if (gravity.getOpposite() == Direction.BOTTOM)
            vx += speed;
        else if (gravity.getOpposite() == Direction.TOP)
            vx -= speed;

    }

    public void stopRight() {
        if (gravity.getOpposite() == Direction.BOTTOM)
            vx -= speed;
        else if (gravity.getOpposite() == Direction.TOP)
            vx += speed;
    }

    public void goLeft() {
        if (gravity.getOpposite() == Direction.BOTTOM)
            vx -= speed;
        else if (gravity.getOpposite() == Direction.TOP)
            vx += speed;
    }

    public void goRight() {
        if (gravity.getOpposite() == Direction.BOTTOM)
            vx += speed;
        else if (gravity.getOpposite() == Direction.TOP)
            vx -= speed;
    }

    public void goesUp() {
        if (gravity.getOpposite() == Direction.BOTTOM)
            jump();
        else if (gravity.getOpposite() == Direction.TOP)
            take();
        else if (gravity.getOpposite() == Direction.LEFT)
            vy += speed;
        else if (gravity.getOpposite() == Direction.RIGHT)
            vy -= speed;
    }

    private void take() {

    }

    private void jump() {
        maxHeight = getY() + jumpHeight;
        vy = jumpHeight;
        goesUp = true;
        upPressedTime = 0;
        isUpPressed = true;
    }

    public void stopGoingUp(){
        isUpPressed = false;
    }

    private void checkJumping() {
        if (getY() >= maxHeight && goesUp) {
            vy = -jumpHeight;
            goesDown = true;
            goesUp = false;
            maxHeight = 0;
        } else if (!canGoDown() && goesDown) {
            vy = 0;
            goesDown = false;
        }
    }

    private boolean canGoDown() {
        //TODO check collision
        if (getY() >= 40)
            return true;
        return false;
    }
}
