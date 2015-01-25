package com.ggj15.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.ggj15.GGJGame;
import com.ggj15.data.SkinCache;
import com.ggj15.data.SoundCache;
import com.ggj15.model.Background;

/**
 * Created by kettricken on 25.01.2015.
 */
public class FinalScreen extends BaseScreen {

    private Background background;

    private float paddingBottom = 150;
    private float padding = 0;
    private float seed = 0;

    public FinalScreen(final GGJGame game, boolean win, final long seed) {
        super(game);
        background= new Background((int) getWorldWidth(), (int) getWorldHeight());
        this.seed = seed;

        String title = "";

        if (win) {
            title = "YOU'VE SURVIVED!";
            SoundCache.play("win");
        } else {
            title = "GAME OVER";
            SoundCache.play("death");
        }

        int colspan = 1;
        if (win) {
            colspan = 3;
            padding = 200;
        }

        Label label = new Label(title, SkinCache.getDefaultSkin(), "title");
        Table table = new Table();
        table.setFillParent(true);
        table.add(label).center().colspan(colspan).padBottom(paddingBottom);
        stage().addActor(table);

        table.row();

        Label restartLabel = new Label("RESTART", SkinCache.getDefaultSkin(), "button-label");
        table.add(restartLabel).padLeft(padding);

        restartLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                game.setGameScreen(seed);
            }
        });

        if (win) {
            table.add().expandX();

            Label nextLabel = new Label("NEXT", SkinCache.getDefaultSkin(), "button-label");
            table.add(nextLabel).padRight(padding);

            nextLabel.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    game.setGameScreen(seed + 1);
                }
            });
        }

        InputMultiplexer multiplexer = new InputMultiplexer(new Processor());
        multiplexer.addProcessor(stage());
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        batch().setProjectionMatrix(getCamera().combined);

        batch().begin();
        background.draw(batch(), 0, 0);
        batch().end();

        stage().act();
        stage().draw();
    }

    private class Processor extends InputAdapter {

        @Override
        public boolean keyUp(int keycode) {
            switch (keycode) {
                case Input.Keys.ESCAPE:
                    Gdx.app.exit();
                    return true;
            }

            return super.keyDown(keycode);
        }
    }
}
