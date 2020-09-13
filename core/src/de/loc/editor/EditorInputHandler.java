package de.loc.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;

import java.util.StringTokenizer;

import de.loc.core.LandsOfCinderScreen;
import de.loc.core.LevelManager;
import de.loc.graphics.CameraManager;
import de.loc.input.IInput;
import de.loc.input.InputHandler;
import de.loc.input.userinterface.EditorMenuScreen;
import de.loc.quest.QuestEditorScreen;
import de.loc.tools.Constants;
import de.loc.tools.Position;

public class EditorInputHandler extends InputHandler {
    public EditorInputHandler(LandsOfCinderScreen screen, IInput inputSystem) {
        super(screen, inputSystem);
    }

    public boolean handle(String handleID) {

        //Allgemein

        if ( handleID.equals("editor_back") ) {
            this.screen.dispose();
            this.screen.getGame().setScreen(new EditorMenuScreen(this.screen.getGame()));
            return true;
        }

        if ( handleID.equals("editor_back_menu") ) {
            ((EditorScreen) this.screen).save();
            this.screen.dispose();
            this.screen.getGame().setScreen(new EditorMenuScreen(this.screen.getGame()));
            return true;
        }

        if ( handleID.equals("editor_saveScene") ) {
            ((EditorScreen) this.screen).saveScene();
            return true;
        }

        if ( handleID.equals("editor_save") ) {
            ((EditorScreen) this.screen).save();
            return true;
        }

        if ( handleID.equals("editor_window_back") ) {
            ((EditorScreen) this.screen).hideWindow();
            return true;
        }

        if ( handleID.equals("editor_window_save") ) {
            ((EditorScreen) this.screen).save();
            return true;
        }

        //New World
        if ( handleID.equals("editor_grid_plus") ) {
            Slider slider = ((EditorScreen) this.screen).getSlider();
            slider.setValue(slider.getValue() + 1);
            this.scaleGameGrid(slider.getValue());
            return true;
        }

        if ( handleID.equals("editor_grid_minus") ) {
            Slider slider = ((EditorScreen) this.screen).getSlider();

            slider.setValue(slider.getValue() - 1);
            this.scaleGameGrid(slider.getValue());
            return true;
        }

        if ( handleID.equals("editor_grid_paintbrush") ) {
            ((EditorScreen) this.screen).toggleBrushMode();
            return true;
        }

        if ( handleID.equals("editor_settings") ) {
            ((EditorScreen) this.screen).openSettings();
            return true;
        }

        if ( handleID.equals("editor_settings_close") ) {
            ((EditorScreen) this.screen).closeSettings();
            return true;
        }

        if ( handleID.startsWith("editor_slider_grid_") ) {
            String value = handleID.substring("editor_slider_grid_".length());
            this.scaleGameGrid(Float.parseFloat(value));
            return true;
        }

        if ( handleID.equals("editor_background_plus") ) {
            Slider slider = ((EditorScreen) this.screen).getBackgroundSlider();
            slider.setValue(slider.getValue() + 1);
            LevelManager.getInstance().getLevel().scaleBackground(slider.getValue());
            return true;
        }

        if ( handleID.equals("editor_background_minus") ) {
            Slider slider = ((EditorScreen) this.screen).getBackgroundSlider();
            slider.setValue(slider.getValue() - 1);
            LevelManager.getInstance().getLevel().scaleBackground(slider.getValue());
            return true;
        }

        if ( handleID.startsWith("editor_slider_background_") ) {
            String value = handleID.substring("editor_slider_background_".length());
            LevelManager.getInstance().getLevel().scaleBackground(Float.parseFloat(value));
            return true;
        }

        //Edit World
        if ( handleID.equals("editor_toggle_grid") ) {
            LevelManager.getInstance().getLevel().toggleGameGrid();
            return true;
        }

        if ( handleID.equals("editor_objectlist") ) {
            ((EditorScreen) this.screen).showListWindow(Constants.OBJECT_LIST_PATH);
            return true;
        }

        if ( handleID.equals("editor_emptylist") ) {
            ((EditorScreen) this.screen).showListWindow(Constants.EMPTY_LIST_PATH);
            return true;
        }

        if ( handleID.equals("editor_itemlist") ) {
            ((EditorScreen) this.screen).showListWindow(Constants.ITEM_LIST_PATH);
            return true;
        }

        if ( handleID.equals("editor_consumablelist") ) {
            ((EditorScreen) this.screen).showListWindow(Constants.CONSUMABLE_LIST_PATH);
            return true;
        }

        if ( handleID.equals("editor_equippablelist") ) {
            ((EditorScreen) this.screen).showListWindow(Constants.EQUIPPABLE_LIST_PATH);
            return true;
        }

        if ( handleID.equals("editor_moblist") ) {
            ((EditorScreen) this.screen).showListWindow(Constants.MOB_LIST_PATH);
            return true;
        }

        if ( handleID.equals("editor_add_npc") ) {
            ((EditorScreen) this.screen).showNpcListWindow();
            return true;
        }

        if ( handleID.equals("editor_npceditor") ) {
            NpcEditorScreen npcEditor = new NpcEditorScreen(this.screen.getGame(), ((EditorScreen) this.screen));
            this.screen.getGame().setScreen(npcEditor);

            return true;
        }

        if ( handleID.equals("editor_questeditor") ) {
            QuestEditorScreen questEditor = new QuestEditorScreen(this.screen.getGame(), ((EditorScreen) this.screen));
            this.screen.getGame().setScreen(questEditor);

            return true;
        }

        if ( handleID.equals("editor_player") ) {
            ((EditorScreen) this.screen).showListWindow(Constants.PLAYER_LIST_PATH);
            return true;
        }

        if ( handleID.equals("editor_showDialogEditor") ) {
            DialogEditorScreen dialogEditor = new DialogEditorScreen(this.screen.getGame(), ((EditorScreen) this.screen));
            this.screen.getGame().setScreen(dialogEditor);
            return true;
        }

        if ( handleID.equals("editor_add_quest") ) {
            ((EditorScreen) this.screen).showQuestList();
            return true;
        }

        if ( handleID.equals("game_close_window") ) {
            ((EditorScreen) this.screen).hideWindows();

        }

        if ( handleID.startsWith("List_") ) {
            handleID = handleID.substring(5);
            StringTokenizer tokenizer = new StringTokenizer(handleID, "_");
            String listType = tokenizer.nextToken();
            String listIndex = tokenizer.nextToken();

            ((EditorScreen) this.screen).chooseEntity(listType, listIndex);
        }

        if ( handleID.startsWith("QuestItem_") ) {
            String questName = handleID.substring(10);
            ((EditorScreen) this.screen).addQuestToEntity(questName);
        }

        if ( handleID.equals("editor_delete_entity") ) {
            ((EditorScreen) this.screen).deleteCurrentEntity();
        }

        if ( handleID.equals("editor_rotate_entity") ) {
            ((EditorScreen) this.screen).rotateCurrentEntity();
        }

        if ( handleID.equals("editor_show_settings_menu") ) {
            ((EditorScreen) this.screen).showSettingsMenu();
        }
        if ( handleID.equals("editor_chest") ) {
            ((EditorScreen) this.screen).showListWindow(Constants.CHEST_LIST_PATH);
        }

        // NEUES EDITOR UI
        if ( handleID.equals("new_ui") ) {
            ((EditorScreen) this.screen).toggleEditorMenu();
        }

        if ( handleID.equals("flag_player_and_npc") ) {
            //((EditorScreen) screen).showItems(Constants.PLAYER_LIST_PATH);
            ((EditorScreen) this.screen).showPlayerAndNPCItems();
        }
        if ( handleID.equals("flag_mob") ) {
            ((EditorScreen) this.screen).showItems(Constants.MOB_LIST_PATH);
        }
        if ( handleID.equals("flag_item") ) {
            ((EditorScreen) this.screen).showItems(Constants.ITEM_LIST_PATH);
        }
        if ( handleID.equals("flag_consumable") ) {
            ((EditorScreen) this.screen).showItems(Constants.CONSUMABLE_LIST_PATH);
        }
        if ( handleID.equals("flag_equippable") ) {
            ((EditorScreen) this.screen).showItems(Constants.EQUIPPABLE_LIST_PATH);
        }
        if ( handleID.equals("flag_object") ) {
            ((EditorScreen) this.screen).showItems(Constants.OBJECT_LIST_PATH);
        }
        if ( handleID.equals("flag_chest") ) {
            ((EditorScreen) this.screen).showItems(Constants.CHEST_LIST_PATH);
        }
        if ( handleID.equals("flag_empty") ) {
            ((EditorScreen) this.screen).showItems(Constants.EMPTY_LIST_PATH);
        }
        if ( handleID.equals("flag_quest") ) {
            Gdx.app.log("INPUT", "Questmenü ist in Arbeit!");
            QuestEditorScreen questEditor = new QuestEditorScreen(this.screen.getGame(), ((EditorScreen) this.screen));
            this.screen.getGame().setScreen(questEditor);
        }
        if ( handleID.equals("flag_dialog") ) {
            Gdx.app.log("INPUT", "Dialogmenü ist in Arbeit! Bis dahin gibts hier einfach so den NPC-Editor!!!");
            NpcEditorScreen npcEditor = new NpcEditorScreen(this.screen.getGame(), ((EditorScreen) this.screen));
            this.screen.getGame().setScreen(npcEditor);
        }
        if ( handleID.equals("flag_settings") ) {
            ((EditorScreen) this.screen).showSettingsMenu();
        }
        if ( handleID.equals("flag_mainmenu") ) {
            ((EditorScreen) this.screen).showMenuWindow();
        }

        return false;
    }

    private void scaleGameGrid(float size) {
        // set the current Background-Scene-Size to compute appropriate values
        ((EditorScreen) this.screen).heightTextureParser.setSceneSize(LevelManager.getInstance().getLevel().getBackgroundSize());
        ((EditorScreen) this.screen).heightTextureParser.updateGameGridHeight(LevelManager.getInstance().getLevel().getGameGrid());

        LevelManager.getInstance().getLevel().scaleGameGrid((int) size, (int) size, false);
        LevelManager.getInstance().getLevel().updateGameGrid(false);
    }

    //Desktop
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX, screenY, pointer, button);

        Gdx.app.log("INPUT", "INPUT_HANDLER CLICK: " + screenX + " " + screenY);

        if ( button == Input.Buttons.LEFT ) {
            Ray pickRay = CameraManager.getPickRay(screenX, screenY);
            Position position = CameraManager.getGridFromScreenCoords(pickRay);
            if ( position != null ) {
                this.inputSystem.clicked(pickRay, position);
            }
        } else if ( button == Input.Buttons.RIGHT ) {
            this.inputSystem.rightClick();
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        EditorInputSystem sys = (EditorInputSystem) this.inputSystem;
        sys.setCurrentMousePosition(screenX, screenY);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if ( !((EditorScreen) this.screen).getBrushMode() ) {
            super.touchDragged(screenX, screenY, pointer);
        }

        Ray pickRay = CameraManager.getPickRay(screenX, screenY);
        Position position = CameraManager.getGridFromScreenCoords(pickRay);
        if ( position != null ) {
            this.inputSystem.dragged(position);
        }
        return false;
    }
    //Android

}
