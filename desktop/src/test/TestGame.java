package test;

import com.badlogic.gdx.Game;
import de.loc.main.MainMenuScreen;


public class TestGame extends Game {
    Runnable r;

    public TestGame(Runnable r) {
        this.r = r;
    }

    public void create() {
        this.setScreen(new TestScreen());
        this.r.run();
    }

    public void render() {
        super.render(); // important!
    }

    public void dispose() {
        super.dispose();
    }
}
