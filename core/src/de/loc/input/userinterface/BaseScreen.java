package de.loc.input.userinterface;

import com.badlogic.gdx.Screen;

import de.loc.input.InputHandler;
import de.loc.main.LandsOfCinder;

public abstract class BaseScreen implements Screen {

    protected final LandsOfCinder game;

    protected UserInterface ui;
    protected InputHandler inputHandler;

    public BaseScreen(LandsOfCinder game) {
        this.game = game;
    }

    @Override
    public void show() {
        this.inputHandler.resume();
    }

    @Override
    public void render(float delta) {
        this.ui.render();
    }

    @Override
    public void resize(int width, int height) {
        this.ui.resize(width, height);
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
    }

    @Override
    public void resume() {
        this.inputHandler.resume();
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
    }

    @Override
    public void dispose() {
        this.ui.dispose();
    }

    public LandsOfCinder getGame() {
        return this.game;
    }
}