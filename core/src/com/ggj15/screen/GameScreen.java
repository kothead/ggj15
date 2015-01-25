package com.ggj15.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.ggj15.GGJGame;
import com.ggj15.data.Configuration;
import com.ggj15.model.*;

/**
 * Created by kettricken on 24.01.2015.
 */
public class GameScreen extends BaseScreen {

    private GGJGame game;
    private Player player;

    private Hole hole;
    private PlanetController planetController;
    private Background background;

    private Array<Planet> planets = new Array<Planet>();

    boolean mapMode = false;

    private Stage mapStage;

    private long seed;

    public GameScreen(GGJGame game, long seed) {
        super(game);
        this.game = game;
        this.seed = seed;

        mapStage = new Stage(new StretchViewport(getWorldWidth(), getWorldHeight()));

        // TODO: find world max width and height
        Configuration.scaleFactorX = mapStage.getWidth() / (Configuration.worldMaxWidth - Configuration.worldMaxWidth);
        Configuration.scaleFactorY = mapStage.getHeight() / Configuration.worldMaxHeight - Configuration.worldMaxHeight;

        background= new Background((int) getWorldWidth(), (int) getWorldHeight());

        player = new Player();


        for (int i = 1; i < 4; i++) {
            Planet planet = new Planet.Builder().width(7+i).height(7 + i).orbitRadius(400*i).build();
            //planet.setPosition(-900*i, 50);
            planets.add(planet);
            mapStage.addActor(planet.getActor());
        }

        mapStage.addActor(player.getActor());

        hole = new Hole(planets);
        mapStage.addActor(hole.getActor());

        planetController = new PlanetController(planets);

        Table table = new Table();
        table.setFillParent(true);
        stage().addActor(table);
        table.right().bottom().padBottom(10).padRight(15);

        HoleIndicator holeIndicator = new HoleIndicator();
        table.add(holeIndicator);

        table.add().expandX();

        InkIndicator inkIndicator = new InkIndicator();
        table.add(inkIndicator);

        Gdx.input.setInputProcessor(new Processor());

    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        mapStage.getViewport().update(width, height, true);
        Configuration.scaleFactorX = mapStage.getWidth() / Configuration.worldMaxWidth;
        Configuration.scaleFactorY = mapStage.getHeight() / Configuration.worldMaxHeight;
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        player.process(delta, planets);

        Gdx.gl.glClearColor(1, 0, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        getCamera().position.set(player.getX(), player.getY(), 0);
        getCamera().update();
        batch().setProjectionMatrix(getCamera().combined);
        shapes().setProjectionMatrix(getCamera().combined);

        hole.process(delta);

        batch().begin();
        background.setPosition(player.getX() - getWorldWidth() / 2, player.getY() - getWorldHeight() / 2);
        background.draw(batch(), 0, 0);
        if (!mapMode) {
            player.draw(batch());
            for(Planet planet: planets){
                planet.draw(delta, batch());
            }

            hole.draw(delta, batch());
        }
        batch().end();

        if (!mapMode)
            hole.draw(shapes(), delta);
         else {
            mapStage.act(delta);
            mapStage.draw();
        }

        stage().act();
        stage().draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        mapStage.dispose();
    }

    private class Processor extends InputAdapter {
        @Override
        public boolean keyUp(int keycode) {
            switch (keycode) {
                case Input.Keys.ESCAPE:
                    game.setMenuScreen();
                    return true;
            }

            return super.keyDown(keycode);
        }

        @Override
        public boolean keyDown(int keycode) {
            switch (keycode) {
                case Input.Keys.M:
                    mapMode = !mapMode;
                    break;

                default:
                    break;
            }

            return super.keyDown(keycode);
        }
    }
}
