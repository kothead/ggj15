package com.ggj15.model;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.ggj15.data.ImageCache;

/**
 * Created by kettricken on 24.01.2015.
 */
public class HoleIndicator extends Actor {

    private Sprite sprite;

    public HoleIndicator() {
        sprite = new Sprite(ImageCache.getTexture("black-hole-index"));
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
    }
}
