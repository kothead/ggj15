package com.ggj15.model;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.ggj15.data.Configuration;
import com.ggj15.data.ImageCache;

/**
 * Created by kettricken on 24.01.2015.
 */
public class HoleIndicator extends Actor {

    private Sprite sprite;
    private ShapeRenderer shapeRenderer;
    private float distance = 0;

    public HoleIndicator() {
        shapeRenderer = new ShapeRenderer();
        sprite = new Sprite(ImageCache.getTexture("hole-indicator"));
        this.setWidth(sprite.getWidth());
        this.setHeight(sprite.getHeight());
        this.setBounds(0, 0, this.getWidth(), this.getHeight());
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        sprite.setPosition(this.getX(), this.getY());
        sprite.setOrigin(this.getOriginX(), this.getOriginY());
        sprite.setRotation(this.getRotation());
        sprite.setScale(this.getScaleX(), this.getScaleY());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        sprite.draw(batch);
        batch.end();
        shapeRenderer.setProjectionMatrix(getStage().getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 1);
        float width = sprite.getWidth() - distance;
        shapeRenderer.rect(sprite.getX(), sprite.getY(), width, sprite.getHeight());
        shapeRenderer.end();
        batch.begin();
    }


    public void updateHoleDistance(float diff) {
        float percent = diff / Math.abs(Configuration.worldMinWidth) + Configuration.worldMaxWidth;
        this.distance = sprite.getWidth() * percent;
    }
}
