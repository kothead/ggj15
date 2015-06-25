package com.ggj15.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.ggj15.GGJGame;
import com.ggj15.data.SkinCache;
import com.ggj15.model.Background;
import com.ggj15.model.MenuController;

/**
 * Created by kettricken on 25.01.2015.
 */
public class MenuScreen extends BaseScreen {

    private Background background;

    private TextField textField;
    private MenuController controller;

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

        Label seedLabel = new Label("SEED:", SkinCache.getDefaultSkin(), "menu");
        textField = new TextField("100", SkinCache.getDefaultSkin(), "menu");
        textField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        textField.setAlignment(Align.center);

        Table innerTable = new Table();
        innerTable.add(seedLabel).center().padRight(20);
        innerTable.add(textField).height(40);

        table.add(innerTable).colspan(3).center().padBottom(paddingBottom);
        table.row();

        TextButton btnPlay = new TextButton("PLAY", SkinCache.getDefaultSkin(), "menu");
        table.add(btnPlay).padLeft(padding);

        btnPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                long seed = Long.valueOf(textField.getText());
                game.setGameScreen(seed);
            }
        });

        table.add().expandX();

        TextButton btnExit = new TextButton("EXIT", SkinCache.getDefaultSkin(), "menu");
        table.add(btnExit).padRight(padding);

        btnExit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Gdx.app.exit();
            }
        });

        controller = new MenuController(stage());
        controller.add(textField, btnPlay, btnExit);
        controller.select(btnPlay);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(controller);
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

    @Override
    public void onStart() {
        // nothing
    }

    @Override
    public void onStop() {
        // nothing
    }
}
