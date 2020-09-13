package de.loc.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Scaling;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.loc.core.LevelManager;
import de.loc.input.userinterface.LandsOfCinderWindow;
import de.loc.input.userinterface.MenuScreen;
import de.loc.input.userinterface.MenuTable;
import de.loc.main.LandsOfCinder;
import de.loc.tools.Constants;

public class EditorChooseWorld extends MenuScreen {

    private final EditorScreen.Type editorType;
    private LandsOfCinderWindow preview;

    public EditorChooseWorld(LandsOfCinder game, EditorScreen.Type editorType) {
        super(game);
        this.editorType = editorType;

        this.setUpUserInterface();
    }

    private void setUpUserInterface() {
        MenuTable menuTable = new MenuTable(this.ui, MenuTable.Type.CHOOSEWORLD);

        String path;
        switch ( this.editorType ) {
            default:
            case NEW_WORLD:
                path = Constants.BACKGROUNDS_PATH;
                break;
            case EDIT_WORLD:
                path = Constants.EDITOR_SAVES_PATH;
                break;
        }

        FileHandle dirHandle = Gdx.files.local(path);

        for (String backgroundFileName : getBackgroundNames()) {
            FileHandle backgroundHandle = Gdx.files.internal("2d/backgrounds/" + backgroundFileName);
            String substring = backgroundHandle.toString().substring(path.length());
            menuTable.addTextButtonItem(substring, "editor_choose_" + substring);
        }
        if ( dirHandle.list().length == 0 ) {
            menuTable.addLabelItem("Keine Daten gefunden");
        }
        this.ui.add(menuTable);

        this.ui.addButton(Constants.UI_ICON_BACK, 0, 0, "editor_choose_world_back");
    }

    private static Collection<String> getBackgroundNames() {
        Collection<String> backgroundFileNames = new ArrayList<>(20);
        FileHandle backgroundsFile = Gdx.files.internal("2d/backgrounds/backgrounds.txt");
        try (BufferedReader reader = backgroundsFile.reader(1024)) {
            String line;
            while ((line = reader.readLine()) != null) {
                backgroundFileNames.add(line);
            }
        } catch ( IOException e ) {
            Gdx.app.log("EDITOR", "Could not read backgrounds file!");
        }
        return backgroundFileNames;
    }

    public void openWindow(String path) {

        String backgroundPath;

        switch ( this.editorType ) {
            default:
            case NEW_WORLD:
                backgroundPath = Constants.BACKGROUNDS_PATH + path;
                LevelManager.getInstance().getLevel().setBackgroundWidth(30.0f);
                LevelManager.getInstance().getLevel().setBackgroundPath(path);
                break;
            case EDIT_WORLD:
                //LevelManager.getInstance().setCurrentLevel(Constants.EDITOR_SAVES_PATH + path);
                SceneLoader sceneLoader = new SceneLoader();
                backgroundPath = Constants.BACKGROUNDS_PATH + sceneLoader.parseBackgroundPathOnly(Constants.EDITOR_SAVES_PATH + path);
                LevelManager.getInstance().getLevel().setBackgroundPath(sceneLoader.parseBackgroundPathOnly(Constants.EDITOR_SAVES_PATH + path));
                break;
        }

        Image image = new Image(this.ui.getAsset(backgroundPath, Texture.class));
        image.setScaling(Scaling.fit);

        this.preview = this.ui.addTwoButtonWindow(image, "Zurück", "Annehmen", "editor_window_back", "editor_window_accept_" + path);
        this.preview.setBackground(new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_PAPER_DARK)));

    }

    public void closeWindow() {
        this.preview.remove();
    }

    public EditorScreen.Type getEditorType() {
        return this.editorType;
    }
}
