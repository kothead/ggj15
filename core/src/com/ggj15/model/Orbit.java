package com.ggj15.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.ggj15.utility.Utils;

/**
 * Created by Тимофей on 01.02.2015.
 */
public class Orbit {

    private static final float DEFAULT_SPEED = 300f;
    private static final float DEFAULT_ORBIT_RADIUS = 1200f;
    private static final Vector2 SYSTEM_CENTER = new Vector2(0, 0);

    private enum OrbitDirection{
        DOWN(0,-1), RIGHT(1,0), UP(0,1), LEFT(-1,0);
        private Vector2 speedDirection;

        OrbitDirection(float multiplierX, float multiplierY) {
            speedDirection = new Vector2(multiplierX, multiplierY);
        }
    }

    private Vector2 speedDirection;
    private boolean isClockwise;

    private float speed;
    private float orbitRadius;
    private Vector2 initialCenter;


    public void process(float delta, Planet planet){
        Vector2 center = new Vector2(planet.getCenterX(),planet.getCenterY());

        float distance = speed*delta;

        Vector2 nextPos = new Vector2(center).add(new Vector2(speedDirection).scl(speed * delta));

        if(Math.abs(nextPos.x)+Math.abs(nextPos.y) > orbitRadius*2){
            // rotate
            float oldPath = distance - (Math.abs(nextPos.x)+Math.abs(nextPos.y) - orbitRadius*2);
            center.add(new Vector2(speedDirection).scl(oldPath));
            speedDirection.rotate90(isClockwise?-1:1);
            center.add(new Vector2(speedDirection).scl(distance - oldPath));
        } else {
            // move forward
            center.add(new Vector2(speedDirection).scl(speed * delta));
        }
        planet.setCenterPosition(center.x, center.y);

        if(Math.abs(Math.abs(center.x)-orbitRadius) > 0.001 && Math.abs(Math.abs(center.y)-orbitRadius) >0.001){
            Gdx.app.log("Bad state", "Orbit is lost " + center.toString());
        }
    }

    public Vector2 getSpeedDirection() {
        return speedDirection;
    }

    public boolean isClockwise() {
        return isClockwise;
    }

    public float getSpeed() {
        return speed;
    }

    public float getOrbitRadius() {
        return orbitRadius;
    }

    public Vector2 getInitialCenter(){
        return initialCenter;
    }

    private static OrbitDirection getOrbitDirection(Vector2 center) {
        OrbitDirection orbitDirection;
        if(center.y<=center.x && center.y>-center.x){
            orbitDirection =  OrbitDirection.DOWN;

        } else if(center.y<center.x && center.y<=-center.x){
            orbitDirection =  OrbitDirection.LEFT;
        } else if(center.y>=center.x && center.y<-center.x){
            orbitDirection =  OrbitDirection.UP;
        } else {
            orbitDirection =  OrbitDirection.RIGHT;
        }
        return orbitDirection;
    }

    public static class Builder {

        private Orbit instance;

        public Builder() {
            instance = new Orbit();
            instance.speed = DEFAULT_SPEED;
            instance.orbitRadius = DEFAULT_ORBIT_RADIUS;
            instance.initialCenter = new Vector2(SYSTEM_CENTER.x - instance.orbitRadius, SYSTEM_CENTER.y - instance.orbitRadius);
            instance.speedDirection = instance.getOrbitDirection(instance.initialCenter).speedDirection.cpy();
            instance.isClockwise = true;
        }

        public Builder speed(float speed) {
            instance.speed = speed;
            assert (speed > 0);
            return this;
        }

        public Builder orbitRadius(float orbitRadius) {
            instance.orbitRadius = orbitRadius;

            int x = Utils.getRandom().nextInt(3) - 1;
            int y = Utils.getRandom().nextInt(3) - 1;

            if (x == y && x == 0) {
                x = 1;
            }

            assert (-2 < x && x < 2 && -2 < y && y < 2);

            instance.initialCenter = new Vector2(SYSTEM_CENTER.x - instance.orbitRadius * x, SYSTEM_CENTER.y - instance.orbitRadius * y);
            instance.speedDirection = instance.getOrbitDirection(instance.initialCenter).speedDirection.cpy();
            return this;
        }

        /**
         * @param isClockwise
         * @return
         */
        public Builder clockwise(boolean isClockwise) {
            instance.isClockwise = isClockwise;
            if (!isClockwise) {
                instance.speedDirection.rotate90(1).rotate90(1);
            }
            return this;
        }

        public Orbit build() {
            return instance;
        }

    }
}
