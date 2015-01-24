package com.ggj15.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.ggj15.GGJGame;
import com.ggj15.model.Planet;
import com.ggj15.model.Player;

/**
 * Created by kettricken on 24.01.2015.
 */
public class GameScreen extends BaseScreen {

    private GGJGame game;
    private Player player;
    private Planet planet;

    public GameScreen(GGJGame game) {
        super(game);

        this.game = game;
        player = new Player();
        planet = new Planet.Builder().width(5).height(5).build();
        planet.setPosition(20, 20);

        Gdx.input.setInputProcessor(new Processor());
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        player.process(delta, planet);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch().begin();
        player.draw(delta, batch());
        planet.draw(delta, batch());
        batch().end();
    }

    private class Processor extends InputAdapter {
        @Override
        public boolean keyUp(int keycode) {
            switch (keycode) {
                case Input.Keys.ESCAPE:
                    return true;
            }

            return super.keyDown(keycode);
        }

        @Override
        public boolean keyDown(int keycode) {
            switch (keycode) {
                default:
                    break;
            }

            return super.keyDown(keycode);
        }
    }
}
