package com.ggj15.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.ggj15.GGJGame;
import com.ggj15.data.Configuration;
import com.ggj15.data.Messages;
import com.ggj15.data.SkinCache;
import com.ggj15.model.*;

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
    private PlanetController planetController;
    private Background background;
    private InkIndicator inkIndicator;
    private HoleIndicator holeIndicator;

    private Array<Planet> planets = new Array<Planet>();

    boolean mapMode = false;

    private Stage mapStage;
    private long seed;

    private boolean hasHelp;
    private Label helpLabel;
    private Messages messages;

    public static Random random = new Random();

    public GameScreen(GGJGame game, long seed) {
        super(game);
        this.game = game;
        this.seed = seed;
        hasHelp = seed == SEED_WITH_HELP;
        random.setSeed(seed);

        mapStage = new Stage(new StretchViewport(getWorldWidth(), getWorldHeight()));

        // TODO: find world max width and height
        Configuration.scaleFactorX = mapStage.getWidth() / (Configuration.worldMaxWidth - Configuration.worldMaxWidth);
        Configuration.scaleFactorY = mapStage.getHeight() / Configuration.worldMaxHeight - Configuration.worldMaxHeight;

        background= new Background((int) getWorldWidth(), (int) getWorldHeight());

        player = new Player();

        for (int i = 1, max = 5+random.nextInt(7); i < max; i++) {
            Planet planet = new Planet.Builder().width(6+random.nextInt(6))
                    .height(6 + random.nextInt(6)).orbitRadius(600 + random.nextInt(15) * 150)
                    .speed((random.nextInt(5) + 1) * 150).build();
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

        holeIndicator = new HoleIndicator();
        table.add(holeIndicator);

        table.add().expandX();

        inkIndicator = new InkIndicator();
        table.add(inkIndicator).padRight(15);

        Gdx.input.setInputProcessor(new Processor());

        if (hasHelp) {
            Skin skin = SkinCache.getDefaultSkin();

            helpLabel = new Label(null, skin, MESSAGE_STYLE);
            stage().addActor(helpLabel);
            messages = new Messages(helpLabel);
            messages.setMessage(Messages.START_TUTORIAL);
        }
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
//        for(Planet planet: planets){
//            planet.process(delta);
//        }
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
