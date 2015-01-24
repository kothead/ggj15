package com.ggj15.model;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.ggj15.data.Configuration;
import com.ggj15.data.ImageCache;

import java.util.Iterator;

/**
 * Created by kettricken on 24.01.2015.
 */
public class Hole extends Sprite {

    private static final float EXPAND_SPEED = 20;
    private static final float DEFAULT_GRAVITY_FORCE = 4000f;
    private static final float DEFAULT_GRAVITY_DIST = 200f;
    public static final int BLOCK_SPEED = 1;

    private final Vector2 center;

    Array<Planet> planets;
    Array<Planet.Block> blocks = new Array<Planet.Block>();
    Array<Vector2> blocksPositions = new Array<Vector2>();

    Rectangle forceRectangle = new Rectangle();
    Rectangle tmpRectangle = new Rectangle();

    PlanetActor actor;

    public Hole(Array<Planet> planets) {
        super();
        this.planets = planets;
        center = new Vector2(Configuration.worldMaxWidth / 2, Configuration.worldMaxHeight / 2);

        setX(center.x - getWidth() / 2);
        setY(center.y - getHeight() / 2);

        forceRectangle.setX(getX() - DEFAULT_GRAVITY_DIST);
        forceRectangle.setY(getY() - DEFAULT_GRAVITY_DIST);
        forceRectangle.setWidth(getWidth() + 2 * DEFAULT_GRAVITY_DIST);
        forceRectangle.setHeight(getHeight() + 2 * DEFAULT_GRAVITY_DIST);

        setRegion(ImageCache.getTexture("terra"));
        actor = new PlanetActor();
    }

    public void draw(ShapeRenderer shapeRenderer, float delta) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //shapeRenderer.setColor(0.5f, 0, 0.5f, 1);
        //shapeRenderer.rect(forceRectangle.getX(), forceRectangle.getY(), forceRectangle.getWidth(), forceRectangle.getHeight());
        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());
        //shapeRenderer.setColor(1, 0, 0, 1);
        //shapeRenderer.rect(tmpRectangle.getX(), tmpRectangle.getY(), tmpRectangle.getWidth(), tmpRectangle.getHeight());
        shapeRenderer.end();
    }

    public void draw(float delta, SpriteBatch batch) {
        Iterator<Vector2> blockCoordsIterator = blocksPositions.iterator();
        Iterator<Planet.Block> blockIterator = blocks.iterator();
        while (blockCoordsIterator.hasNext()) {
            Vector2 position = blockCoordsIterator.next();
            Planet.Block block = blockIterator.next();
            boolean contains = getBoundingRectangle().contains(new Rectangle(position.x, position.y,
                    Planet.BLOCK_SIZE, Planet.BLOCK_SIZE));
            if ( contains) {
                blockCoordsIterator.remove();
                blockIterator.remove();
            } else {
                batch.draw(block.getTexture(), position.x, position.y);
            }
        }
    }

    public void process(float delta) {
        setSize(getWidth() + EXPAND_SPEED * delta, getHeight() + EXPAND_SPEED * delta);
        setX(getX() - EXPAND_SPEED * delta / 2);
        setY(getY() - EXPAND_SPEED * delta / 2);

        forceRectangle.setSize(getWidth() + 2 * DEFAULT_GRAVITY_DIST, getHeight() + 2 * DEFAULT_GRAVITY_DIST);
        forceRectangle.setX(getX() - DEFAULT_GRAVITY_DIST);
        forceRectangle.setY(getY() - DEFAULT_GRAVITY_DIST);

        for (Planet planet : planets) {
            boolean intersects = Intersector.intersectRectangles(forceRectangle, planet.getRectangle(), tmpRectangle);
            if (intersects) {
                float x = MathUtils.random(tmpRectangle.getX(), tmpRectangle.getWidth() + tmpRectangle.getX());
                float y = MathUtils.random(tmpRectangle.getY(), tmpRectangle.getHeight() + tmpRectangle.getY());
                Vector2 coordinates = planet.getBlockCoordinates(x, y);
                Planet.Block block = planet.takeBlock(x, y);
                if (block != null) {
                    blocks.add(block);
                    blocksPositions.add(coordinates);
                }
            }
        }

        for (Vector2 currentPoint : blocksPositions) {
            float diff = (float) Math.sqrt(Math.pow(currentPoint.x - center.x, 2) + Math.pow(currentPoint.y - center.y, 2));
            currentPoint.x = currentPoint.x + BLOCK_SPEED / diff * (center.x - currentPoint.x);
            currentPoint.y = currentPoint.y + BLOCK_SPEED / diff * (center.y - currentPoint.y);
        }
    }

    public PlanetActor getActor() {
        return actor;
    }

    private class PlanetActor extends Actor {

        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
            batch.draw(Hole.this,
                    Hole.this.getX() * Configuration.scaleFactorX,
                    Hole.this.getY() * Configuration.scaleFactorY,
                    Hole.this.getWidth() * Configuration.scaleFactorX,
                    Hole.this.getHeight() * Configuration.scaleFactorY);
        }
    }
}
