package com.ggj15.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.ggj15.data.Configuration;
import com.ggj15.data.ImageCache;

/**
 * Created by kettricken on 25.01.2015.
 */
public class Rocket extends Sprite {

    private RocketActor actor;

    public Rocket() {
        setRegion(ImageCache.getTexture("rock"));
        setSize(getRegionWidth(), getRegionHeight());
        setOrigin(getRegionWidth() / 2f, getRegionHeight() / 2f);

        actor = new RocketActor();
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
        batch.draw(Rocket.this, getX(), getY());
    }

    public RocketActor getActor() {
        return actor;
    }

    public  class RocketActor extends Actor {

        private TextureRegion textureRegion;

        public RocketActor() {
            textureRegion = ImageCache.getTexture("char-fly");
//            setSize(textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
//            setOrigin(textureRegion.getRegionWidth() / 2f,
//                    textureRegion.getRegionHeight() / 2f);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);

            float x = Rocket.this.getX() * Configuration.scaleFactorX;
            float y = Rocket.this.getY() * Configuration.scaleFactorY;

            float offsetX = getStage().getWidth() / 2f;
            float offsetY = getStage().getHeight() / 2f;

            float width = Rocket.this.getWidth() * Configuration.scaleFactorX;
            float height = Rocket.this.getHeight() * Configuration.scaleFactorY;

            Gdx.app.log("Test", "draw rocket on map");
            batch.draw(textureRegion,
                    x + offsetX, y + offsetY,
                    width, height);
        }
    }
}
