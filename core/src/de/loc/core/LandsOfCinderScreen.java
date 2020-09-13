package de.loc.core;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;

import de.loc.event.EventSystem;
import de.loc.graphics.ModelComponentCreator;
import de.loc.graphics.RenderSystem;
import de.loc.input.userinterface.BaseScreen;
import de.loc.main.LandsOfCinder;
import de.loc.tools.Constants;

public abstract class LandsOfCinderScreen extends BaseScreen {

    protected Engine engine;
    protected RenderSystem renderSystem;
    protected boolean loadScheduled;
    private final FPSLogger fpsLogger;
    private String loadLevelFilePath;

    public LandsOfCinderScreen(LandsOfCinder game) {
        super(game);

        this.engine = new Engine();

        LevelManager.getInstance().setEngine(this.engine);

        this.fpsLogger = new FPSLogger();

        // TODO: Hier nochmal drüber schauen!
        //        FileHandle tmpHandle = null;
        //
        //		if (Gdx.app.getType() == Constants.DESKTOP) {
        //			tmpHandle = Gdx.files.local(Constants.GAME_TMP_DIRECTORY);
        //			Gdx.app.log("PATH:", tmpHandle.file().getAbsolutePath());
        //		} else if (Gdx.app.getType() == Constants.ANDROID) {
        //			tmpHandle = Gdx.files.local(Constants.GAME_TMP_DIRECTORY);
        //		} else {
        //			Gdx.app.log("Fehler", "Unbekannter Application Type!");
        //		}
        //		boolean deleted = tmpHandle.deleteDirectory();
        //		if (!deleted)
        //		{
        //			Gdx.app.log("NNNEEEEEIN", "tmpHandle konnte nicht deleted werden!!!!!");
        //		}
        //		else
        //		{
        //			Gdx.app.log("ja", "tmpHandle deleted!");
        //		}
    }

    public Engine getEngine() {
        return this.engine;
    }

    public void scheduleLoad(String path) {
        this.loadScheduled = true;
        this.loadLevelFilePath = path;
    }

    protected abstract void load(String file);

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.engine.update(delta);
        super.render(delta);

        if ( Constants.SHOW_FPS ) {
            this.fpsLogger.log();
        }

        //da das löschen immer erst am ende eines update cycles ausgeführt wird muss danach geladen werden
        if ( this.loadScheduled ) {
            this.load(this.loadLevelFilePath);
        }
    }

    @Override
    public void dispose() {
        super.dispose();

        LevelManager.getInstance().dispose();
        //LevelManager.getInstance().setPlayerEntity(null);
        EventSystem.getInstance().clear();
        this.renderSystem.dispose();
        ModelComponentCreator.getInstance().dispose();
    }
}
