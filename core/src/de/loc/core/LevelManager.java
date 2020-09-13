package de.loc.core;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

public final class LevelManager implements Disposable {

    private static LevelBackground level;

    private String currentLevel;
    private String currentPackage;

    private Entity playerEntity;

    // Die LoadSettings beinhalten alle Einstellungen, die beim Laden eines neuen 
    // Levels aktualisiert werden können. 
    public LoadSettings loadSettings;

    private LevelManager() {
        level = new LevelBackground();
        this.loadSettings = new LoadSettings();
    }

    public void setEngine(Engine engine) {
        level.setEngine(engine);
    }

    public void setupEditorEntities() {
        //TODO soll endlich auch noch weg
        level.addEntities();
    }

    private static final class ManagerHolder {
        static final LevelManager manager = new LevelManager();
    }

    public static LevelManager getInstance() {

        return ManagerHolder.manager;
    }

    public LevelBackground getLevel() {
        return level;
    }

    public boolean hasPlayer() {
        return this.playerEntity != null;
    }

    public Entity getPlayerEntity() {
        return this.playerEntity;
    }

    public void setPlayerEntity(Entity playerEntity) {
        this.playerEntity = playerEntity;
    }

    public String getCurrentLevelFileName() {
        return this.currentLevel;
    }

    public void setCurrentPackage(String currentPackage) {
        this.currentPackage = currentPackage;
    }

    public String getCurrentPackage() {
        return this.currentPackage;
    }

    public void setCurrentLevel(String currentLevel) {
        this.currentLevel = currentLevel;
    }

    @Override
    public void dispose() {
        level.dispose();
        this.currentLevel = null;
        this.currentPackage = null;
        this.playerEntity = null;
    }

    public static final class LoadSettings {

        public Vector3 dirLightPos;
        public String ambientMusic;
        public String combatMusic;

        public LoadSettings() {
            this.ambientMusic = "";
            this.combatMusic = "";
            this.dirLightPos = new Vector3();
        }
    }
}
