package de.loc.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;

import java.io.IOException;
import java.io.StringWriter;

import de.loc.combat.CombatSystem;
import de.loc.core.FeedbackSystem;
import de.loc.core.LandsOfCinderScreen;
import de.loc.core.LandsOfCinderSystem;
import de.loc.core.LevelManager;
import de.loc.core.PositionSystem;
import de.loc.core.TypeComponent;
import de.loc.dialog.DialogSystem;
import de.loc.editor.SceneLoader;
import de.loc.editor.SceneSaver;
import de.loc.event.Event;
import de.loc.event.EventListener;
import de.loc.event.EventSystem;
import de.loc.graphics.AnimationSystem;
import de.loc.graphics.GameRenderSystem;
import de.loc.graphics.ModelComponentCreator;
import de.loc.input.userinterface.CombatInterface;
import de.loc.input.userinterface.DialogWindow;
import de.loc.input.userinterface.InventoryWindow;
import de.loc.input.userinterface.LandsOfCinderWindow;
import de.loc.input.userinterface.MenuWindow;
import de.loc.input.userinterface.MerchantWindow;
import de.loc.input.userinterface.QuestLogWindow;
import de.loc.input.userinterface.SkillTreeWindow;
import de.loc.input.userinterface.UserInterface;
import de.loc.item.InventoryComponent;
import de.loc.item.ItemSystem;
import de.loc.item.MerchantComponent;
import de.loc.main.LandsOfCinder;
import de.loc.movement.MovementAISystem;
import de.loc.movement.MovementSystem;
import de.loc.online.OnlineContentSystem;
import de.loc.physics.PhysicsSystem;
import de.loc.quest.QuestSystem;
import de.loc.sound.SoundEngine;
import de.loc.tools.Constants;

public class GameScreen extends LandsOfCinderScreen {

    //UI
    private MenuWindow settingsWindow;
    private InventoryWindow inventoryWindow;
    private MerchantWindow merchantWindow;

    private LandsOfCinderWindow warning;
    private QuestLogWindow questLogWindow;
    private DialogWindow dialogWindow;
    private SkillTreeWindow skillTreeWindow;
    private CombatInterface combatInterface;

    private Table windowTable;
    private NinePatchDrawable windowBackground;
    private LandsOfCinderWindow window;
    private TextField input;

    /* Window Breite und Höhe */
    private static final float WINDOW_WIDTH = Constants.WIDTH / 2;
    private static final float WINDOW_HEIGHT = Constants.HEIGHT / 5;

    public enum Mode {
        GAME,
        TEST
    }

    private final Mode mode;

    private QuestSystem questSystem;

    public GameScreen(LandsOfCinder game, Mode mode) {
        super(game);
        this.mode = mode;
        if ( mode == Mode.TEST ) {
            FileHandle tmpHandle = null;

            if ( Gdx.app.getType() == Constants.DESKTOP ) {
                tmpHandle = Gdx.files.local(Constants.GAME_TMP_DIRECTORY);
                Gdx.app.log("PATH:", tmpHandle.file().getAbsolutePath());
            } else if ( Gdx.app.getType() == Constants.ANDROID ) {
                tmpHandle = Gdx.files.local(Constants.GAME_TMP_DIRECTORY);
            } else {
                Gdx.app.log("Fehler", "Unbekannter Application Type!");
            }
            boolean deleted = tmpHandle.deleteDirectory();
            if ( !deleted ) {
                Gdx.app.log("NNNEEEEEIN", "tmpHandle konnte nicht deleted werden!!!!!");
            } else {
                Gdx.app.log("ja", "tmpHandle deleted!");
            }
        }
        this.initEntityComponentSystem();

    }

    private void initEntityComponentSystem() {

        // Level Manager
        String lastLevel = "";
        if ( this.mode == Mode.GAME ) {
            try {
                FileHandle settingsFile = null;
                if ( Gdx.app.getType() == Constants.DESKTOP ) {
                    settingsFile = Gdx.files.local(Constants.GAME_SAVES_PATH + "settings.xml");
                } else if ( Gdx.app.getType() == Constants.ANDROID ) {
                    settingsFile = Gdx.files.internal(Constants.GAME_SAVES_PATH + "settings.xml");
                }
                XmlReader xmlReader = new XmlReader();
                XmlReader.Element xmlScene = xmlReader.parse(settingsFile);
                lastLevel = xmlScene.getChildByName("LastLevel").getText();
            } catch ( Exception e ) {
                Gdx.app.log("Warning", "Could not open Settings File");
            }
            if ( !lastLevel.isEmpty() ) {
                LevelManager.getInstance().setCurrentLevel(lastLevel);
            }
        }
        String packageName = LevelManager.getInstance().getCurrentPackage();
        String levelName = LevelManager.getInstance().getCurrentLevelFileName();
        String path = Constants.PACKAGE_FOLDER + packageName + "/levels/" + levelName;

        SoundEngine soundEngine = SoundEngine.getInstance();
        soundEngine.addToEventSystem();
        soundEngine.setMusicVolume(0.5f);
        soundEngine.setSoundEffectVolume(1.0f);

        GameInputSystem gameInputSystem = new GameInputSystem();
        this.engine.addSystem(gameInputSystem);

        this.inputHandler = new GameInputHandler(this, gameInputSystem);

        this.renderSystem = new GameRenderSystem();
        this.engine.addSystem(this.renderSystem);

        this.load(path);

        if ( !LevelManager.getInstance().hasPlayer() ) {
            this.ui = new UserInterface(this.inputHandler);

            this.addWarning();

            return;
        }

        MovementSystem movementSystem = new MovementSystem();
        this.engine.addSystem(movementSystem);

        PositionSystem positionSystem = new PositionSystem(this);
        this.engine.addSystem(positionSystem);

        ItemSystem itemSystem = new ItemSystem();
        this.engine.addSystem(itemSystem);

        MovementAISystem movementAiSystem = new MovementAISystem(3f);
        this.engine.addSystem(movementAiSystem);

        AnimationSystem animationSystem = new AnimationSystem();
        this.engine.addSystem(animationSystem);

        ViewportSystem viewportSystem = new ViewportSystem();
        this.engine.addSystem(viewportSystem);

        PhysicsSystem physicsSystem = new PhysicsSystem();
        this.engine.addSystem(physicsSystem);

        CombatSystem combatSystem = new CombatSystem();
        this.engine.addSystem(combatSystem);

        DialogSystem dialogSystem = new DialogSystem();
        this.engine.addSystem(dialogSystem);

        this.questSystem = new QuestSystem();
        this.engine.addSystem(this.questSystem);

        CharDevSystem charDevSystem = new CharDevSystem();
        this.engine.addSystem(charDevSystem);

        this.setUpUserInterface();
        this.setUpQuestLogUI(this.questSystem);
        this.setUpCombatUI(combatSystem);
        this.setUpInventoryUI();
        this.setUpMerchantUI();

        LevelManager.getInstance().getLevel().setDrawGrid(false);

        //TODO noch vom ui abhängig
        FeedbackSystem feedbackSystem = new FeedbackSystem(this.ui);
        this.engine.addSystem(feedbackSystem);

        OnlineContentSystem onlineSystem = new OnlineContentSystem(this, this.ui);
        this.engine.addSystem(onlineSystem);

        //alle systeme resetten
        for ( EntitySystem entitySystem : this.engine.getSystems() ) {
            ((LandsOfCinderSystem) entitySystem).reset();
        }
        if ( this.mode == Mode.GAME ) {
            this.questSystem.load();
        }
    }

    private void setUpUserInterface() {
        this.ui = new UserInterface(this.inputHandler);
        this.ui.setScreenSize(20, Color.BLACK);

        this.setupWindowTable();
        this.windowBackground = new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_PAPER_BRIGHT));

        this.ui.addButton(Constants.UI_ICON_INVENTAR, 5, 5, 100, 100, "game_settings");

        this.setUpSettingsUI();
        this.setUpDialogUI();
        this.setUpSkillTree();
    }

    private void setUpSettingsUI() {
        this.settingsWindow = new MenuWindow(this.ui, MenuWindow.Type.LEFT);

        this.settingsWindow.setBackground(new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCHES_PATH + "papier_links.9.png")));

        //settingsWindow.addTextButtonItem("Save Game", "game_save");
        this.settingsWindow.addTextButtonItem("Inventar", "game_inventory");
        this.settingsWindow.addTextButtonItem("Tagebuch", "game_questlog");
        this.settingsWindow.addTextButtonItem("SkillTree", "game_skill_tree");
        //settingsWindow.addFileSelectBoxItem(Constants.EDITOR_SAVES_PATH, "game_load_");
        //settingsWindow.addFileSelectBoxItem(Constants.GAME_SAVES_PATH, "game_load_");
        this.settingsWindow.addBackButton("game_settings_close");
        this.settingsWindow.addTextButtonItem("Menü", "game_back_menu");
        this.settingsWindow.addTextButtonItem("Gitter anzeigen", "game_toggle_grid");

        this.ui.add(this.settingsWindow);

        this.settingsWindow.hide();
    }

    private void setUpInventoryUI() {
        this.inventoryWindow = new InventoryWindow(this.ui);
        this.inventoryWindow.addBackButton("game_close_window");
        this.inventoryWindow.setBackground(new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_PAPER_BRIGHT)));
        this.ui.add(this.inventoryWindow);
        this.inventoryWindow.hide();

        EventSystem.getInstance().addListener(new EventListener() {
            @Override
            public void update(Event e) {
                GameScreen.this.showWindow(Window.INVENTORY_LIST);
                GameScreen.this.inventoryWindow.updateInventory(
                    ((Entity) e.args[0]).getComponent(InventoryComponent.class),
                    LevelManager.getInstance().getPlayerEntity().getComponent(InventoryComponent.class));
            }
        }, EventSystem.EventType.SHOW_INVENTORY_LIST);
    }

    private void setUpMerchantUI() {
        this.merchantWindow = new MerchantWindow(this.ui);
        this.merchantWindow.addBackButton("game_close_window");
        this.merchantWindow.setBackground(new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_PAPER_BRIGHT)));
        this.ui.add(this.merchantWindow);
        this.merchantWindow.hide();

        EventSystem.getInstance().addListener(new EventListener() {
            @Override
            public void update(Event e) {
                GameScreen.this.showWindow(Window.MERCHANT_WINDOW);
                GameScreen.this.merchantWindow.show(
                    ((Entity) e.args[0]).getComponent(MerchantComponent.class),
                    LevelManager.getInstance().getPlayerEntity().getComponent(InventoryComponent.class));
            }
        }, EventSystem.EventType.SHOW_MERCHANT_WINDOW);
    }

    private void setUpQuestLogUI(QuestSystem questSystem) {
        this.questLogWindow = new QuestLogWindow(this.ui, questSystem.getQuestLog());

        this.questLogWindow.addBackButton("game_close_window");
        this.questLogWindow.setBackground(new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_PAPER_BRIGHT)));
        //questLogWindow.padTop((float) 50.0);

        this.ui.add(this.questLogWindow);
        this.questLogWindow.hide();
    }

    private void setUpDialogUI() {
        this.dialogWindow = new DialogWindow(this.ui);
        //dialogWindow.addBackButton("game_close_window");
        this.dialogWindow.setBackground(new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_PAPER_BRIGHT)));

        EventSystem.getInstance().addListener(new EventListener() {
            @Override
            public void update(Event e) {
                if ( e.eventType == EventSystem.EventType.TALK_EVENT || e.eventType == EventSystem.EventType.COMMENTARY ) {
                    GameScreen.this.showWindow(Window.DIALOG);
                    GameScreen.this.dialogWindow.addDialog((String) e.args[0], (String) e.args[1], (String) e.args[2]);
                } else if ( e.eventType == EventSystem.EventType.DIALOG_ENDED ) {
                    GameScreen.this.dialogWindow.hide();
                }
            }
        }, EventSystem.EventType.TALK_EVENT, EventSystem.EventType.COMMENTARY, EventSystem.EventType.DIALOG_ENDED);

        this.ui.add(this.dialogWindow);
        this.dialogWindow.hide();
    }

    private void setUpSkillTree() {
        this.skillTreeWindow = new SkillTreeWindow(this.ui);

        //skillTreeWindow.addBackButton("game_close_window");
        this.skillTreeWindow.setBackground(new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_PAPER_BRIGHT)));
        //skillTreeWindow.setHeading("Hallo");
        this.skillTreeWindow.buildSkillTreePoint("punch");
        this.skillTreeWindow.buildSkillTreePoint("uppercut");
        this.skillTreeWindow.buildSkillTreePoint("steam");
        this.ui.add(this.skillTreeWindow);
        //ui.add(button);
        this.skillTreeWindow.hide();

    }

    public void showSkillTree() {
        this.skillTreeWindow.toggle();
    }

    public void showWindow(Window window) {
        this.hideWindows();
        switch ( window ) {
            case QUESTLOG:
                this.questLogWindow.show();
                break;
            case INVENTORY:
                // hier muss manuell geupdated werden, da die Funktion von aussen und nicht über Events getriggert wird
                this.inventoryWindow.updateInventory(LevelManager.getInstance().getPlayerEntity().getComponent(InventoryComponent.class));
                this.inventoryWindow.show();
                break;
            case DIALOG:
                this.dialogWindow.show();
                break;
            case INVENTORY_LIST:
                // updaten passiert automatisch, siehe setUpInventoryUI
                this.inventoryWindow.show();
                break;
            case MERCHANT_WINDOW:
                // updaten passiert automatisch, siehe setUpInventoryUI
                this.merchantWindow.show();
                break;
            default:
                break;
        }
    }

    private void setUpCombatUI(CombatSystem combatSystem) {
        this.combatInterface = new CombatInterface(this.ui, combatSystem);
    }

    public void hideWindows() {
        this.questLogWindow.hide();
        //characterWindow.setVisible(false);
        this.dialogWindow.hide();
        this.inventoryWindow.hide();
        this.merchantWindow.hide();
    }

    public void toggleSettings() {
        this.settingsWindow.toggle();
    }

    public void showSaveWindow() {
        this.showInputWindow("Zurück", "Speichern", "game_window_back", "game_window_save");
    }

    public void save() {
        if ( !this.input.getText().equals("") ) {
            SceneSaver saver = new SceneSaver(SceneSaver.Type.SAVE_GAME, this.getEngine().getEntitiesFor(Family.all(TypeComponent.class).get()));
            saver.setInput(this.input.getText());
        }
    }

    @Override
    protected void load(String path) {

        //saveCurrentLevelState();

        SceneLoader loader = new SceneLoader();
        this.engine.removeAllEntities();

        FileHandle levelFile = null;
        FileHandle tempFile = null;
        if ( this.mode == Mode.GAME ) {

            if ( Gdx.app.getType() == Constants.DESKTOP ) {
                levelFile = Gdx.files.internal(path);
                tempFile = Gdx.files.local(Constants.GAME_SAVES_PATH + levelFile.name());
            } else if ( Gdx.app.getType() == Constants.ANDROID ) {
                levelFile = Gdx.files.internal(path);
                tempFile = Gdx.files.internal(Constants.GAME_SAVES_PATH + levelFile.name());
            } else {
                Gdx.app.log("Fehler", "Unbekannter Application Type!");
            }
        } else {
            if ( Gdx.app.getType() == Constants.DESKTOP ) {
                levelFile = Gdx.files.internal(path);
                tempFile = Gdx.files.local(Constants.GAME_TMP_DIRECTORY + levelFile.name());
            } else if ( Gdx.app.getType() == Constants.ANDROID ) {
                levelFile = Gdx.files.local(path);
                tempFile = Gdx.files.local(Constants.GAME_TMP_DIRECTORY + levelFile.name());
            } else {
                Gdx.app.log("Fehler", "Unbekannter Application Type!");
            }
        }

        ModelComponentCreator.getInstance().particleSystem.removeAll();

        // TODO: slow on Android! create a xml list were all previous visited levels are listed!
        if ( tempFile.exists() ) {
            loader.load(tempFile.path(), this.getEngine());
        } else {
            loader.load(path, this.getEngine());
        }

        LevelManager.getInstance().getLevel().setDrawGrid(false);

        //alle systeme resetten
        for ( EntitySystem entitySystem : this.engine.getSystems() ) {
            ((LandsOfCinderSystem) entitySystem).reset();
        }

        this.loadScheduled = false;
    }

    public void saveCurrentLevelState() {
        SceneSaver saver = new SceneSaver(SceneSaver.Type.SAVE_EDITOR, this.engine.getEntitiesFor(Family.all(TypeComponent.class).get()));
        String levelName = LevelManager.getInstance().getCurrentLevelFileName();
        if ( !levelName.isEmpty() ) {
            if ( this.mode == Mode.TEST ) {
                saver.save(Constants.GAME_TMP_DIRECTORY + LevelManager.getInstance().getCurrentLevelFileName());
            } else {
                saver.save(Constants.GAME_SAVES_PATH + LevelManager.getInstance().getCurrentLevelFileName());
            }
        }
    }

    public void addWarning() {
        Label label = new Label("Das Level hat keinen Spieler!", this.ui.getSkin());

        this.warning = this.ui.addTwoButtonWindow(label, "Ok!", "Ok?", "game_back_choose", "game_back_choose");
        this.warning.setBackground(new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_PAPER_DARK)));
    }

    /* Aufbau eines WindowTables */
    private void setupWindowTable() {
        /* windowTable */
        this.windowTable = new Table();
        this.windowTable.setName("windowTable");
        this.windowTable.pad(20.0f);
    }

    /* Ein Eingabefenster erscheint */
    private void showInputWindow(String leftButton, String rightButton, String leftHandle, String rightHandle) {
        this.hideWindow();

        this.input = new TextField("", this.ui.getSkin());
        this.windowTable.add(this.input).expandX().prefWidth(Constants.WIDTH / 3);

        this.window = this.ui.addTwoButtonWindow(this.windowTable, leftButton, rightButton, leftHandle, rightHandle);
        this.window.setBackground(this.windowBackground);
        this.window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.window.setPosition(Constants.WIDTH / 2 - WINDOW_WIDTH / 2, Constants.HEIGHT / 2 - WINDOW_HEIGHT / 2);
    }

    /* Lässt das aktive Fenster verschwinden */
    public void hideWindow() {
        if ( this.window != null ) {
            this.window.remove();
            this.windowTable.clearChildren();
        }
    }

    public enum Window {
        QUESTLOG,
        INVENTORY,
        DIALOG,
        INVENTORY_LIST, // nicht das Player Inventar, sondern Loot!
        MERCHANT_WINDOW
    }

    @Override
    public void dispose() {
        this.saveCurrentLevelState();
        if ( this.mode == Mode.GAME ) {
            this.questSystem.saveQuestState();
            this.saveLastGameSettings();
        }
        super.dispose();

    }

    private void saveLastGameSettings() {
        StringWriter writer = new StringWriter();
        XmlWriter xmlWriter = new XmlWriter(writer);

        try {
            xmlWriter.element("Settings");

            xmlWriter.element("LastLevel").text(LevelManager.getInstance().getCurrentLevelFileName());
            xmlWriter.pop();

            xmlWriter.pop();
            xmlWriter.close();

        } catch ( IOException e ) {
            e.printStackTrace();
        }

        FileHandle file = Gdx.files.local(Constants.GAME_SAVES_PATH + "settings.xml");
        file.writeString(writer.toString(), false);
    }
}
