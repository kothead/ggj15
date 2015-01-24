package com.ggj15.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ggj15.data.ImageCache;

/**
 * Created by st on 1/24/15.
 */
public class Background extends TiledSprite {

    private static final String TEXTURE = "sky";
    private static final int FRAMES_COUNT = 3;
    private static final float DURATION = 0.1f;

    private float stateTime;
    private Animation anim;

    public Background(int width, int height) {
        setSize(width, height);
        TextureRegion[] regions = new TextureRegion[FRAMES_COUNT];
        for (int i = 0; i < FRAMES_COUNT; i++) {
            regions[i] = ImageCache.getFrame(TEXTURE, i + 1);
        }

        anim = new Animation(DURATION, regions);
        setRegion(anim.getKeyFrame(0, true));
    }

    @Override
    public void draw(Batch batch, int offsetX, int offsetY) {
        setRegion(anim.getKeyFrame(stateTime, true));
        stateTime += Gdx.graphics.getDeltaTime();

        super.draw(batch, offsetX, offsetY);
    }
}
