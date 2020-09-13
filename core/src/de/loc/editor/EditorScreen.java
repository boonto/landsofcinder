package de.loc.editor;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.XmlReader;

import java.util.ArrayList;

import de.loc.core.LandsOfCinderScreen;
import de.loc.core.LevelManager;
import de.loc.core.PositionComponent;
import de.loc.core.TypeComponent;
import de.loc.dialog.NameComponent;
import de.loc.graphics.CameraManager;
import de.loc.graphics.RenderSystem;
import de.loc.graphics.RotationComponent;
import de.loc.input.InputComponent;
import de.loc.input.userinterface.EditorMenuScreen;
import de.loc.input.userinterface.EditorSettingsMenu;
import de.loc.input.userinterface.EntityContextMenu;
import de.loc.input.userinterface.EntityListWindow;
import de.loc.input.userinterface.LandsOfCinderWindow;
import de.loc.input.userinterface.MenuWindow;
import de.loc.input.userinterface.UserInterface;
import de.loc.main.LandsOfCinder;
import de.loc.quest.QuestParser;
import de.loc.sound.SoundEngine;
import de.loc.tools.Constants;
import de.loc.tools.HeightTextureParser;
import de.loc.tools.ListItem;
import de.loc.tools.Position;
import de.loc.tools.XmlHelper;

public class EditorScreen extends LandsOfCinderScreen {
    HeightTextureParser heightTextureParser = null;
    private EditorInputSystem inputSystem;
    private final Type editorType;
    private MenuWindow menuWindow;
    private Slider slider;

    LevelManager manager;
    String currentPackage;
    String currentLevel;

    private Quickbar quickbar;
    private EditorMenu editormenu;

    private Slider backgroundSlider;

    private Table activeQuickslot;

    private EntityListWindow entityListWindow;
    private EditorSettingsMenu editorSettingsMenu;
    private ArrayList<ListItem> currentEntityList;

    private EntityContextMenu entityContextMenu;

    private Table windowTable;
    private LandsOfCinderWindow window;
    private TextField input;

    private NinePatchDrawable active;
    private NinePatchDrawable inactive;
    private NinePatchDrawable windowBackground;

    /* UI Schriftgröße und Schriftfarbe*/
    private static final int UI_TEXTSIZE = 18;
    private static final Color UI_TEXTCOLOR = Color.BLACK;

    /* Abstand zwischen den einzelnen UI-Elementen */
    private static final int TABLE_OFFSET = 20;

    /* Window Breite und Höhe */
    private static final float WINDOW_WIDTH = Constants.WIDTH / 2.0f;
    private static final float WINDOW_HEIGHT = Constants.HEIGHT / 5.0f;

    /* Konstruktor */
    public EditorScreen(LandsOfCinder game, Type type) {
        super(game);
        this.editorType = type;

        this.manager = LevelManager.getInstance();
        this.currentPackage = this.manager.getCurrentPackage();
        this.currentLevel = this.manager.getCurrentLevelFileName();

        this.setupEditorScreen();
    }

    /* Aufbau des EditorScreens */
    private void setupEditorScreen() {
        this.init();
        this.setupUserInterface();
    }

    /* Initialisierung von wichtigen Daten am Anfang */
    private void init() {
        /* Musik und Sound */
        SoundEngine.getInstance().setMusicVolume(0.0f);

        //Systeme
        this.renderSystem = new RenderSystem();
        this.engine.addSystem(this.renderSystem);
        /* Zwischenschritt bei neuer Welt */
        if ( this.editorType == Type.NEW_WORLD ) {
            this.initNewWorld();
        }
        this.inputSystem = new EditorInputSystem(this, this.editorType);
        this.inputHandler = new EditorInputHandler(this, this.inputSystem);
        this.engine.addSystem(this.inputSystem);

        /* UI */
        this.ui = new UserInterface(this.inputHandler);

        /* Aktive Tables */
        this.activeQuickslot = new Table();
        this.activeQuickslot.setName("");

        /* Sonstiges */
        this.active = new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_PAPER_DARK));
        this.inactive = new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_PAPER_BRIGHT));
        this.windowBackground = new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_PAPER_BRIGHT));
    }

    /* Zwischenschritt bei neuer Welt */
    private void initNewWorld() {
        this.engine.removeAllEntities();
        this.heightTextureParser = new HeightTextureParser(LevelManager.getInstance().getLevel().getBackgroundSize());
        this.manager.setupEditorEntities();
    }

    /* Aufbau der Benutzeroberfläche */
    private void setupUserInterface() {
        /* Schriftgröße und Schriftfarbe */
        this.ui.setScreenSize(UI_TEXTSIZE, UI_TEXTCOLOR);

        /* Aufbau eines klassischen Fenster-Tables */
        this.setupWindowTable();

        if ( this.editorType == Type.EDIT_WORLD ) {
            this.setupUiEditWorld();
        } else if ( this.editorType == Type.NEW_WORLD ) {
            this.setupUiNewWorld();
        } else {
            Gdx.app.log("EDITOR", "Fehler: Unbekannter Editor-Type!");
        }
    }

    /* Aufbau eines WindowTables */
    private void setupWindowTable() {
        /* windowTable */
        this.windowTable = new Table();
        this.windowTable.setName("windowTable");
        this.windowTable.pad(20.0f);
    }

    /* Aufbau der Benutzeroberfläche wenn ein Level editiert wird */
    private void setupUiEditWorld() {
        /* ALTES UI */
        this.menuWindow = new MenuWindow(this.ui, MenuWindow.Type.LEFT);

        this.menuWindow.addTextButtonItem("Spieler", "editor_player");
        this.menuWindow.addTextButtonItem("NPCs", "editor_add_npc");
        this.menuWindow.addTextButtonItem("Gegner", "editor_moblist");
        this.menuWindow.addTextButtonItem("Objekte", "editor_objectlist");
        this.menuWindow.addTextButtonItem("Items", "editor_itemlist");
        this.menuWindow.addTextButtonItem("Ausrüstung", "editor_equippablelist");
        this.menuWindow.addTextButtonItem("Verzehrbares", "editor_consumablelist");
        this.menuWindow.addTextButtonItem("Empties", "editor_emptylist");
        this.menuWindow.addTextButtonItem("Truhe", "editor_chest");
        this.menuWindow.addTextButtonItem("NPC Editor", "editor_npceditor");
        this.menuWindow.addTextButtonItem("Quest Editor", "editor_questeditor");
        this.menuWindow.addTextButtonItem("Gitter anzeigen", "editor_toggle_grid");
        this.menuWindow.addTextButtonItem("Einstellungen", "editor_show_settings_menu");
        this.menuWindow.addTextButtonItem("Menü", "editor_back_menu");
        this.menuWindow.addBackButton("editor_settings_close");
        this.menuWindow.setBackground(new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCHES_PATH + "papier_links.9.png")));

        this.entityListWindow = new EntityListWindow(this.ui);
        this.entityListWindow.addBackButton("game_close_window");
        this.entityListWindow.setBackground(new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_PAPER_BRIGHT)));
        this.ui.add(this.entityListWindow);
        this.entityListWindow.hide();

        this.ui.setScreenSize(20, Color.WHITE);
        this.ui.addButton(Constants.UI_ICON_EDITORMENU, 10, (int) ((float) Gdx.graphics.getHeight() - Constants.UI_IMAGE_SIZE), "new_ui");
        //ui.addButton(Constants.UI_ICON_SETTINGS, 0, (int) (Gdx.graphics.getHeight() - Constants.UI_IMAGE_SIZE), "editor_settings");
        //ui.addTextButton("Neues UI", (int) (Constants.WIDTH-60), (int) (Constants.HEIGHT-Constants.UI_IMAGE_SIZE), "new_ui");

        this.entityContextMenu = new EntityContextMenu(this.ui);
        this.editorSettingsMenu = new EditorSettingsMenu(this, this.ui, this.renderSystem);



        /* NEUES UI */

        /* Editormenü und Schnellleiste */
        this.editormenu = new EditorMenu(this.ui);
        this.quickbar = new Quickbar(this.ui, 10);

        /* Hinzufügen */
        this.ui.add(this.quickbar);
        this.ui.add(this.editormenu);

        /* Neues UI unsichtbar machen */
        this.editormenu.toggle();

        this.ui.dragAndDrop(this.editormenu, this.quickbar);

    }

    public void toggleEditorMenu() {
        this.editormenu.toggle();
    }

    public void toggleQuickbar() {
        this.quickbar.toggle();
    }

    /* Aufbau der Benutzeroberfläche für ein neues Level */
    private void setupUiNewWorld() {
        this.ui.addButton(Constants.UI_ICON_BACK, 0, 0, "editor_back");
        this.slider = this.ui.addSlider((int) Constants.UI_MIDDLE_X, 0, "editor_slider_grid_");
        this.ui.addButton(Constants.UI_ICON_MINUS, (int) Constants.UI_MIDDLE_X - (int) Constants.UI_IMAGE_SIZE, 0, "editor_grid_minus");
        this.ui.addButton(Constants.UI_ICON_PLUS, (int) Constants.UI_MIDDLE_X + (int) Constants.UI_MIDDLE_WIDTH, 0, "editor_grid_plus");

        this.backgroundSlider = this.ui.addSlider((int) Constants.UI_MIDDLE_X, 55, "editor_slider_background_");
        this.ui.addButton(Constants.UI_ICON_MINUS, (int) Constants.UI_MIDDLE_X - (int) Constants.UI_IMAGE_SIZE, 55, "editor_background_minus");
        this.ui.addButton(Constants.UI_ICON_PLUS, (int) Constants.UI_MIDDLE_X + (int) Constants.UI_MIDDLE_WIDTH, 55, "editor_background_plus");

        //Save Button in weiß
        this.ui.setScreenSize(20, Color.WHITE);

        this.ui.addTextButton("Save", (float) (int) ((float) Gdx.graphics.getWidth() - Constants.UI_IMAGE_SIZE), 0.0f, "editor_saveScene");
        this.ui.addButton(
            Constants.UI_ICON_BRUSH,
            (int) ((float) Gdx.graphics.getWidth() - Constants.UI_IMAGE_SIZE),
            (int) ((float) Gdx.graphics.getHeight() - Constants.UI_IMAGE_SIZE),
            "editor_grid_paintbrush");
    }

    public void openSettings() {
        this.ui.add(this.menuWindow);
    }

    public void closeSettings() {
        this.ui.remove(this.menuWindow);
    }

    public Entity placeEntity(XmlReader.Element entityXmlNode, Position position) {
        //TODO temporär
        if ( entityXmlNode.getName().equals("Player") ) {
            if ( LevelManager.getInstance().hasPlayer() ) {
                Gdx.app.log("EDITOR", "Es kann nur einen Player geben!");
                return null;
            }
        }

        Entity entity = EntityFactory.createEntity(entityXmlNode, new PositionComponent(position), 0.0f);

        if ( entity != null ) {

            this.engine.addEntity(entity);
            //TODO temporär
            if ( entity.getComponent(InputComponent.class) != null ) {
                LevelManager.getInstance().setPlayerEntity(entity);
            }

        }
        return entity;
    }

    /* ALT */
    public void showNpcListWindow() {
        ArrayList<ListItem> items = new ArrayList<>();
        FileHandle npcRootFolder = Gdx.files.local(Constants.PACKAGE_FOLDER + LevelManager.getInstance().getCurrentPackage() + "/npcs/");
        for ( FileHandle npcFolder : npcRootFolder.list() ) {
            if ( npcFolder.isDirectory() ) {
                FileHandle npc = new FileHandle(npcFolder.path() + "/" + npcFolder.name() + ".xml");
                ListItem listItem = XmlHelper.parseXml(npc.path());
                items.add(listItem);
            } else {
                Gdx.app.log("EDITOR", "NPC Liste: Problem Parsen der NPC-Ordner");
            }
        }

        this.currentEntityList = items;
        this.entityListWindow.setContent(this.currentEntityList);
        this.entityListWindow.show();
    }

    /* ALT */
    public void showListWindow(String listPath) {
        this.currentEntityList = XmlHelper.parseXmlList(listPath);
        this.entityListWindow.setContent(this.currentEntityList);
        this.entityListWindow.show();
    }

    /* NEU */
    public void showItems(String listPath) {
        this.currentEntityList = XmlHelper.parseXmlList(listPath);
        this.editormenu.setContent(this.currentEntityList);
    }

    /* NEU */
    public void showPlayerAndNPCItems() {
        ArrayList<ListItem> items = XmlHelper.parseXmlList(Constants.PLAYER_LIST_PATH);
        FileHandle npcRootFolder = Gdx.files.local(Constants.PACKAGE_FOLDER + LevelManager.getInstance().getCurrentPackage() + "/npcs/");
        for ( FileHandle npcFolder : npcRootFolder.list() ) {
            if ( npcFolder.isDirectory() ) {
                FileHandle npc = new FileHandle(npcFolder.path() + "/" + npcFolder.name() + ".xml");
                ListItem listItem = XmlHelper.parseXml(npc.path());
                items.add(listItem);
            } else {
                Gdx.app.log("EDITOR", "NPC Liste: Problem Parsen der NPC-Ordner");
            }
        }

        this.currentEntityList = items;
        this.editormenu.setContent(this.currentEntityList);
    }

    public void showWindow(Window window) {
        this.hideWindows();
        switch ( window ) {
            case ENTITY_LIST:
                this.entityListWindow.show();
                break;
            default:
                break;
        }
    }

    public void hideWindows() {
        this.entityListWindow.hide();
    }

    public void addQuestToEntity(String questName) {
        Entity entity = this.inputSystem.getCurrentEntity();
        String clientName = entity.getComponent(NameComponent.class).name;
        entity.add(QuestParser.parseQuestComponent(Constants.PACKAGE_FOLDER + this.currentPackage + "/quests/" + questName, clientName));
    }

    public void removeEntity(Entity entity) {
        this.engine.removeEntity(entity);
    }

    public void deleteCurrentEntity() {
        Entity entity = this.inputSystem.getCurrentEntity();
        // TODO: PositionMapper?
        PositionComponent pos = entity.getComponent(PositionComponent.class);
        //LevelManager.getInstance().getLevel().getGameGrid().setType(pos.position, GameGrid.Type.WALKABLE);

        //TODO temporär
        if ( entity.getComponent(InputComponent.class) != null ) {
            LevelManager.getInstance().setPlayerEntity(null);
        }

        if ( entity.getComponent(TypeComponent.class).type == TypeComponent.Type.TILE ) {
            LevelManager.getInstance().getLevel().getGameGrid().clearTile(pos.position, pos.size, entity.getComponent(RotationComponent.class).angle);
        } else {
            LevelManager.getInstance().getLevel().getGameGrid().setToWalkable(pos.position, pos.size, entity.getComponent(RotationComponent.class).angle);
        }

        this.hideContextMenu();
        this.engine.removeEntity(entity);
    }

    public Entity getCurrentEntity() {
        return this.inputSystem.getCurrentEntity();
    }

    public void rotateCurrentEntity() {
        Entity entity = this.inputSystem.getCurrentEntity();

        PositionComponent pos = entity.getComponent(PositionComponent.class);
        RotationComponent rot = entity.getComponent(RotationComponent.class);

        float newAngle = rot.angle + 90.0f;

        if ( newAngle >= 360.0f ) {
            newAngle = 0.0f;
        }

        LevelManager.getInstance().getLevel().getGameGrid().setToWalkable(pos.position, pos.size, rot.angle);
        if ( LevelManager.getInstance().getLevel().getGameGrid().checkWalkablePosition(pos.position, pos.size, newAngle) ) {

            rot.angle += 90.0f;

            if ( rot.angle >= 360.0f ) {
                rot.angle = 0.0f;
            }

            LevelManager.getInstance().getLevel().getGameGrid().setToOccupied(pos.position, pos.size, rot.angle);
            //LevelManager.getInstance().getLevel().updateGameGrid(false);
        } else {
            LevelManager.getInstance().getLevel().getGameGrid().setToOccupied(pos.position, pos.size, rot.angle);
        }

    }

    public void showQuestList() {

        FileHandle dirHandle = Gdx.files.internal(Constants.PACKAGE_FOLDER + this.currentPackage + "/quests/");
        this.entityListWindow.setContent(dirHandle);
        this.entityListWindow.show();

    }

    public void showContextMenu(Entity entity, Position position) {

        TypeComponent t = entity.getComponent(TypeComponent.class);
        this.entityContextMenu.show(t, position, entity);

    }

    public void showSettingsMenu() {
        //editormenu.hide();
        //editorSettingsMenu.show();

        this.editormenu.setMenuAsContent(this.editorSettingsMenu.getSettingsMenuTable());
    }

    public void hideContextMenu() {
        //ui.hideEntityContextMenu();
        this.entityContextMenu.hide();
    }

    public Slider getSlider() {
        return this.slider;
    }

    public Slider getBackgroundSlider() {
        return this.backgroundSlider;
    }

    public void toggleBrushMode() {
        this.inputSystem.toggleBrushMode();
    }

    public boolean getBrushMode() {
        return this.inputSystem.getBrushMode();
    }

    public void showSaveWindow(String leftButton, String rightButton, String leftHandle, String rightHandle) {
        this.showInputWindow(leftButton, rightButton, leftHandle, rightHandle);
    }

    public void saveScene() {
        String background = LevelManager.getInstance().getLevel().getBackgroundPath();
        SceneSaver saver = new SceneSaver(SceneSaver.Type.SAVE_EDITOR, this.getEngine().getEntitiesFor(Family.all(TypeComponent.class).get()));
        background = background.replace(".png", ".xml");
        saver.save(Constants.AVAILABLE_SCENES_PATH + background);
    }

    public void save() {
        String packageName = LevelManager.getInstance().getCurrentPackage();
        String levelName = LevelManager.getInstance().getCurrentLevelFileName();
        String path = Constants.PACKAGE_FOLDER + packageName + "/levels/" + levelName;
        Gdx.app.log("EDITOR", "Saved to " + path);

        SceneSaver saver = new SceneSaver(SceneSaver.Type.SAVE_EDITOR, this.getEngine().getEntitiesFor(Family.all(TypeComponent.class).get()));
        saver.setInput(path);
    }

    public void load(String file) {
        SceneLoader loader = new SceneLoader();

        this.engine.removeAllEntities();
        loader.load(file, this.getEngine());
        this.inputSystem.setObstructedFields(this.engine);
        this.resetCameraPosition();
        this.renderSystem.reset();
        this.loadScheduled = false;
    }

    private void resetCameraPosition() {
        CameraManager.setCameraPosition(new Vector2(0.0f, 0.0f));
        CameraManager.getCamera().zoom = 1.0f;
        CameraManager.getCamera().update();
    }

    public void chooseEntity(String listType, String listIndex) {

        if ( listIndex.equals("null") ) {
            this.inputSystem.setXmlEntity(null);
            Gdx.app.log("EDITOR", "OK schon gut! Kein Entity!");
        } else {
            this.currentEntityList = this.getListItemsFromListType(listType);
            int index = Integer.parseInt(listIndex);
            if ( listType.equals("Npc") ) {
                this.inputSystem.setXmlEntity(this.currentEntityList.get(index - 1).xmlElement);
                Gdx.app.log("EDITOR", this.currentEntityList.get(index - 1).name);
            } else {
                this.inputSystem.setXmlEntity(this.currentEntityList.get(index).xmlElement);
                Gdx.app.log("EDITOR", this.currentEntityList.get(index).name);
            }
        }
    }

    //GEHT SCHÖNER
    private ArrayList<ListItem> getListItemsFromListType(String listType) {
        if ( listType.equals("Player") ) {
            return XmlHelper.parseXmlList(Constants.PLAYER_LIST_PATH);
        } else if ( listType.equals("Npc") ) {
            ArrayList<ListItem> items = new ArrayList<ListItem>();
            FileHandle npcRootFolder = Gdx.files.local(Constants.PACKAGE_FOLDER + LevelManager.getInstance().getCurrentPackage() + "/npcs/");
            for ( FileHandle npcFolder : npcRootFolder.list() ) {
                if ( npcFolder.isDirectory() ) {
                    FileHandle npc = new FileHandle(npcFolder.path() + "/" + npcFolder.name() + ".xml");
                    ListItem listItem = XmlHelper.parseXml(npc.path());
                    items.add(listItem);
                } else {
                    Gdx.app.log("EDITOR", "NPC Liste: Problem Parsen der NPC-Ordner");
                }
            }
            return items;
        } else if ( listType.equals("Mob") ) {
            return XmlHelper.parseXmlList(Constants.MOB_LIST_PATH);
        } else if ( listType.equals("Item") ) {
            return XmlHelper.parseXmlList(Constants.ITEM_LIST_PATH);
        } else if ( listType.equals("Consumable") ) {
            return XmlHelper.parseXmlList(Constants.CONSUMABLE_LIST_PATH);
        } else if ( listType.equals("Equippable") ) {
            return XmlHelper.parseXmlList(Constants.EQUIPPABLE_LIST_PATH);
        } else if ( listType.equals("Object") ) {
            return XmlHelper.parseXmlList(Constants.OBJECT_LIST_PATH);
        } else if ( listType.equals("Chest") ) {
            return XmlHelper.parseXmlList(Constants.CHEST_LIST_PATH);
        } else if ( listType.equals("Empty") ) {
            return XmlHelper.parseXmlList(Constants.EMPTY_LIST_PATH);
        } else if ( listType.equals("Tile") ) {
            return XmlHelper.parseXmlList(Constants.OBJECT_LIST_PATH);
        } else {
            Gdx.app.log("EDITOR", "Fehler: Unbekannter Handle-List-Typ!");
        }
        return null;
    }

    /* Ein Eingabefenster erscheint */
    private void showInputWindow(String leftButton, String rightButton, String leftHandle, String rightHandle) {
        this.hideWindow();

        this.input = new TextField("", this.ui.getSkin());
        this.windowTable.add(this.input).expandX().prefWidth(Constants.WIDTH / 3.0f);

        this.window = this.ui.addTwoButtonWindow(this.windowTable, leftButton, rightButton, leftHandle, rightHandle);
        this.window.setBackground(this.windowBackground);
        this.window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.window.setPosition(Constants.WIDTH / 2.0f - WINDOW_WIDTH / 2.0f, Constants.HEIGHT / 2.0f - WINDOW_HEIGHT / 2.0f);
    }

    public void showMenuWindow() {
        this.hideWindow();
        this.editormenu.hide();

        this.window = new LandsOfCinderWindow(this.ui.getSkin());

        Label labelMenu = new Label("MENÜ" + "\n", this.ui.getSkin());

        Table tableFortsetzen = new Table();
        tableFortsetzen.setBackground(this.inactive);
        tableFortsetzen.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EditorScreen.this.hideWindow();
                EditorScreen.this.toggleEditorMenu();
            }
        });
        Label labelFortsetzen = this.ui.addLabel("Fortsetzen");
        labelFortsetzen.setAlignment(Align.center);
        tableFortsetzen.add(labelFortsetzen).expand().fill();

        Table tableEinstellungen = new Table();
        tableEinstellungen.setBackground(this.inactive);
        tableEinstellungen.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("EDITOR", "In Arbeit!");
            }
        });
        Label labelEinstellungen = this.ui.addLabel("Einstellungen");
        labelEinstellungen.setAlignment(Align.center);
        tableEinstellungen.add(labelEinstellungen).expand().fill();

        Table tableBeenden = new Table();
        tableBeenden.setBackground(this.inactive);
        tableBeenden.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EditorScreen.this.save();
                EditorScreen.this.dispose();
                EditorScreen.this.getGame().setScreen(new EditorMenuScreen(EditorScreen.this.getGame()));
            }
        });
        Label labelBeenden = this.ui.addLabel("Beenden");
        labelBeenden.setAlignment(Align.center);
        tableBeenden.add(labelBeenden).expand().fill();

        this.window.add(labelMenu).row();
        this.window.add(tableFortsetzen).expandX().prefWidth(Constants.WIDTH / 5.0f).row();
        this.window.add(tableEinstellungen).expandX().prefWidth(Constants.WIDTH / 5.0f).row();
        this.window.add(tableBeenden).expandX().prefWidth(Constants.WIDTH / 5.0f).row();
        this.ui.add(this.window);

        this.window.setBackground(new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_MOUSEOVER)));
        this.window.setSize(Constants.WIDTH / 5.0f, Constants.HEIGHT * 0.5f);
        this.window.setPosition(Constants.WIDTH / 2.0f - this.window.getWidth() / 2.0f, Constants.HEIGHT / 2.0f - this.window.getHeight() / 2.0f);
    }

    /* Lässt das aktive Fenster verschwinden */
    public void hideWindow() {
        if ( this.window != null ) {
            this.window.remove();
            this.windowTable.clearChildren();
        }
    }

    @Override
    public void dispose() {
        super.dispose();

        if ( this.heightTextureParser != null ) {
            this.heightTextureParser.dispose();
        }
    }

    /* Verschiedene Varianten des Editors */
    public enum Type {
        NEW_WORLD,
        EDIT_WORLD
    }

    public enum Window {
        ENTITY_LIST
    }
}
