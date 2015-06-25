package com.ggj15.data;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.ggj15.model.Player;

/**
 * Created by st on 12/8/14.
 */
public class Messages {

    private static final float TIME_FOR_MESSAGE = 8f;

    public static final int START_TUTORIAL = 0;
    public static final int MESSAGE_NULL = 6;
    private static final float MESSAGE_HORIZONTAL_PADDING = 20;
    private static final float MESSAGE_VERTICAL_PADDING = 40;

    private static final String[] DEFAULT_MESSAGES = {
            "press A/D to move LEFT/RIGHT",
            "press W to jump, hold W to fly",
            "press M to show map",
            "if you fell in the black hole YOU ARE DEAD!",
            "there is your ink level. If it's zero YOU ARE DEAD!",
            "and what do you do now? OF COURSE, GET TO THE ROCKET!",
            ""
    };

    private Label label;
    private float timer;
    private int current = MESSAGE_NULL;

    public Messages(Label label) {
        this.label = label;
    }

    public void process(float delta) {
        label.setPosition(MESSAGE_HORIZONTAL_PADDING, Configuration.WORLD_HEIGHT * Configuration.SCALE_FACTOR / 2f + 80);
        if (current == MESSAGE_NULL) return;

        timer += delta;
        if (timer < TIME_FOR_MESSAGE) return;

        timer = 0;
        switch (current) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:

                setMessage(current + 1);
                break;
        }
    }

    public void setMessage(int message) {
        if ((current == 0 || current == 1 || current == 2 || current == 3 || current == 4)
                && message != 0 && message != 1
                && message != 2 && message != 3
                && message != 4 && message != 5 && message != MESSAGE_NULL) return;
        current = message;
        timer = 0;
        label.setText(DEFAULT_MESSAGES[message]);
    }
}
