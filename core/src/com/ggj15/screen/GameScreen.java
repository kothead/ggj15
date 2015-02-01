package com.ggj15.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.ggj15.GGJGame;
import com.ggj15.data.Configuration;
import com.ggj15.data.Messages;
import com.ggj15.data.MusicCache;
import com.ggj15.data.SkinCache;
import com.ggj15.model.*;
import com.ggj15.utility.Utils;

import java.util.Random;

/**
 * Created by kettricken on 24.01.2015.
 */
public class GameScreen extends BaseScreen {

    private static final String MESSAGE_STYLE = "message";
    private static final int SEED_WITH_HELP = 100;
    private static final int MESSAGE_PADDING = 20;

    private GGJGame game;
    private Player player;

    private Hole hole;
    private Background background;
    private InkIndicator inkIndicator;
    private HoleIndicator holeIndicator;
    private Rocket rocket;

    private Orbiter orbiter;
    private Array<Planet> planets = new Array<Planet>();

    boolean mapMode = false;

    private Stage mapStage;
    private long seed;

    private boolean hasHelp;
    private Label helpLabel;
    private Messages messages;

    private Rectangle tmpRectangle = new Rectangle();

    public GameScreen(GGJGame game, long seed) {
        super(game);
        this.game = game;
        this.seed = seed;
        hasHelp = seed == SEED_WITH_HELP;
        Random random = Utils.getRandom();
        random.setSeed(seed);

        mapStage = new Stage(new StretchViewport(getWorldWidth(), getWorldHeight()));

        // TODO: find world max width and height
        Configuration.scaleFactorX = mapStage.getWidth() / (Configuration.worldMaxWidth - Configuration.worldMaxWidth);
        Configuration.scaleFactorY = mapStage.getHeight() / Configuration.worldMaxHeight - Configuration.worldMaxHeight;

        background = new Background((int) getWorldWidth(), (int) getWorldHeight());

        orbiter = new Orbiter();
        for (int i = 1, max = 5+random.nextInt(7); i < max; i++) {
            int width = 6 + random.nextInt(6);
            int height = 6 + random.nextInt(6);
            float radius = 600 + random.nextInt(15) * 150;
            float speed = (random.nextInt(5) + 1) * 50;
            boolean isClockwise = random.nextBoolean();

            Orbit orbit = new Orbit.Builder().orbitRadius(radius).speed(speed).clockwise(isClockwise).build();
            Planet planet = new Planet.Builder().width(width).height(height).build(orbit);

            planets.add(planet);
            orbiter.add(planet, orbit);
            mapStage.addActor(planet.getActor());
        }

        player = new Player();
        player.setX(MathUtils.random(planets.get(0).getCleanX(), planets.get(0).getCleanX() + planets.get(0).getCleanWidth()));
        player.setY(planets.get(0).getCleanY() + planets.get(0).getCleanHeight());

        Planet lastPlanet = planets.get(planets.size - 5);
        rocket = new Rocket(lastPlanet);
        rocket.setX(MathUtils.random(lastPlanet.getCleanX(),lastPlanet.getCleanX() + lastPlanet.getCleanWidth()));
        rocket.setY(lastPlanet.getCleanY() + lastPlanet.getCleanHeight());
        mapStage.addActor(rocket.getActor());

        mapStage.addActor(player.getActor());

        hole = new Hole(planets);
        mapStage.addActor(hole.getActor());

        Table table = new Table();
        table.setFillParent(true);
        stage().addActor(table);
        table.right().bottom().padBottom(10).padRight(15).padLeft(15);

        holeIndicator = new HoleIndicator();
        //table.add(holeIndicator);

        table.add().expandX();

        inkIndicator = new InkIndicator();
        table.add(inkIndicator);

        Gdx.input.setInputProcessor(new Processor());

        if (hasHelp) {
            Skin skin = SkinCache.getDefaultSkin();

            helpLabel = new Label(null, skin, MESSAGE_STYLE);
            stage().addActor(helpLabel);
            messages = new Messages(helpLabel);
            messages.setMessage(Messages.START_TUTORIAL);
        }

        MusicCache.play(MusicCache.TYNC);
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

        if (!player.isAlive()) {
            game.setFinalScreen(false, seed);
        } else if (Intersector.intersectRectangles(player.getBoundingRectangle(), hole.getBoundingRectangle(), tmpRectangle)) {
            game.setFinalScreen(false, seed);
        } else if (rocket.getBoundingRectangle().contains(player.getBoundingRectangle())) {
            game.setFinalScreen(true, seed);
        }

        // TODO
        orbiter.process(delta);
        player.process(delta, planets);
        rocket.process(delta);
        inkIndicator.setInkLevel(player.getInkLevel());

        Gdx.gl.glClearColor(1, 0, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        getCamera().position.set(player.getX(), player.getY(), 0);
        getCamera().update();
        batch().setProjectionMatrix(getCamera().combined);
        shapes().setProjectionMatrix(getCamera().combined);

        hole.process(delta);

        float diff = (float) Math.sqrt(Math.pow(player.getX() - hole.getCenter().x, 2) +
                Math.pow(player.getY() - hole.getCenter().y, 2));
        diff -= hole.getWidth() / 2;
        holeIndicator.updateHoleDistance(diff);


        batch().begin();
        background.setPosition(player.getX() - getWorldWidth() / 2, player.getY() - getWorldHeight() / 2);
        background.draw(batch(), 0, 0);
        if (!mapMode) {
            player.draw(batch());
            for(Planet planet: planets){
                planet.draw(delta, batch());
            }

            hole.draw(delta, batch());
            rocket.draw(batch());
        }
        batch().end();

        if (!mapMode)
            hole.draw(shapes(), delta);
         else {
            mapStage.act(delta);
            mapStage.draw();
        }

        if (hasHelp) messages.process(delta);
        stage().act();
        stage().draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        mapStage.dispose();
        MusicCache.pause();
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
