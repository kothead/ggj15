package com.ggj15.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ggj15.GGJGame;
import com.ggj15.data.ImageCache;
import com.ggj15.model.Player;

/**
 * Created by kettricken on 24.01.2015.
 */
public class GameScreen extends BaseScreen {

    Player player;

    GGJGame game;

    ShapeRenderer renderer;

    public GameScreen(GGJGame game) {
        super(game);

        this.game = game;

        player = new Player(ImageCache.getTexture(ImageCache.PLAYER));

        renderer = new ShapeRenderer();

        Gdx.input.setInputProcessor(new Processor());
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        batch().begin();
        player.draw(delta, batch());
        batch().end();
    }

    private class Processor extends InputAdapter {
        @Override
        public boolean keyUp(int keycode) {
            switch (keycode) {
                case Input.Keys.W:
                    player.stopGoingUp();
                    return true;

                case Input.Keys.A:
                    player.stopLeft();
                    return true;

                case Input.Keys.S:
                    return true;

                case Input.Keys.D:
                    player.stopRight();
                    return true;

                case Input.Keys.UP:
                    return true;

                case Input.Keys.RIGHT:
                    return true;

                case Input.Keys.DOWN:
                    return true;

                case Input.Keys.LEFT:
                    return true;

                case Input.Keys.ESCAPE:
                    return true;
            }

            return super.keyDown(keycode);
        }

        @Override
        public boolean keyDown(int keycode) {
            switch (keycode) {
                case Input.Keys.W:
                    player.goesUp();
                    return true;

                case Input.Keys.A:
                    player.goLeft();
                    return true;

                case Input.Keys.S:
                    return true;

                case Input.Keys.D:
                    player.goRight();
                    return true;
            }

            return super.keyDown(keycode);
        }
    }
}
