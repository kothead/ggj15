package com.ggj15.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ggj15.utility.Utils;

/**
 * Created by Тимофей on 01.02.2015.
 */
public class Orbiter {

    private Array<Planet> planets;
    private Array<Orbit> orbits;

    public Orbiter() {
        planets = new Array<Planet>();
        orbits = new Array<Orbit>();
    }

    public int add(Planet planet, Orbit orbit) {
        int index = getCount();
        planets.add(planet);
        orbits.add(orbit);
        return index;
    }

    public void process(float delta) {
        for (int i = 0; i < getCount(); i++) {
            orbits.get(i).process(delta, planets.get(i));
        }
    }

    private int getCount() {
        return planets.size;
    }
}

