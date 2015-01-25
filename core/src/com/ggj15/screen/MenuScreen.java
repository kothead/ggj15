package com.ggj15.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.ggj15.GGJGame;
import com.ggj15.data.SkinCache;
import com.ggj15.model.Background;

/**
 * Created by kettricken on 25.01.2015.
 */
public class MenuScreen extends BaseScreen {

    private Background background;

    private TextField textField;

    private float paddingBottom = 70;
    private float padding = 200;

    public MenuScreen(final GGJGame game) {
        super(game);
        background= new Background((int) getWorldWidth(), (int) getWorldHeight());

        Label label = new Label("INK MUST FLOW", SkinCache.getDefaultSkin(), "title");
        Table table = new Table();
        table.setFillParent(true);
        table.add(label).center().colspan(3).padBottom(paddingBottom);
        stage().addActor(table);

        table.row();

        Label seedLabel = new Label("SEED:", SkinCache.getDefaultSkin(), "button-label");
        textField = new TextField("100", SkinCache.getDefaultSkin());
        textField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        textField.setAlignment(Align.center);

        Table innerTable = new Table();
        innerTable.add(seedLabel).center().padRight(20);
        innerTable.add(textField).height(40);

        table.add(innerTable).colspan(3).center().padBottom(paddingBottom);
        table.row();

        Label playLabel = new Label("PLAY", SkinCache.getDefaultSkin(), "button-label");
        table.add(playLabel).padLeft(padding);

        playLabel.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                long seed = Long.valueOf(textField.getText());
                game.setGameScreen(seed);
            }
        });

        table.add().expandX();

        Label exitLabel = new Label("EXIT", SkinCache.getDefaultSkin(), "button-label");
        table.add(exitLabel).padRight(padding);

        exitLabel.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Gdx.app.exit();
            }
        });

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
