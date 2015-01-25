package com.ggj15.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.ggj15.data.ImageCache;

/**
 * Created by kettricken on 24.01.2015.
 */
public class InkIndicator extends Actor {

    private Sprite bottomSprite;
    private Sprite topSprite;

    private float inkLevel = 0;

    ShapeRenderer renderer = new ShapeRenderer();

    public InkIndicator() {
        bottomSprite = new Sprite(ImageCache.getTexture("ink-index-mask"));
        topSprite = new Sprite(ImageCache.getTexture("ink-index"));

        this.setWidth(bottomSprite.getWidth());
        this.setHeight(bottomSprite.getHeight());
        this.setBounds(0, 0, this.getWidth(), this.getHeight());

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        bottomSprite.setPosition(this.getX(), this.getY());
        bottomSprite.setOrigin(this.getOriginX(), this.getOriginY());
        bottomSprite.setRotation(this.getRotation());
        bottomSprite.setScale(this.getScaleX(), this.getScaleY());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        bottomSprite.draw(batch);
        drawTopSprite(batch);
    }

    private void drawTopSprite(Batch batch) {

        batch.end();

        Gdx.gl.glClearDepthf(1f);
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glDepthFunc(GL20.GL_LESS);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthMask(true);
        Gdx.gl.glColorMask(false, false, false, true);

        renderer.setProjectionMatrix(getStage().getCamera().combined);
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(1, 1, 1, 1);
        float width = bottomSprite.getX() + bottomSprite.getWidth() - inkLevel;
        renderer.rect(width, bottomSprite.getY(), inkLevel, bottomSprite.getHeight());
        renderer.end();

        batch.flush();

        batch.begin();

        Gdx.gl.glColorMask(true, true, true, true);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthFunc(GL20.GL_EQUAL);

        batch.draw(topSprite, bottomSprite.getX(), bottomSprite.getY());
        batch.flush();

        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthMask(false);
    }

    public void setInkLevel(float inkLevel) {
        this.inkLevel = bottomSprite.getWidth() * (inkLevel / 100);
    }
}
