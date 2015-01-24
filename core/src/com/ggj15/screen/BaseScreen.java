package com.ggj15.screen;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ggj15.GGJGame;
import com.ggj15.data.Configuration;

/**
 * Created by st on 10/24/14.
 */
public abstract class BaseScreen extends ScreenAdapter {

    private static final String TEXTURE_FONT = "font";
    private static final String FONT_FILE = "font.fnt";

    private GGJGame game;
    private OrthographicCamera camera;
    private StretchViewport viewport;
    private float worldWidth, worldHeight;

    private SpriteBatch batch;
    private ShapeRenderer shapes;
    private Stage stage;

    public BaseScreen(GGJGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        calcWorldSize();
        viewport = new StretchViewport(worldWidth, worldHeight, camera);

        batch = new SpriteBatch();
        stage = new Stage(new StretchViewport(getWorldWidth() / 2, getWorldHeight() / 2));
        shapes = new ShapeRenderer();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.setWorldSize(getWorldWidth(), getWorldHeight());
        viewport.update(width, height, true);

        batch.setProjectionMatrix(getCamera().combined);
        shapes.setProjectionMatrix(getCamera().combined);
    }

    public GGJGame getGame() {
        return game;
    }

    public float getWorldWidth() {
        return worldWidth;
    }

    public float getWorldHeight() {
        return worldHeight;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public SpriteBatch batch() {
        return batch;
    }

    public ShapeRenderer shapes() {
        return shapes;
    }

    public Stage stage() {
        return stage;
    }

    private void calcWorldSize() {
        worldWidth = Configuration.WORLD_WIDTH;
        worldHeight = Configuration.WORLD_HEIGHT;
    }
}
