package com.ggj15.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.ggj15.GGJGame;
import com.ggj15.model.Hole;
import com.ggj15.model.Planet;
import com.ggj15.model.Player;

/**
 * Created by kettricken on 24.01.2015.
 */
public class GameScreen extends BaseScreen {

    private GGJGame game;
    private Player player;
    private Planet planet;
    private Hole hole;

    private Array<Planet> planets = new Array<Planet>();

    boolean mapMode = true;

    private Stage stage;

    private float world_max_width = 3000;
    private float world_max_height = 1800;
    public static float scaleFactorX;
    public static float scaleFactorY;

    public GameScreen(GGJGame game) {
        super(game);

        this.game = game;

        stage = new Stage(new StretchViewport(getWorldWidth(), getWorldHeight()));

        scaleFactorX = stage.getWidth() / world_max_width;
        scaleFactorY = stage.getHeight() / world_max_height;

        player = new Player();
        stage.addActor(player.getActor());

        planet = new Planet.Builder().width(11).height(11).build();
        planet.setPosition(70, 20);
        planets.add(planet);
        stage.addActor(planet.getActor());

        hole = new Hole(planets);
        stage.addActor(hole.getActor());

        Gdx.input.setInputProcessor(new Processor());
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        stage.getViewport().update(width, height, true);
        scaleFactorX = stage.getWidth() / world_max_width;
        scaleFactorY = stage.getHeight() / world_max_height;
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        player.process(delta, planet);

        Gdx.gl.glClearColor(1, 0, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        getCamera().position.set(player.getX(), player.getY(), 0);
        getCamera().update();
        batch().setProjectionMatrix(getCamera().combined);
        shapes().setProjectionMatrix(getCamera().combined);

        hole.process(delta);

        batch().begin();
        player.draw(delta, batch());
        planet.draw(delta, batch());
        hole.draw(delta, batch());
        batch().end();

        hole.draw(shapes(), delta);

        if (mapMode) {
            stage.act(delta);
            stage.draw();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
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
