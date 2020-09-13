package de.loc.input.userinterface;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;

import de.loc.core.LevelManager;
import de.loc.editor.EditorScreen;
import de.loc.graphics.RenderSystem;
import de.loc.tools.Constants;
import de.loc.tools.XmlHelper;

public class EditorSettingsMenu {
    private final EditorScreen screen;
    private final UserInterface ui;
    private final RenderSystem renderSystem;

    private final Table settingsMenuTable;
    private Table lightSettingsTable;
    private final LevelManager levelManager;

    public EditorSettingsMenu(EditorScreen screen, UserInterface ui, RenderSystem renderSystem) {
        this.screen = screen;
        this.ui = ui;
        this.renderSystem = renderSystem;
        this.levelManager = LevelManager.getInstance();

        this.settingsMenuTable = new Table();
        this.settingsMenuTable.setName("settings");
        this.createSettingsTable();
    }

    private void createSettingsTable() {
        TextButton button_lichteinstellungen = new TextButton("Licht einstellen...", this.ui.getSkin());
        button_lichteinstellungen.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EditorSettingsMenu.this.screen.toggleEditorMenu();
                EditorSettingsMenu.this.showLightSettings();
            }
        });
        this.settingsMenuTable.add(button_lichteinstellungen).left().row();

        //--------------------------------------------------------------------------------

        Array<String> content = XmlHelper.parseMusicList(Constants.MUSIC_LIST_PATH);
        final String usePrevious = "Use music from previous level";
        content.insert(0, usePrevious);

        Label editMusic = new Label("\n" + "MUSIK" + "\n", this.ui.getSkin());
        this.settingsMenuTable.add(editMusic).left().row();

        Label labelAmbient = new Label("Ambient Music   ", this.ui.getSkin());
        this.settingsMenuTable.add(labelAmbient).left();
        final SelectBox<String> ambientMusicBox = new SelectBox<String>(this.ui.getSkin());
        ambientMusicBox.setItems(content);
        ambientMusicBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String music = ambientMusicBox.getSelected();
                if ( music.equals(usePrevious) ) {
                    EditorSettingsMenu.this.levelManager.loadSettings.ambientMusic = "";
                } else {
                    EditorSettingsMenu.this.levelManager.loadSettings.ambientMusic = music;
                }
                System.out.println(EditorSettingsMenu.this.levelManager.loadSettings.ambientMusic);
            }
        });
        this.settingsMenuTable.add(ambientMusicBox).row();

        Label labelCombat = new Label("Combat Music   ", this.ui.getSkin());
        this.settingsMenuTable.add(labelCombat).left();
        final SelectBox<String> combatMusicBox = new SelectBox<String>(this.ui.getSkin());
        combatMusicBox.setItems(content);
        combatMusicBox.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String music = combatMusicBox.getSelected();
                if ( music.equals(usePrevious) ) {
                    EditorSettingsMenu.this.levelManager.loadSettings.combatMusic = "";
                } else {
                    EditorSettingsMenu.this.levelManager.loadSettings.combatMusic = music;
                }
                System.out.println(EditorSettingsMenu.this.levelManager.loadSettings.combatMusic);
            }
        });
        this.settingsMenuTable.add(combatMusicBox).row();

        ambientMusicBox.setSelected(this.levelManager.loadSettings.ambientMusic);
        combatMusicBox.setSelected(this.levelManager.loadSettings.combatMusic);

        //----GITTER----------------------------------

        Label labelGridHeader = new Label("\nGITTER\n", this.ui.getSkin());
        this.settingsMenuTable.add(labelGridHeader).left().row();

        TextButton button_gridOnOff = new TextButton("Gitter ein-/ausschalten", this.ui.getSkin());
        button_gridOnOff.setChecked(true);
        button_gridOnOff.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EditorSettingsMenu.this.levelManager.getLevel().toggleGameGrid();
            }
        });
        this.settingsMenuTable.add(button_gridOnOff).left().row();

    }

    private void showLightSettings() {
        this.lightSettingsTable = new Table();
        this.lightSettingsTable.setBackground(new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_MOUSEOVER)));
        this.lightSettingsTable.setPosition(10, 10);

        //-------LICHT------------------------------------------------------------------

        // Um die neue Lichtposition später mit abspeichern zu können.
        this.settingsMenuTable.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                EditorSettingsMenu.this.levelManager.loadSettings.dirLightPos.set(EditorSettingsMenu.this.renderSystem.getLightPosition());
            }
        });

        final Vector2 horVer = new Vector2();

        Label editLight = new Label("LICHT" + "\n", this.ui.getSkin());
        this.lightSettingsTable.add(editLight).left().row();

        Label labelHor = new Label("Horizonzal: ", this.ui.getSkin());
        final Slider sliderHor = new Slider(-10.0f, 10.0f, 0.20f, false, this.ui.getSkin());
        sliderHor.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float v = sliderHor.getValue();
                EditorSettingsMenu.this.renderSystem.setLightPosition(horVer.y + -v, EditorSettingsMenu.this.renderSystem.getLightPosition().y, horVer.y + v);
                // lightCamera.position.set( vertical + -v, lightCamera.position.y, vertical + v);
                horVer.x = v;
            }
        });

        this.lightSettingsTable.add(labelHor).left();
        this.lightSettingsTable.add(sliderHor).row();

        Label labelVer = new Label("Vertikal: ", this.ui.getSkin());
        final Slider sliderVer = new Slider(0.0f, 10.0f, 0.10f, false, this.ui.getSkin());
        sliderVer.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float v = sliderVer.getValue();
                EditorSettingsMenu.this.renderSystem.setLightPosition(-horVer.x + v, EditorSettingsMenu.this.renderSystem.getLightPosition().y, horVer.x + v);
                horVer.y = v;
            }
        });
        this.lightSettingsTable.add(labelVer).left();
        this.lightSettingsTable.add(sliderVer).row();

        Label labelHeight = new Label("Höhe: ", this.ui.getSkin());
        final Slider sliderHeight = new Slider(15.0f, 20.0f, 0.05f, false, this.ui.getSkin());
        sliderHeight.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float v = sliderHeight.getValue(); //mindestens 15 meter abstand damits in der nearplane bleibt
                EditorSettingsMenu.this.renderSystem.setLightPosition(
                    EditorSettingsMenu.this.renderSystem.getLightPosition().x,
                    v,
                    EditorSettingsMenu.this.renderSystem.getLightPosition().z);
            }
        });
        this.lightSettingsTable.add(labelHeight).left();
        this.lightSettingsTable.add(sliderHeight).row();

        this.renderSystem.setLightPosition(-sliderHor.getValue() + sliderVer.getValue(), sliderHeight.getValue(), sliderHor.getValue() + sliderVer.getValue());

        TextButton button = new TextButton("OK", this.ui.getSkin());
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EditorSettingsMenu.this.hideLightSettings();
                EditorSettingsMenu.this.screen.toggleEditorMenu();
            }
        });
        this.lightSettingsTable.add(button).padTop(10).expandX().fill().row();

        this.ui.getMainTable().setFillParent(true);
        this.ui.getMainTable().add(this.lightSettingsTable);
    }

    public Table getSettingsMenuTable() {
        return this.settingsMenuTable;
    }

    public Table getLightSettingsTable() {
        return this.lightSettingsTable;
    }

    public void show() {
        this.settingsMenuTable.getParent().setVisible(true);
    }

    public void hide() {
        this.settingsMenuTable.getParent().setVisible(false);
    }

    public void hideLightSettings() {
        this.lightSettingsTable.remove();
        this.lightSettingsTable = null;
    }
}
