package com.ggj15;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ggj15.data.ImageCache;
import com.ggj15.data.MusicCache;
import com.ggj15.data.SkinCache;
import com.ggj15.data.SoundCache;

public class GGJGame extends Game {
	SpriteBatch batch;
	Texture img;

	@Override
	public void create () {
		ImageCache.load();
		SoundCache.load();
		SkinCache.load();
	}

	@Override
	public void setScreen(Screen screen) {
		Screen old = getScreen();
		super.setScreen(screen);
		if (old != null) old.dispose();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
}
