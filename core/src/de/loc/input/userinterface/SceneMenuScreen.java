package de.loc.input.userinterface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Scaling;

import de.loc.core.LevelManager;
import de.loc.main.LandsOfCinder;
import de.loc.tools.Constants;

public class SceneMenuScreen extends MenuScreen {
    private LandsOfCinderWindow previewWindow;
    private final EditorMenuScreen editorMenu;

    /* Konstruktor */
    public SceneMenuScreen(LandsOfCinder game, EditorMenuScreen editorMenu) {
        super(game);
        this.editorMenu = editorMenu;
        this.setupUserInterface();
    }

    /* Aufbau der Benutzeroberfläche */
    private void setupUserInterface() {
        MenuTable menuTable = new MenuTable(this.ui, MenuTable.Type.CHOOSEWORLD);

        FileHandle dirHandle = Gdx.files.internal(Constants.AVAILABLE_SCENES_PATH);
        for ( FileHandle entry : dirHandle.list() ) {
            String string = entry.nameWithoutExtension();
            menuTable.addTextButtonItem(string, "scene_menu_choose_" + string);
        }
        if ( dirHandle.list().length == 0 ) {
            menuTable.addLabelItem("Keine Szenen gefunden!");
        }
        this.ui.add(menuTable);
    }

    /* Öffnet ein Vorschaufenster */
    public void openWindow(String sceneName) {
        String backgroundPath = Constants.BACKGROUNDS_PATH + sceneName + ".png";
        LevelManager.getInstance().getLevel().setBackgroundWidth(30.f);
        LevelManager.getInstance().getLevel().setBackgroundPath(sceneName);

        Image image = new Image(this.ui.getAsset(backgroundPath, Texture.class));
        image.setScaling(Scaling.fit);

        this.previewWindow = this.ui.addTwoButtonWindow(image, "Zurück", "Annehmen", "scene_menu_window_close", "scene_menu_window_accept_" + sceneName);
        this.previewWindow.setBackground(new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_PAPER_DARK)));
    }

    /* Schließt das Vorschaufenster */
    public void closeWindow() {
        this.previewWindow.remove();
    }

    /* Setzt die gewählte Szene für das gerade erstellte Level im Editor-Menü */
    public void setScene(String sceneName) {
        this.editorMenu.initNewLevel(sceneName);
        this.game.setScreen(this.editorMenu);
    }

}
