package com.ggj15.model;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.ggj15.data.SkinCache;

/**
 * Created by st on 2/3/15.
 */
public class MenuController extends InputAdapter {

    private static final String POSTFIX_SELECTED = "-selected";
    private static final String METHOD_GET_STYLE = "getStyle";
    private static final String METHOD_SET_STYLE = "setStyle";
    private static final int NO_SELECTED = -1;

    private Stage stage;
    private Array<Actor> actors;
    private int selectedId = NO_SELECTED;

    public MenuController(Stage stage) {
        this.stage = stage;
        actors = new Array<Actor>();
    }

    public void add(Actor... actors) {
        this.actors.addAll(actors);
    }

    public void remove(Actor actor) {
        int id = actors.indexOf(actor, true);
        if (selectedId >= id) {
            selectedId--;
            Actor current = getSelectedActor();
            if (current != null) clickIt(current);
        }
        actors.removeValue(actor, true);
    }

    @Override
    public boolean keyDown(int keycode) {
        Actor actor = getSelectedActor();

        switch (keycode) {
            case Input.Keys.LEFT:
            case Input.Keys.A:
                if (!isUnderEdit(actor)) selectOnLeft();
                break;

            case Input.Keys.RIGHT:
            case Input.Keys.D:
            case Input.Keys.TAB:
                if (!isUnderEdit(actor)) selectOnRight();
                break;

            case Input.Keys.ENTER:
                if (isUnderEdit(actor)) {
                    stage.setKeyboardFocus(null);
                    stage.setScrollFocus(null);
                } else if (actor != null) {
                    clickIt(getSelectedActor());
                }
                break;
        }

        return super.keyUp(keycode);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Actor selected = getSelectedActor();
        Actor focused = stage.getKeyboardFocus();
        if (focused != selected) {
            select(focused);
        }
        return super.touchDown(screenX, screenY, pointer, button);
    }

    public void selectOnLeft() {
        selectByOffset(-1);
    }

    public void selectOnRight() {
        selectByOffset(1);
    }

    public void select(Actor actor) {
        int id = actors.indexOf(actor, true);
        select(id);
    }

    private void selectByOffset(int offset) {
        select(selectedId + offset);
    }

    private void select(int index) {
        if (actors.size == 0) return;
        Actor actor = getSelectedActor();
        if (actor != null) setSelectedStyle(actor, false);

        selectedId = index;
        if (Math.abs(selectedId) >= actors.size) selectedId %= actors.size;
        if (selectedId < 0) selectedId = actors.size + selectedId;

        actor = getSelectedActor();
        stage.setKeyboardFocus(actor);
        stage.setScrollFocus(actor);
        setSelectedStyle(actor, true);
    }

    private boolean isUnderEdit(Actor actor) {
        return actor != null && stage.getKeyboardFocus() == actor
                && actor instanceof TextField;
    }

    private Actor getSelectedActor() {
        if (selectedId == NO_SELECTED) return null;
        return actors.get(selectedId);
    }

    private void clickIt(Actor actor) {
        InputEvent event = new InputEvent();
        event.setType(InputEvent.Type.touchDown);
        actor.fire(event);

        event.setType(InputEvent.Type.touchUp);
        actor.fire(event);
    }

    /**
     * changes actor's style which should be presented in json skin file twice,
     * as usual and with "-selected" prefix. Uses libgdx reflection like setEnabled
     * method in Skin class. Hope, it is not very slow.
     * @param actor
     * @param selected
     */
    public void setSelectedStyle(Actor actor, boolean selected) {
        // Get current style.
        Method method = findMethod(actor.getClass(), METHOD_GET_STYLE);
        if (method == null) return;
        Object style;
        try {
            style = method.invoke(actor);
        } catch (Exception ignored) {
            return;
        }
        // Determine new style.
        Skin skin = SkinCache.getDefaultSkin();
        String name = skin.find(style);
        if (name == null) return;
        name = name.replace(POSTFIX_SELECTED, "") + (selected ? POSTFIX_SELECTED : "");
        style = skin.get(name, style.getClass());
        // Set new style.
        method = findMethod(actor.getClass(), METHOD_SET_STYLE);
        if (method == null) return;
        try {
            method.invoke(actor, style);
        } catch (Exception ignored) {
        }
    }

    static private Method findMethod(Class type, String name) {
        Method[] methods = ClassReflection.getMethods(type);
        for (int i = 0, n = methods.length; i < n; i++) {
            Method method = methods[i];
            if (method.getName().equals(name)) return method;
        }
        return null;
    }
}
