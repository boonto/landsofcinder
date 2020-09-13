package de.loc.main;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;

import de.loc.input.userinterface.MenuScreen;
import de.loc.input.userinterface.MenuTable;
import de.loc.sound.SoundEngine;

public class MainMenuScreen extends MenuScreen {

    public MainMenuScreen(LandsOfCinder game) {
        super(game);

        this.setUpUserInterface();

        SoundEngine soundEngine = SoundEngine.getInstance();
        soundEngine.setCombatMusic("epic_battle_against_the_dampfmaschine.ogg");
        soundEngine.setAmbientMusic("loc_main_theme.ogg");
        soundEngine.setMusicVolume(0.9f);
        soundEngine.playAmbientMusic();
    }

    private void setUpUserInterface() {

        MenuTable menuTable = new MenuTable(this.ui, MenuTable.Type.STANDARD);
        ScrollPane scroll = menuTable.getScroll();
        scroll.setScrollingDisabled(true, true);
        menuTable.addHeadingItem();
        menuTable.addTextButtonItem("Fortsetzen", "main_menu_play");
        menuTable.addTextButtonItem("Neues Spiel", "main_menu_new_game");
        menuTable.addTextButtonItem("Editor", "main_menu_editor");
        menuTable.addTextButtonItem("Beenden", "main_menu_exit");

        this.ui.add(menuTable);
    }
}
