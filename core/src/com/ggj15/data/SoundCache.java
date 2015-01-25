package com.ggj15.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created by st on 9/25/14.
 */
public class SoundCache {

    private static final String SOUND_DIR = "assets/audio/sound/";
    private static final String SOUND_EXT = ".wav";

    private static ObjectMap<String, Sound> sounds;

    public static void load() {
        sounds = new ObjectMap<String, Sound>();

        String[] keys = {
                "block",
                "death",
                "jump",
                "planet_burst",
                "planet_burst2",
                "rocket",
                "take-off",
                "win",
                "ink"
        };
        for (String key: keys) {
            Sound sound = Gdx.audio.newSound(Gdx.files.internal(SOUND_DIR + key + SOUND_EXT));
            sounds.put(key, sound);
        }
    }

    public static void play(String key) {
        sounds.get(key).play();
    }
}
