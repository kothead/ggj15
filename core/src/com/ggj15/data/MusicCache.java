package com.ggj15.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

/**
 * Created by st on 10/13/14.
 */
public class MusicCache {

    public static final String TYNC = "tync";

    private static final String MUSIC_DIR = "audio/music/";
    private static final String MUSIC_EXT = ".mp3";
    private static final float VOLUME = 1f;

    private static String key = TYNC;
    private static Music music;
    private static boolean playing = false;

    public static void play() {
        if (key != null) play(key);
    }

    public static void play(String key) {
        if (music != null) {
            if (MusicCache.key.equals(key)) {
                if (!isPlaying()) {
                    playing = true;
                    music.play();
                }
                return;
            } else {
                music.dispose();
            }
        }

        playing = true;
        MusicCache.key = key;
        String path  = MUSIC_DIR + key + MUSIC_EXT;
        music = Gdx.audio.newMusic(Gdx.files.internal(path));
        music.setLooping(true);
        music.setVolume(VOLUME);
        music.play();
    }

    public static boolean isPlaying() {
        return playing;
    }

    public static void pause() {
        if (isPlaying()) {
            playing = false;
            music.pause();
        }
    }

    public static void dispose() {
        pause();
        if (music != null) music.dispose();
        music = null;
    }
}
