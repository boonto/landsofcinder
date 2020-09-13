package de.loc.main;

import com.badlogic.gdx.Game;

public class LandsOfCinder extends Game {
    public void create() {
        this.setScreen(new MainMenuScreen(this));

    }

    public void render() {
        super.render(); // important!
    }

    public void dispose() {
        super.dispose();
    }
}