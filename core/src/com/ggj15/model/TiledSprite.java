package com.ggj15.model;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by st on 8/14/14.
 */
public class TiledSprite extends Sprite {

    public TiledSprite() {
        super();
    }

    public TiledSprite(TextureRegion region) {
        super(region);
    }

    public TiledSprite(TextureRegion region, float width, float height) {
        super(region);
        setSize(width, height);
    }

    public void draw(Batch batch, int offsetX, int offsetY) {
        offsetX = offsetX % getRegionWidth() - getRegionWidth();
        offsetY = offsetY % getRegionHeight() - getRegionHeight();
        for (float i = getY() + offsetY; i < getY() + getHeight(); i += getRegionHeight()) {
            for (float j = getX() + offsetX; j < getX() + getWidth(); j += getRegionWidth()) {
                batch.draw(getTexture(), j, i, getRegionWidth(), getRegionHeight(),
                        getU(), getV(), getU2(), getV2());
            }
        }
    }
}
