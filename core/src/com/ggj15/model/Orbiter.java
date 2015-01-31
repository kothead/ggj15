package com.ggj15.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ggj15.utility.Utils;

/**
 * Created by st on 1/26/15.
 */
public class Orbiter {

    private static final float FIRST_PLANET_ORBIT_DISTANCE = 200f;
    private static final float MIN_ORBIT_DISTANCE = 10f;
    private static final float VARIABLE_ORBIT_DISTANCE = 10f;
    private static final float DEFAULT_SYSTEM_CENTER_X = 0f;
    private static final float DEFAULT_SYSTEM_CENTER_Y = 0f;

    public enum Orbit {
        CLOCKWISE, COUNTERCLOCKWISE
    }

    private static final Direction[] BASE_ORBIT = {
        Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT
    };

    private Array<Integer> sides;
    private Array<Planet> planets;
    private Vector2 systemCenter;

    public Orbiter() {
        planets = new Array<Planet>();
        sides = new Array<Integer>();
        systemCenter = new Vector2(DEFAULT_SYSTEM_CENTER_X, DEFAULT_SYSTEM_CENTER_Y);
    }

    public void moveSystemCenter(float x, float y) {
        Vector2 pos = new Vector2(x, y);
        pos.add(systemCenter);
        setSystemCenter(pos);
    }

    public void setSystemCenter(float x, float y) {
        Vector2 pos = new Vector2(x, y);
        setSystemCenter(pos);
    }

    public void setSystemCenter(Vector2 pos) {
        for (Planet planet: planets) {
            planet.setPosition(planet.getX() - systemCenter.x, planet.getY() - systemCenter.y);
            planet.setPosition(planet.getX() + pos.x, planet.getY() + pos.y);
        }
        systemCenter = pos;
    }

    /**
     * calculates available orbit
     * @param width of planet
     * @param height of planet
     * @return radius of orbit to the center of a planet
     */
    public float getAvailableOrbit(float width, float height) {
        float inner = MIN_ORBIT_DISTANCE;
        if (planets.size > 0) {
            Planet planet = planets.get(planets.size - 1);
            inner += planet.getOrbitRadius() + Math.max(planet.getWidth() / 2f, planet.getHeight() / 2f);
        } else {
            inner += FIRST_PLANET_ORBIT_DISTANCE;
        }
        inner += Utils.getRandom().nextFloat() * VARIABLE_ORBIT_DISTANCE;
        inner += Math.max(width / 2f, height / 2f);
        return inner;
    }

    public int add(Planet planet) {
        int index = getCount();
        planets.add(planet);
        sides.add(0);
        return index;
    }

    public void locateOnOrbit(int index) {
        Planet planet = planets.get(index);
        int side = Utils.getRandom().nextInt(BASE_ORBIT.length);
        sides.set(index, side);
        Direction direction = BASE_ORBIT[side];

        float x = planet.getOrbitRadius() * Direction.getDx(direction);
        float y = planet.getOrbitRadius() * Direction.getDy(direction);

        float variate = Utils.getRandom().nextFloat() * planet.getOrbitRadius() * 2
                - planet.getOrbitRadius();
        if (x == 0) {
            x = variate;
        } else {
            y = variate;
        }

        planet.setCenteredPosition(x, y);
    }

    public void remove(Planet planet) {
        int index = planets.indexOf(planet, true);
        planets.removeIndex(index);
        sides.removeIndex(index);
    }

    public void process(float delta) {
        for (int i = 0; i < getCount(); i++) {
            Planet planet = planets.get(i);
            Direction direction = getCurrentDirection(i);
            float dx = Direction.getDx(direction) * planet.getSpeed() * delta;
            float dy = Direction.getDy(direction) * planet.getSpeed() * delta;

            float overX = getOverrun(planet.getCenterX(), dx, planet.getOrbitRadius());
            float overY = getOverrun(planet.getCenterY(), dy, planet.getOrbitRadius());

            planet.move(dx - overX, dy - overY);

            if (overX != 0 || overY != 0) {
                setNextDirection(i);
                direction = getCurrentDirection(i);
                dx = Direction.getDx(direction) * Math.abs(overX + overY);
                dy = Direction.getDy(direction) * Math.abs(overX + overY);
                planet.move(dx, dy);
            }
        }
    }

    private Direction getCurrentDirection(int index) {
        return BASE_ORBIT[sides.get(index)];
    }

    private float getOverrun(float value, float delta, float orbit) {
        float result = Math.abs(value + delta) - orbit;
        if (result < 0) result = 0;
        return result * Math.signum(delta);
    }

    private void setNextDirection(int index) {
        Planet planet = planets.get(index);
        int side = sides.get(index);
        if (planet.getOrbit() == Orbit.CLOCKWISE) {
            side++;
            if (side >= BASE_ORBIT.length) side = 0;
        } else {
            side--;
            if (side < 0) side = BASE_ORBIT.length - 1;
        }
        sides.set(index, side);
    }

    private int getCount() {
        return planets.size;
    }
}
