package com.ggj15;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ggj15.data.ImageCache;
import com.ggj15.data.SkinCache;
import com.ggj15.data.SoundCache;
import com.ggj15.screen.FinalScreen;
import com.ggj15.screen.GameScreen;
import com.ggj15.screen.MenuScreen;

public class GGJGame extends Game {
	SpriteBatch batch;
	Texture img;
    private float scaleFactor;

    @Override
	public void create () {
		ImageCache.load();
		SoundCache.load();
		SkinCache.load();

        setMenuScreen();
	}

    @Override
	public void setScreen(Screen screen) {
		Screen old = getScreen();
		super.setScreen(screen);
		if (old != null) old.dispose();
	}

	@Override
	public void render () {
        super.render();
	}

    public void setMenuScreen() {
        setScreen(new MenuScreen(this));
    }

    public void setGameScreen(long seed){
        setScreen(new GameScreen(this, seed));
    }

    public void setFinalScreen(boolean win, long seed) {
        setScreen(new FinalScreen(this, win, seed));
    }

    public float getScaleFactor() {
        return scaleFactor;
    }
}
