package de.loc.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;

import de.loc.core.LevelManager;
import de.loc.editor.EditorChooseWorld;
import de.loc.editor.EditorScreen;
import de.loc.game.GameScreen;
import de.loc.input.userinterface.BaseScreen;
import de.loc.input.userinterface.EditorMenuScreen;
import de.loc.input.userinterface.SceneMenuScreen;
import de.loc.main.MainMenuScreen;
import de.loc.tools.Constants;
import de.loc.tools.XmlHelper;

public class MenuInputHandler extends InputHandler {
    public MenuInputHandler(BaseScreen screen) {
        super(screen);
    }

    public boolean handle(String handleID) {
        /* Hauptmenü */
        if ( handleID.equals("main_menu_play") ) {
            String startLevel = XmlHelper.parseStartLevel(Constants.PACKAGE_FOLDER + Constants.STD_PACKAGE);
            //String startLevel = new EditorMenuScreen(screen.getGame()).parseStartLevel(Constants.PACKAGE_FOLDER + Constants.STD_PACKAGE);
            LevelManager.getInstance().setCurrentPackage(Constants.STD_PACKAGE);
            LevelManager.getInstance().setCurrentLevel(startLevel);

            this.screen.dispose();
            this.screen.getGame().setScreen(new GameScreen(this.screen.getGame(), GameScreen.Mode.GAME));
            return true;
        }
        if ( handleID.equals("main_menu_new_game") ) {

            // TODO: Hier nochmal drüber schauen!
            FileHandle tmpHandle = null;

            if ( Gdx.app.getType() == Constants.DESKTOP ) {
                tmpHandle = Gdx.files.local(Constants.GAME_SAVES_PATH);
                Gdx.app.log("PATH:", tmpHandle.file().getAbsolutePath());
            } else if ( Gdx.app.getType() == Constants.ANDROID ) {
                tmpHandle = Gdx.files.local(Constants.GAME_SAVES_PATH);
            } else {
                Gdx.app.log("INPUT", "Unbekannter Application Type!");
            }
            boolean deleted = tmpHandle.deleteDirectory();
            if ( deleted ) {
                Gdx.app.log("INPUT", "tmpHandle deleted!");
            } else {
                Gdx.app.log("INPUT", "tmpHandle konnte nicht deleted werden!!!!!");
            }

            String startLevel = XmlHelper.parseStartLevel(Constants.PACKAGE_FOLDER + Constants.STD_PACKAGE);
            LevelManager.getInstance().setCurrentPackage(Constants.STD_PACKAGE);
            LevelManager.getInstance().setCurrentLevel(startLevel);

            this.screen.dispose();
            this.screen.getGame().setScreen(new GameScreen(this.screen.getGame(), GameScreen.Mode.GAME));
            return true;
        }
        if ( handleID.equals("main_menu_editor") ) {
            this.screen.dispose();
            //System.out.println("ZIIIIIP");
            //ZipHelper.zip(Gdx.files.local("content_packages/lands_of_cinder"), Gdx.files.local("content_packages/lol"));
            this.screen.getGame().setScreen(new EditorMenuScreen(this.screen.getGame()));
            return true;
        }
        if ( handleID.equals("main_menu_exit") ) {
            Gdx.app.exit();
            return true;
        }
        if ( handleID.equals("back_to_main_menu") ) {
            this.screen.dispose();
            //System.out.println("UNNZIIIIP");
            //ZipHelper.unzip(Gdx.files.local("content_packages/lol"), Gdx.files.local("content_packages/lololol"));
            this.screen.getGame().setScreen(new MainMenuScreen(this.screen.getGame()));
            return true;
        }

        /* NEW_WORLD */
        if ( handleID.startsWith("editor_choose_") ) {
            String sceneName = handleID.substring("editor_choose_".length());
            ((EditorChooseWorld) this.screen).openWindow(sceneName);
            return true;
        }
        if ( handleID.equals("editor_choose_world_back") ) {
            this.screen.dispose();
            this.screen.getGame().setScreen(new EditorMenuScreen(this.screen.getGame()));
            return true;
        }
        if ( handleID.startsWith("editor_window_accept_") ) {
            this.screen.dispose();
            String path = handleID.substring("editor_window_accept_".length());
            EditorScreen editorScreen = new EditorScreen(this.screen.getGame(), ((EditorChooseWorld) this.screen).getEditorType());

            if ( ((EditorChooseWorld) this.screen).getEditorType() == EditorScreen.Type.EDIT_WORLD ) {
                editorScreen.scheduleLoad(Constants.EDITOR_SAVES_PATH + path);
            }

            this.screen.getGame().setScreen(editorScreen);
            return true;
        }
        if ( handleID.equals("editor_window_back") ) {
            ((EditorChooseWorld) this.screen).closeWindow();
            return true;
        }

        /* Scene-Auswahl */
        if ( handleID.startsWith("scene_menu_choose_") ) {
            String sceneName = handleID.substring("scene_menu_choose_".length());
            ((SceneMenuScreen) this.screen).openWindow(sceneName);
            return true;
        }

        /* Scene-Vorschaufenster */
        if ( handleID.startsWith("scene_menu_window_accept_") ) {
            String sceneName = handleID.substring("scene_menu_window_accept_".length()) + ".xml";
            ((SceneMenuScreen) this.screen).setScene(sceneName);
            return true;
        }
        if ( handleID.equals("scene_menu_window_close") ) {
            ((SceneMenuScreen) this.screen).closeWindow();
            return true;
        }


        /* Erstellung eines Pakets */
        if ( handleID.equals("editor_menu_window_createpackage") ) {
            if ( ((EditorMenuScreen) this.screen).checkInput() ) {
                if ( !((EditorMenuScreen) this.screen).checkOverwrite() ) {
                    ((EditorMenuScreen) this.screen).createPackage();
                    return true;
                } else {
                    ((EditorMenuScreen) this.screen).showMessageWindow("Paket existiert bereits. Überschreiben?",
                                                                       "Nein",
                                                                       "Ja",
                                                                       "editor_menu_window_close",
                                                                       "editor_menu_overwrite_createpackage_accept");
                    return true;
                }
            } else {
                Gdx.app.log("INPUT", "Bitte gib einen Namen ein!");
                return true;
            }
        }
        if ( handleID.equals("editor_menu_overwrite_createpackage_accept") ) {
            ((EditorMenuScreen) this.screen).createPackage();
            return true;
        }

        /* Erstellung eines Levels */
        if ( handleID.equals("editor_menu_window_createlevel") ) {
            if ( ((EditorMenuScreen) this.screen).checkInput() ) {
                if ( !((EditorMenuScreen) this.screen).checkOverwrite() ) {
                    ((EditorMenuScreen) this.screen).createLevel();
                    this.screen.getGame().setScreen(new SceneMenuScreen(this.screen.getGame(), (EditorMenuScreen) this.screen));
                    return true;
                } else {
                    ((EditorMenuScreen) this.screen).showMessageWindow("Level existiert bereits. Überschreiben?",
                                                                       "Nein",
                                                                       "Ja",
                                                                       "editor_menu_window_close",
                                                                       "editor_menu_overwrite_createlevel_accept");
                    return true;
                }
            } else {
                Gdx.app.log("INPUT", "Bitte gib einen Namen ein!");
                return true;
            }
        }
        if ( handleID.equals("editor_menu_overwrite_createlevel_accept") ) {
            ((EditorMenuScreen) this.screen).createLevel();
            this.screen.getGame().setScreen(new SceneMenuScreen(this.screen.getGame(), (EditorMenuScreen) this.screen));
            return true;
        }

        /* Umbenennen eines Pakets oder Levels */
        if ( handleID.equals("editor_menu_window_rename") ) {
            if ( ((EditorMenuScreen) this.screen).checkInput() ) {
                if ( ((EditorMenuScreen) this.screen).checkOverwrite() ) {
                    ((EditorMenuScreen) this.screen).showMessageWindow("Name existiert bereits. Überschreiben?",
                                                                       "Nein",
                                                                       "Ja",
                                                                       "editor_menu_window_close",
                                                                       "editor_menu_overwrite_rename_accept");
                    return true;
                } else {
                    if ( ((EditorMenuScreen) this.screen).levelActivated() ) {
                        ((EditorMenuScreen) this.screen).renameLevel();
                        return true;
                    } else {
                        ((EditorMenuScreen) this.screen).renamePackage();
                        return true;
                    }
                }
            } else {
                Gdx.app.log("INPUT", "Bitte gib einen Namen ein!");
                return true;
            }
        }
        if ( handleID.equals("editor_menu_overwrite_rename_accept") ) {
            if ( ((EditorMenuScreen) this.screen).levelActivated() ) {
                ((EditorMenuScreen) this.screen).renameLevel();
                return true;
            } else {
                ((EditorMenuScreen) this.screen).renamePackage();
                return true;
            }
        }

        if ( handleID.equals("editor_menu_window_close") ) {
            ((EditorMenuScreen) this.screen).hideWindow();
            return true;
        }


        /* Online EditorMenu */
        if ( handleID.equals("editor_menu_online_upload") ) {
            ((EditorMenuScreen) this.screen).uploadPackage();
            return true;
        }

        if ( handleID.equals("editor_menu_online_level") ) {
            ((EditorMenuScreen) this.screen).showOnlinePackages();
            return true;
        }

        if ( handleID.equals("editor_menu_uploaded_level") ) {
            ((EditorMenuScreen) this.screen).showUploadedPackages();
            return true;
        }

        if ( handleID.equals("editor_menu_online_login") ) {
            ((EditorMenuScreen) this.screen).logIn();
            return true;
        }

        if ( handleID.equals("editor_menu_online_switch_register") ) {
            ((EditorMenuScreen) this.screen).switchToRegister();
            return true;
        }
        if ( handleID.equals("editor_menu_online_switch_login") ) {
            ((EditorMenuScreen) this.screen).switchToLogin();
            return true;
        }
        if ( handleID.startsWith("editor_menu_online_download_") ) {
            String packageID = handleID.substring("editor_menu_online_download_".length());
            ((EditorMenuScreen) this.screen).downloadPackage(packageID);
            return true;
        }

        if ( handleID.startsWith("editor_menu_downloaded_delete_") ) {
            String packageID = handleID.substring("editor_menu_downloaded_delete_".length());
            ((EditorMenuScreen) this.screen).deletePackage(packageID, Constants.ONLINE_FOLDER);
            return true;
        }

        if ( handleID.startsWith("editor_menu_uploaded_delete_") ) {
            String packageID = handleID.substring("editor_menu_uploaded_delete_".length());
            ((EditorMenuScreen) this.screen).deleteUploadedPackage(packageID);
            return true;
        }

        return false;
    }

    // Desktop
    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return true;
    }

    // Android
    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }
}
