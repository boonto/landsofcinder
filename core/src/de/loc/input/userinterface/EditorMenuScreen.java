package de.loc.input.userinterface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

import de.loc.core.LevelManager;
import de.loc.editor.EditorChooseWorld;
import de.loc.editor.EditorScreen;
import de.loc.game.GameScreen;
import de.loc.main.LandsOfCinder;
import de.loc.online.OnlineHelper;
import de.loc.online.OnlineStatus;
import de.loc.tools.Constants;
import de.loc.tools.Pair;

public class EditorMenuScreen extends MenuScreen {
    private LevelManager manager;

    private Table mainTable;
    private Table menuTable;
    private Table menuLeftTable;
    private Table menuRightTable;
    private Table menuRightInnerOfflineTable;
    private Table menuRightInnerOnlineTable;
    private Table activePackageTable;
    private Table activeLevelTable;
    private Table windowTable;
    private Table containerFlagsMenu;
    private Table flagContainerTable;
    private Table activeFlag;
    private Table loginTable;
    private static TextField loginName;
    private static TextField loginPassword;
    private ScrollPane scrollPane;

    private NinePatchDrawable flagActive;
    private NinePatchDrawable flagInactive;
    private boolean firstFlag;
    private boolean hideRightTable;
    private boolean uploadedReiter;
    private int counter;

    private List<Pair<NinePatchDrawable, String>> iconList;

    private LandsOfCinderWindow window;
    private TextField input;

    private boolean overwrite;
    private XmlReader.Element xmlScene;
    private Map<String, List<String>> allPackages;
    private Map<String, List<String>> allOnlinePackages;

    private Table temporaryTable;

    private NinePatchDrawable active;
    private NinePatchDrawable inactive;

    /* Abstand zwischen den einzelnen UI-Elementen */
    private static final float TABLE_OFFSET = 20.0f;

    /* Breite der rechten Seite des Menüs */
    private static final float RIGHT_SIDE_WIDTH = Constants.WIDTH * 0.75f;

    /* UI Schriftgröße und Schriftfarbe*/
    private static final int UI_TEXTSIZE = 18;
    private static final Color UI_TEXTCOLOR = Color.BLACK;

    /* Window Breite und Höhe */
    private static final float WINDOW_WIDTH = Constants.WIDTH / 2.0f;
    private static final float WINDOW_HEIGHT = Constants.HEIGHT / 5.0f;

    /* Startlevel Selectbox Header */
    private static final String STARTLEVEL_HEADER = "Wähle ein Level";

    /* Online */
    private final OnlineStatus onlineStatus;
    private boolean offlinePackages;
    private boolean firstTime = true;
    private String prevAction = "";

    /* Konstruktor */
    public EditorMenuScreen(LandsOfCinder game) {
        super(game);
        this.setupMenu();

        this.onlineStatus = new OnlineStatus();
    }

    /* Aufbau des Package-Menüs */
    private void setupMenu() {
        this.init();
        this.setupUserInterface();
    }

    /* Initialisierung von wichtigen Daten am Anfang */
    private void init() {
        /* LevelManager */
        this.manager = LevelManager.getInstance();

        /* Alle Packages */
        this.allPackages = this.parsePackages(Constants.PACKAGE_FOLDER);


        /* ActivePackageTable */
        this.activePackageTable = new Table();
        this.activePackageTable.setName("");

        /* ActiveLevelTable */
        this.activeLevelTable = new Table();
        this.activeLevelTable.setName("");

        /* Sonstiges */
        this.active = new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_PAPER_DARK));
        this.inactive = new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_PAPER_BRIGHT));

        FileHandle cookieFile = Gdx.files.local(Constants.COOKIE_PATH);
        this.hideRightTable = !cookieFile.exists();
    }

    /* Parst alle Package-Verzeichnisse und liefert eine HashMap mit allen Packages zurück */
    private Map<String, List<String>> parsePackages(String path) {
        this.offlinePackages = !path.equals(Constants.ONLINE_FOLDER);
        Map<String, List<String>> allPackages = new HashMap<>();
        FileHandle packageRootFolder = Gdx.files.local(path);
        for ( FileHandle packageFolder : packageRootFolder.list() ) {
            if ( packageFolder.isDirectory() ) {
                String packageName = this.convertForUser(packageFolder.nameWithoutExtension());
                List<String> allLevels = this.parseLevels(packageName);
                allPackages.put(packageName, allLevels);
            } else {
                Gdx.app.log("MENU", "Problem beim Parsen der Package-Ordner.");
            }
        }
        return allPackages;
    }

    /* Parst das Package-Verzeichnis und liefert eine ArrayList mit allen Levels zurück */
    private List<String> parseLevels(String packageName) {
        String path;
        if ( this.offlinePackages ) {
            path = Constants.PACKAGE_FOLDER;
        } else {
            path = Constants.ONLINE_FOLDER;
        }
        FileHandle packageFolder = Gdx.files.local(path + "/" + this.convertForAssets(packageName) + "/levels/");
        List<String> allLevels = new ArrayList<>();
        for ( FileHandle level : packageFolder.list() ) {
            allLevels.add(this.convertForUser(level.nameWithoutExtension()));
        }
        return allLevels;
    }

    /* Aufbau der Benutzeroberfläche */
    private void setupUserInterface() {
        /* Schriftgröße und Schriftfarbe */
        this.ui.setScreenSize(UI_TEXTSIZE, UI_TEXTCOLOR);

        this.setupWindowTable();

        this.setupMainTable();
        this.setupMenuTable();
        this.setupMenuLeftTable();
        this.setupMenuRightTable();
        this.setupContent(this.allPackages);

    }

    /* Aufbau eines WindowTables */
    private void setupWindowTable() {
        /* windowTable */
        this.windowTable = new Table();
        this.windowTable.setName("windowTable");
        this.windowTable.pad(TABLE_OFFSET);
    }

    /* Aufbau des MainTables */
    private void setupMainTable() {
        /* MainTable */
        this.mainTable = this.ui.getMainTable();
        this.mainTable.setName("mainTable");
        this.mainTable.setFillParent(true);

        /* Back Button */
        Button buttonBack = this.ui.addButton(Constants.UI_ICON_BACK, 0, 0, "back_to_main_menu");

        /* Hinzufügen */
        this.mainTable.add(buttonBack).pad(TABLE_OFFSET / 2.0f);
    }

    /* Aufbau des MenuTables */
    private void setupMenuTable() {
        /* MenuTable */
        this.menuTable = new Table();
        this.menuTable.setName("menuTable");
        this.menuTable.pad(TABLE_OFFSET);

        /* Hinzufügen */
        this.mainTable.add(this.menuTable).row();
    }

    /* Aufbau der linken Hälfte des Package-Menüs */
    private void setupMenuLeftTable() {
        /* menuLeftTable */
        this.menuLeftTable = new Table();
        this.menuLeftTable.setName("menuLeftTable");
        this.menuLeftTable.pad(TABLE_OFFSET);
        this.menuLeftTable.setBackground(this.inactive);

        /* Button zum Spielen eines Packages oder Levels */
        TextButton playButton = this.ui.addTextButton("Testen", 0.0f, 0.0f, "editor_menu_test");
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean availableStartLevel = false;
                if ( EditorMenuScreen.this.packageActivated() ) {
                    EditorMenuScreen.this.updateOverview(EditorMenuScreen.this.activePackageTable);

                    if ( EditorMenuScreen.this.levelActivated() ) {
                        LevelManager.getInstance()
                                    .setCurrentPackage(EditorMenuScreen.this.convertForAssets(EditorMenuScreen.this.getPackageName(EditorMenuScreen.this.activePackageTable)));
                        LevelManager.getInstance()
                                    .setCurrentLevel(EditorMenuScreen.this.convertForAssets(EditorMenuScreen.this.getLevelName(EditorMenuScreen.this.activeLevelTable)
                                                                                            + ".xml"));
                        availableStartLevel = true;
                    } else {
                        LevelManager.getInstance()
                                    .setCurrentPackage(EditorMenuScreen.this.convertForAssets(EditorMenuScreen.this.getPackageName(EditorMenuScreen.this.activePackageTable)));
                        String startLevel = EditorMenuScreen.this.parseStartLevel(Constants.PACKAGE_FOLDER + EditorMenuScreen.this.manager.getCurrentPackage());
                        if ( !startLevel.equals(STARTLEVEL_HEADER + ".xml") ) {
                            EditorMenuScreen.this.manager.setCurrentLevel(startLevel);
                            availableStartLevel = true;
                        }
                    }
                    if ( availableStartLevel ) {
                        EditorMenuScreen.this.dispose();
                        GameScreen gameScreen = new GameScreen(EditorMenuScreen.this.getGame(), GameScreen.Mode.TEST);
                        EditorMenuScreen.this.game.setScreen(gameScreen);

                    } else {
                        Gdx.app.log("MENU", "Es ist kein StartLevel vorhanden!");
                    }
                }
            }
        });

        /* Button zur Umbenennung eines Packages oder Levels */
        TextButton renameButton = this.ui.addTextButton("Umbenennen", 0.0f, 0.0f, "editor_menu_rename");
        renameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EditorMenuScreen.this.showInputWindow("Zurück", "Umbenennen", "editor_menu_window_close", "editor_menu_window_rename");
            }
        });

        /* Button zur Löschung eines Packages oder Levels */
        TextButton deleteButton = this.ui.addTextButton("Löschen", 0.0f, 0.0f, "editor_menu_delete");
        deleteButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if ( EditorMenuScreen.this.levelActivated() ) {
                    EditorMenuScreen.this.deleteLevel(EditorMenuScreen.this.getLevelName(EditorMenuScreen.this.activeLevelTable));
                } else {
                    EditorMenuScreen.this.deletePackage(
                        EditorMenuScreen.this.getPackageName(EditorMenuScreen.this.activePackageTable),
                        Constants.PACKAGE_FOLDER);
                }
            }
        });

        /* Button zur Erstellung eines neuen Packages */
        TextButton createPackageButton = this.ui.addTextButton("Neues Paket", 0.0f, 0.0f, "editor_menu_createpackage");
        createPackageButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EditorMenuScreen.this.activeLevelTable.setBackground(EditorMenuScreen.this.inactive);
                EditorMenuScreen.this.activeLevelTable = new Table();
                EditorMenuScreen.this.activeLevelTable.setName("");
                EditorMenuScreen.this.showInputWindow("Zurück", "Erstelle Paket", "editor_menu_window_close", "editor_menu_window_createpackage");
            }
        });

        /* Button zur Erstellung eines neuen Levels */
        TextButton createLevelButton = this.ui.addTextButton("Neues Level", 0.0f, 0.0f, "editor_menu_createlevel");
        createLevelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EditorMenuScreen.this.activeLevelTable.setBackground(EditorMenuScreen.this.inactive);
                EditorMenuScreen.this.activeLevelTable = new Table();
                EditorMenuScreen.this.activeLevelTable.setName("Neues Level");
                EditorMenuScreen.this.showInputWindow("Zurück", "Erstelle Level", "editor_menu_window_close", "editor_menu_window_createlevel");
            }
        });

        /* Button zur Erstellung eines neuen Levels */
        TextButton createSceneButton = this.ui.addTextButton("Neue Scene", 0.0f, 0.0f, "editor_menu_createScene");
        createSceneButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EditorMenuScreen.this.activeLevelTable.setBackground(EditorMenuScreen.this.inactive);
                EditorMenuScreen.this.activeLevelTable = new Table();
                EditorMenuScreen.this.activeLevelTable.setName("");
                EditorChooseWorld newScene = new EditorChooseWorld(EditorMenuScreen.this.game, EditorScreen.Type.NEW_WORLD);
                EditorMenuScreen.this.game.setScreen(newScene);
            }
        });

        /* Button zum Uploaden eines des Packages */
        TextButton uploadPackageButton = this.ui.addTextButton("Hochladen", 0.0f, 0.0f, "editor_menu_online_upload");
        uploadPackageButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //TODO: Upload hier einfügen...
            }
        });

        /* Hinzufügen */
        this.menuLeftTable.add(playButton).pad(TABLE_OFFSET / 2.0f).expandX().prefWidth(Constants.WIDTH).row();
        this.menuLeftTable.add(renameButton).pad(TABLE_OFFSET / 2.0f).expandX().prefWidth(Constants.WIDTH).row();
        this.menuLeftTable.add(deleteButton).pad(TABLE_OFFSET / 2.0f).expandX().prefWidth(Constants.WIDTH).row();
        this.menuLeftTable.add(createPackageButton).pad(TABLE_OFFSET / 2.0f).padTop(TABLE_OFFSET).expandX().prefWidth(Constants.WIDTH).row();
        this.menuLeftTable.add(createLevelButton).pad(TABLE_OFFSET / 2.0f).expandX().prefWidth(Constants.WIDTH).row();
        this.menuLeftTable.add(createSceneButton).pad(TABLE_OFFSET / 2.0f).expandX().prefWidth(Constants.WIDTH).row();
        this.menuLeftTable.add(uploadPackageButton).pad(TABLE_OFFSET / 2.0f).padTop(TABLE_OFFSET).expandX().prefWidth(Constants.WIDTH).row();
        this.menuTable.add(this.menuLeftTable).expandX().prefWidth(Constants.WIDTH).expandY().center();
    }

    /* Aufbau der rechten Hälfte des Package-Menüs */
    private void setupMenuRightTable() {
        this.flagContainerTable = new Table();
        this.flagContainerTable.setName("flagContainerTable");

        this.containerFlagsMenu = new Table();
        this.containerFlagsMenu.setName("menuRightTable");
        this.containerFlagsMenu.pad(TABLE_OFFSET);

        this.menuTable.add(this.containerFlagsMenu).expand().prefWidth(Constants.WIDTH).prefHeight(Constants.HEIGHT * 0.9f).top().padTop(50.0f);

        /* menuRightTable */
        this.menuRightTable = new Table();
        this.menuRightTable.setName("menuRightTable");
        this.menuRightTable.pad(TABLE_OFFSET);

        /* menuRightInnerOfflineTable */
        this.menuRightInnerOfflineTable = new Table();
        this.menuRightInnerOfflineTable.setName("menuRightInnerOfflineTable");

        this.menuRightInnerOnlineTable = new Table();
        this.menuRightInnerOnlineTable.setName("menuRightInnerOnlineTable");


        /* Hinzufügen */
        this.menuRightTable.add(this.menuRightInnerOfflineTable);
        this.menuRightTable.add(this.menuRightInnerOnlineTable).top();
        this.containerFlagsMenu.add(this.flagContainerTable).left().padLeft(TABLE_OFFSET).top().row();
        this.containerFlagsMenu.add(this.menuRightTable).row();


        /* Die ScrollPane muss am Ende hinzugefügt werden */
        this.setupScrollPane();
        this.addReiter();
    }

    private void addReiter() {

        /* Aktiver Reiter */
        this.activeFlag = new Table();
        this.activeFlag.setName("");

        this.firstFlag = true;

        this.flagActive = new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_FLAG_ACTIVE));
        this.flagInactive = new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_FLAG_INACTIVE));

        this.iconList = new ArrayList<>(4);
        // TODO Patrick 2020: Onlinefunktionalität deaktiviert, funktioniert aktuell einfach nicht
//        this.iconList.add(new Pair<>(new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_ICON_OBJECTS)), "editor_menu_offline_level"));
//        this.iconList.add(new Pair<>(new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_ICON_ONLINE)), "editor_menu_online_level"));
//        this.iconList.add(new Pair<>(new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_ICON_DOWNLOAD)), "editor_menu_download_level"));
//        this.iconList.add(new Pair<>(new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_ICON_UPLOAD)), "editor_menu_uploaded_level"));

        for ( Pair<NinePatchDrawable, String> anIconList : this.iconList ) {
            NinePatchDrawable drawable = anIconList.getLeft();
            String handleID = anIconList.getRight();
            this.addFlag(drawable, handleID);
        }
    }

    private void addFlag(Drawable drawable, String handleID) {
        /* Reiter-Table */
        Table flagTable = new Table();
        flagTable.setName("flagTable");

        if ( this.firstFlag ) {
            flagTable.setBackground(this.flagActive);
            this.activeFlag = flagTable;
            this.firstFlag = false;
        } else {
            flagTable.setBackground(this.flagInactive);
        }

        /* Reiter-Bild */
        Image flagImage = new Image();
        flagImage.setName(handleID);
        flagImage.setDrawable(drawable);
        this.addFlagListener(flagImage, flagTable);

        /* Hinzufügen */
        flagTable.add(flagImage).size(50.0f, 50.0f);
        this.flagContainerTable.add(flagTable).padRight(10.0f);
    }

    private void addFlagListener(final Actor actor, final Table table) {
        if ( actor.getName() != null ) {
            actor.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    EditorMenuScreen.this.activateFlag(table, actor.getName());
                    //entryContainerTable.clearChildren();
                    EditorMenuScreen.this.ui.getInputHandler().handle(actor.getName());
                }
            });
        } else {

            Gdx.app.log("MENU", "Fehler: Es wurde kein Clicklistener hinzugefügt!");
        }
    }

    private void activateFlag(Table flagTable, String name) {
        this.activeFlag.setBackground(this.flagInactive);
        this.activeFlag = flagTable;
        this.activeFlag.setBackground(this.flagActive);
        if ( name.equals("editor_menu_offline_level") ) {
            this.showMenuLeftButton();
            this.menuRightTable.setVisible(true);
            this.menuRightTable.clear();
            this.menuRightTable.add(this.menuRightInnerOfflineTable);
            this.menuRightInnerOfflineTable.setVisible(true);
            this.hideLogin();
            this.menuLeftTable.setVisible(true);
            this.clearFlagContainer();
        } else if ( name.equals("editor_menu_online_level") ) {
            this.showMenuLeftButton();
            //menuRightTable.clear();
            this.menuRightInnerOfflineTable.setVisible(false);
            this.checkForVisibility();
            this.hideLogin();
            //menuLeftTable.setVisible(false);
            this.clearFlagContainer();
            this.hideMenuLeftButton();
        } else if ( name.equals("editor_menu_download_level") ) {
            this.showMenuLeftButton();
            //menuRightTable.setVisible(false);
            //int a = flagContainerTable.getChildren().size;
            //System.out.println("Children:  " + a);
            this.menuRightTable.setVisible(true);
            this.menuRightTable.clear();
            this.allOnlinePackages = this.parsePackages(Constants.ONLINE_FOLDER);
            this.setupContentDownloaded(this.allOnlinePackages);
            this.hideLogin();
            this.hideMenuLeftButton();
            this.showOneMenuLeftButton(0);
            this.clearFlagContainer();
            if ( this.allOnlinePackages.size() <= 1 ) {
                this.createTemporaryTable(this.allOnlinePackages.size(), this.menuRightTable);
            }
        } else if ( name.equals("editor_menu_uploaded_level") ) {
            //clearFlagContainer();
            this.showMenuLeftButton();
            //menuRightTable.clear();
            this.checkForVisibility();
            //setRightTable();
            this.hideLogin();
            this.uploadedReiter = true;
            this.hideMenuLeftButton();
            this.hideMenuLeftButton();
            if ( this.counter <= 1 ) {
                this.createTemporaryTable(this.counter, this.flagContainerTable);
            }
        } else {
            Gdx.app.log("MENU", "Kein Reiter verfügbar!!!");
        }
    }

    private void clearFlagContainer() {
        if ( this.flagContainerTable.getChildren().size == 5 ) {
            //flagContainerTable.removeActor(temporaryTable);
            this.flagContainerTable.clear();
            this.addReiter();
        }
    }

    private void hideMenuLeftButton() {
        for ( int i = 0; i <= 6; i++ ) {
            Actor a = this.menuLeftTable.getChildren().get(i);
            a.setVisible(false);
        }
    }

    private void showOneMenuLeftButton(int button) {
        Actor a = this.menuLeftTable.getChildren().get(button);
        a.setVisible(true);
    }

    private void showMenuLeftButton() {
        for ( int i = 0; i <= 6; i++ ) {
            Actor a = this.menuLeftTable.getChildren().get(i);
            a.setVisible(true);
        }
    }

    private void createTemporaryTable(int size, Table table) {
        this.temporaryTable = new Table();
        //table.setDebug(true);

        Table packageContentTable = new Table();
        packageContentTable.setName("packageContentTable");

        //disableScrollPane();
        this.temporaryTable.add(packageContentTable).expandX();
        float height = 0.0f;
        if ( size == 0 ) {
            height = Constants.HEIGHT * 0.7f;
        } else if ( size == 1 ) {
            height = Constants.HEIGHT * 0.35f;
        } else {
            height = Constants.HEIGHT * 0.7f;
        }
        table.add(this.temporaryTable).height(height);
    }

    private void checkForVisibility() {
        if ( this.hideRightTable ) {
            this.menuRightTable.setVisible(false);
        } else {
            this.menuRightTable.clear();
        }
    }

    private void setUpLoginScreen(String name) {
        this.loginTable = new Table();
        this.loginTable.setBackground(new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_PAPER_BRIGHT)));
        this.loginTable.setHeight(Constants.HEIGHT / 2.0f);
        this.loginTable.setWidth(Constants.WIDTH / 2.5f);
        this.loginTable.setX(this.containerFlagsMenu.getWidth() / 2.0f - this.loginTable.getWidth() / 2.0f);
        this.loginTable.setY(this.containerFlagsMenu.getHeight() / 2.0f - this.loginTable.getHeight() / 2.0f);

        float widthTable = this.loginTable.getWidth();
        float heightTable = this.loginTable.getHeight();

        //Textfeld für den Namen
        Button nameTextfield = this.ui.addSkillButtonWithoutListener(Constants.UI_NINEPATCHES_PATH + "textfeld2.png",
                                                                     this.loginTable.getWidth() / 2.0f - (this.loginTable.getWidth() / 1.5f) / 2.0f,
                                                                     this.loginTable.getHeight() - this.loginTable.getHeight() / 3.0f,
                                                                     this.loginTable.getWidth() / 1.5f,
                                                                     this.loginTable.getHeight() / 6.0f);
        this.loginTable.addActor(nameTextfield);
        loginName = new TextField("Name", this.ui.getSkin());
        loginName.setY(nameTextfield.getHeight() / 2.0f - loginName.getHeight() / 2.0f);
        nameTextfield.addActor(loginName);

        //Textfeld für das Passwort
        Button passwordTextfield = this.ui.addSkillButtonWithoutListener(Constants.UI_NINEPATCHES_PATH + "textfeld2.png",
                                                                         this.loginTable.getWidth() / 2.0f - (this.loginTable.getWidth() / 1.5f) / 2.0f,
                                                                         this.loginTable.getHeight() - this.loginTable.getHeight() / 1.8f,
                                                                         this.loginTable.getWidth() / 1.5f,
                                                                         this.loginTable.getHeight() / 6.0f);
        this.loginTable.addActor(passwordTextfield);
        loginPassword = new TextField("Passwort", this.ui.getSkin());
        loginPassword.setY(passwordTextfield.getHeight() / 2.0f - loginPassword.getHeight() / 2.0f);
        loginPassword.setPasswordMode(true);
        loginPassword.setPasswordCharacter('*');
        passwordTextfield.addActor(loginPassword);

        String ueberschrift;
        if ( name.equals("login") ) {
            Button login = this.ui.addTextButtonOtherSize("Login",
                                                          (this.loginTable.getWidth() / 3.0f),
                                                          this.loginTable.getHeight() / 4.0f,
                                                          this.loginTable.getWidth() / 3.0f,
                                                          this.loginTable.getHeight() / 7.0f,
                                                          "editor_menu_online_login");
            this.loginTable.addActor(login);

            Button buttonRegister = this.ui.addTextButtonOtherSize("Registrieren",
                                                             (this.loginTable.getWidth() / 3.0f) + (this.loginTable.getWidth() / 3.0f) / 2.0f,
                                                             this.loginTable.getHeight() / 0.0f,
                                                             this.loginTable.getWidth() / 3.0f,
                                                             this.loginTable.getHeight() / 7.0f,
                                                             "editor_menu_online_switch_register");
            this.loginTable.addActor(buttonRegister);

            Label register = new Label("Noch nicht angemeldet?", this.ui.getSkin());
            register.setX(register.getWidth() / 3.0f);
            register.setY(this.loginTable.getWidth() / 10.0f);
            this.loginTable.addActor(register);
            ueberschrift = "Login";
        } else {
            Button buttonRegister = this.ui.addTextButtonOtherSize("Registrieren",
                                                             (this.loginTable.getWidth() / 3.0f),
                                                             this.loginTable.getHeight() / 4.0f,
                                                             this.loginTable.getWidth() / 3.0f,
                                                             this.loginTable.getHeight() / 7.0f,
                                                             "editor_menu_online_register");
            this.loginTable.addActor(buttonRegister);

            Button buttonLogin = this.ui.addTextButtonOtherSize("Login",
                                                          (this.loginTable.getWidth() / 3.0f) + (this.loginTable.getWidth() / 3.0f) / 2.0f,
                                                          this.loginTable.getHeight() / 11.0f,
                                                          this.loginTable.getWidth() / 3.0f,
                                                          this.loginTable.getHeight() / 7.0f,
                                                          "editor_menu_online_switch_login");
            this.loginTable.addActor(buttonLogin);

            Label register = new Label("Schon registriert?", this.ui.getSkin());
            register.setX(register.getWidth() / 3.0f);
            register.setY(this.loginTable.getWidth() / 10.0f);
            this.loginTable.addActor(register);
            ueberschrift = "Registrieren";
        }

        //Überschrift
        Label label = new Label(ueberschrift, this.ui.getSkin());
        label.setX(widthTable / 2.0f - widthTable / 12.0f);
        label.setY(heightTable - label.getHeight() * 2.0f);
        this.loginTable.addActor(label);

        this.containerFlagsMenu.addActor(this.loginTable);
        this.loginTable.setVisible(false);
    }

    public static String getLoginUsername() {
        return loginName.getText();
    }

    public static String getLoginPassword() {
        return loginPassword.getText();
    }

    private void setUpPackagesScreen(String title, String author, String id, String kategorie) {

        /* packageTable */
        final Table onlinePackageTable = new Table();
        onlinePackageTable.setName(id);
        onlinePackageTable.pad(TABLE_OFFSET);
        onlinePackageTable.setBackground(this.inactive);
        onlinePackageTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EditorMenuScreen.this.activatePackage(onlinePackageTable);
            }
        });
        this.activatePackage(onlinePackageTable);

        /* Package-Header Table */
        Table packageHeaderTable = new Table();
        packageHeaderTable.setName("packageHeaderTable");
        packageHeaderTable.pad(TABLE_OFFSET);
        packageHeaderTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EditorMenuScreen.this.activeLevelTable.setBackground(EditorMenuScreen.this.inactive);
                EditorMenuScreen.this.activeLevelTable = new Table();
                EditorMenuScreen.this.activeLevelTable.setName("");
            }
        });


        /* Package-Name Label*/
        Label labelPackageName = this.ui.addLabel(title);
        labelPackageName.setName("label_packageName");

        /* StartLevel Label */
        Label labelAuthor = this.ui.addLabel(author);
        labelAuthor.setName("label_" + author);

        /* Bewertung */
        Label labelBewertung = this.ui.addLabel("8.0 von 10.0");
        labelBewertung.setName("label_bewertung");

        /* packageContentTable */
        Table packageContentTable = new Table();
        packageContentTable.setName("packageContentTable");
        packageContentTable.pad(TABLE_OFFSET);


        /* Download Button */
        Button buttonDownload = this.ui.addButton("ui/icons/download.png", 0, 0, "editor_menu_online_download_" + id);

        /* Löschen Button */
        String inputString = "";
        if ( this.uploadedReiter ) {
            inputString = "editor_menu_uploaded_delete_";
        } else {
            inputString = "editor_menu_downloaded_delete_";
        }
        Button buttonDelete = this.ui.addButton("ui/icons/delete.png", 0, 0, inputString + id);



        /* Hinzufügen */
        if ( kategorie.equals("online") ) {
            packageHeaderTable.add(labelPackageName).expand().prefWidth(Constants.WIDTH).prefHeight(Constants.HEIGHT / 10.0f).left().fill();
            packageHeaderTable.add(labelBewertung).padRight(TABLE_OFFSET / 2.0f).right().row();
            packageHeaderTable.add(labelAuthor).padRight(TABLE_OFFSET / 2.0f).left();
            packageHeaderTable.add(buttonDownload).padTop(TABLE_OFFSET).right();
            buttonDelete.setVisible(false);
        } else if ( kategorie.equals("download") ) {
            packageHeaderTable.add(labelPackageName).expand().prefWidth(Constants.WIDTH).prefHeight(Constants.HEIGHT / 10.0f).left();
            packageHeaderTable.add(labelBewertung).padRight(TABLE_OFFSET / 2.0f).right().row();
            packageHeaderTable.add(labelAuthor).padRight(TABLE_OFFSET / 2.0f).left();
            //packageHeaderTable.add(button_download).padTop(TABLE_OFFSET).right();
            packageHeaderTable.add(buttonDelete).padTop(TABLE_OFFSET / 2.0f).right();
            buttonDownload.setVisible(false);
        } else if ( kategorie.equals("upload") ) {
            packageHeaderTable.add(labelPackageName).expand().prefWidth(Constants.WIDTH).prefHeight(Constants.HEIGHT / 10.0f).left();
            packageHeaderTable.add(labelBewertung).padRight(TABLE_OFFSET / 2.0f).right().row();
            packageHeaderTable.add(labelAuthor).padRight(TABLE_OFFSET / 2.0f).left();
            //packageHeaderTable.add(button_download).padTop(TABLE_OFFSET).right();
            packageHeaderTable.add(buttonDelete).padTop(TABLE_OFFSET / 2.0f).right();
            buttonDownload.setVisible(false);
        } else {
            Gdx.app.log("MENU", "Keine Kategorie ausgewählt");
        }

        onlinePackageTable.add(packageHeaderTable).row();
        onlinePackageTable.add(packageContentTable).expandX().prefWidth(Constants.WIDTH);
        this.menuRightTable.add(onlinePackageTable).row();
        //menuRightInnerOnlineTable.setDebug(true);
    }

    private void showList(JsonValue packageList, String content) {
        this.menuRightTable.clear();
        this.counter = 0;
        for ( JsonValue entry = packageList; entry != null; entry = entry.next ) {
            this.setUpPackagesScreen(entry.getString("title"), entry.getString("author"), entry.getString("_id"), content);
            this.counter += 1;
        }
    }

    private boolean isLoggedIn() {
        FileHandle cookieFile = Gdx.files.local(Constants.COOKIE_PATH);
        FileHandle userIdFile = Gdx.files.local(Constants.USER_ID_PATH);
        if ( cookieFile.exists() && userIdFile.exists() ) {
            this.onlineStatus.cookie = cookieFile.readString();
            this.onlineStatus.userId = userIdFile.readString();
            return OnlineHelper.waitForHttpGetPackages(this.onlineStatus);
        } else {
            return false;
        }
    }

    public void logIn() {
        this.hideRightTable = false;
        this.menuRightTable.setVisible(true);

        if ( OnlineHelper.waitForHttpPostLogin(this.onlineStatus) ) {
            FileHandle cookieFile = Gdx.files.local(Constants.COOKIE_PATH);
            FileHandle userIdFile = Gdx.files.local(Constants.USER_ID_PATH);
            cookieFile.writeString(this.onlineStatus.cookie, false);
            userIdFile.writeString(this.onlineStatus.userId, false);

            if ( this.prevAction.equals("upload") ) {//TODO raus damit
                this.uploadPackage();
            } else if ( this.prevAction.equals("levels") ) {
                this.showOnlinePackages();
            } else if ( this.prevAction.equals("uploaded") ) {
                this.showUploadedPackages();
            }
        }
        this.hideLogin();
    }

    public void switchToRegister() {
        this.setUpLoginScreen("register");
        this.loginTable.setVisible(true);
    }

    public void switchToLogin() {
        this.setUpLoginScreen("login");
        this.loginTable.setVisible(true);
    }

    public void uploadPackage() {
        if ( this.isLoggedIn() ) {
            this.onlineStatus.packageFolder = this.convertForAssets(this.getPackageName(this.activePackageTable));
            this.onlineStatus.packageTitle = this.getPackageName(this.activePackageTable);

            OnlineHelper.waitForHttpPostPackages(this.onlineStatus);
        } else {
            this.showLogin();
            this.prevAction = "upload";
        }
    }

    public void showOnlinePackages() {
        if ( this.isLoggedIn() ) {
            OnlineHelper.waitForHttpGetPackages(this.onlineStatus);
            this.showList(this.onlineStatus.packageList, "online");
            this.onlineStatus.packageList = null;
        } else {
            this.showLogin();
            this.prevAction = "levels";
        }
    }

    public void showUploadedPackages() {
        if ( this.isLoggedIn() ) {
            OnlineHelper.waitForHttpGetUserPackages(this.onlineStatus);
            this.showList(this.onlineStatus.userPackageList, "upload");
            this.onlineStatus.userPackageList = null;
        } else {
            this.showLogin();
            this.prevAction = "uploaded";
        }
    }

    public void downloadPackage(String packageId) {
        this.onlineStatus.packageId = packageId;

        OnlineHelper.waitForHttpGetPackage(this.onlineStatus);
    }

    public void deleteUploadedPackage(String packageId) {
        this.onlineStatus.packageId = packageId;

        if ( OnlineHelper.waitForHttpDeletePackage(this.onlineStatus) ) {
            this.showUploadedPackages();
        }
    }

    private void showLogin() {
        if ( this.firstTime ) {
            this.setUpLoginScreen("login");
            this.firstTime = false;
        }

        this.loginTable.setVisible(true);
    }

    private void hideLogin() {
        if ( this.loginTable != null ) {
            this.loginTable.setVisible(false);
        }

    }

    private void setRightTable() {
        Table placeHolder = new Table();
        placeHolder.setWidth(5000.0f);
        placeHolder.setHeight(5000.0f);
        this.menuRightTable.add(placeHolder).expand();
    }

    /* Aufbau der ScrollPane in der rechten Hälfte des Menüs */
    private void setupScrollPane() {
        /* ScrollPane */
        this.scrollPane = new ScrollPane(this.menuRightTable);
        this.scrollPane.setName("scrollPane");
        this.scrollPane.setScrollingDisabled(true, false);

        /* Hinzufügen */
        this.containerFlagsMenu.add(this.scrollPane).width(RIGHT_SIDE_WIDTH).row();

    }

    private void disableScrollPane() {
        this.scrollPane.setScrollingDisabled(true, true);
    }

    private void activateScrollPane() {
        this.scrollPane.setScrollingDisabled(true, false);
    }

    /* Aufbau eines Package-Tables */
    private void setupPackageTable(String name) {
        /* packageTable */
        final Table packageTable = new Table();
        packageTable.setName("packageTable");
        packageTable.pad(TABLE_OFFSET);
        packageTable.setBackground(this.inactive);
        packageTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EditorMenuScreen.this.activatePackage(packageTable);
            }
        });
        this.activatePackage(packageTable);

        /* Package-Header Table */
        Table packageHeaderTable = new Table();
        packageHeaderTable.setName("packageHeaderTable");
        packageHeaderTable.pad(TABLE_OFFSET);
        packageHeaderTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EditorMenuScreen.this.activeLevelTable.setBackground(EditorMenuScreen.this.inactive);
                EditorMenuScreen.this.activeLevelTable = new Table();
                EditorMenuScreen.this.activeLevelTable.setName("");
            }
        });

        /* Package-Name Label*/
        Label labelPackageName = this.ui.addLabel(name);
        labelPackageName.setName("label_packageName");

        /* StartLevel Label */
        Label labelStartLevel = this.ui.addLabel("Startlevel: ");
        labelStartLevel.setName("label_startLevel");

        /* StartLevel Table & Selectbox */
        Table selectboxTable = new Table();
        selectboxTable.setName("selectboxTable");
        SelectBox selectboxStartLevel = this.ui.addSelectBox(this.getLevels(name), "selectbox_startLevel");

        /* Toggle Button */
        TextButton buttonToggleContent = this.ui.addTextButton("+", 0.0f, 0.0f, "editor_menu_togglebutton");
        buttonToggleContent.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EditorMenuScreen.this.activatePackage(packageTable);
                EditorMenuScreen.this.toggleContent();
            }
        });

        /* packageContentTable */
        Table packageContentTable = new Table();
        packageContentTable.setName("packageContentTable");
        packageContentTable.pad(TABLE_OFFSET);

        /* Hinzufügen */
        selectboxTable.add(selectboxStartLevel).width(Constants.WIDTH / 5.0f);
        packageHeaderTable.add(labelPackageName).expand().prefWidth(Constants.WIDTH).prefHeight(Constants.HEIGHT / 10.0f).left();
        packageHeaderTable.add(labelStartLevel).padRight(TABLE_OFFSET / 2.0f).right();
        packageHeaderTable.add(selectboxTable).right().row();
        packageHeaderTable.add(buttonToggleContent).padTop(TABLE_OFFSET).left();
        packageTable.add(packageHeaderTable).row();
        packageTable.add(packageContentTable).expandX().prefWidth(Constants.WIDTH);
        this.menuRightInnerOfflineTable.add(packageTable).row();

        /* Overview-StartLevel setzen */
        selectboxStartLevel.setSelected(this.getOverviewStartLevel(packageTable));
        if ( this.allPackages.size() <= 1 ) {
            this.createTemporaryTable(this.allPackages.size(), this.menuRightInnerOfflineTable);
        } else {
            if ( this.temporaryTable != null ) {
                this.menuRightInnerOfflineTable.removeActor(this.temporaryTable);
            }

        }

    }

    /* Aufbau eines Level-Tables */
    private void setupLevelTable(String name) {
        /* levelTable */
        final Table levelTable = new Table();
        levelTable.setName("packageTable");
        levelTable.pad(TABLE_OFFSET);
        levelTable.setBackground(this.inactive);
        levelTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EditorMenuScreen.this.activateLevel(levelTable);
            }
        });
        this.activateLevel(levelTable);

        /* Level-Name Label*/
        Label labelLevelName = this.ui.addLabel(name);
        labelLevelName.setName("label_levelName");

        /* Bearbeiten Button */
        Button editButton = this.ui.addButton(Constants.UI_ICON_SETTINGS, 0, 0, "editor_menu_editlevel");
        editButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                EditorMenuScreen.this.manager.setCurrentPackage(EditorMenuScreen.this.convertForAssets(EditorMenuScreen.this.getPackageName(EditorMenuScreen.this.activePackageTable)));
                EditorMenuScreen.this.activateLevel(levelTable);
                EditorMenuScreen.this.game.getScreen().dispose();
                EditorScreen editorScreen = new EditorScreen(EditorMenuScreen.this.game, EditorScreen.Type.EDIT_WORLD);
                String levelPath = EditorMenuScreen.this.getLevelPath();
                editorScreen.scheduleLoad(levelPath);
                editorScreen.load(levelPath);
                EditorMenuScreen.this.game.setScreen(editorScreen);
            }
        });

        /* Hinzufügen */
        levelTable.add(labelLevelName).expand().prefWidth(Constants.WIDTH).prefHeight(Constants.HEIGHT / 10.0f);
        levelTable.add(editButton).expand().right();
        this.getPackageContentTable(this.activePackageTable).add(levelTable).row();
    }

    /* Erstellung aller Package- und Level-Tables */
    private void setupContent(Map<String, List<String>> content) {
        for ( Map.Entry<String, List<String>> entry : content.entrySet() ) {
            String packageName = entry.getKey();
            this.setupPackageTable(packageName);

            List<String> allLevels = entry.getValue();
            for ( String allLevel : allLevels ) {
                this.setupLevelTable(allLevel);
            }
            this.toggleContent();
        }
        if ( content.isEmpty() ) {
            this.createTemporaryTable(content.size(), this.menuRightInnerOfflineTable);
        } else if ( content.size() == 1 ) {
            this.createTemporaryTable(content.size(), this.menuRightInnerOfflineTable);
        }
    }

    /* Erstellung aller Package- und Level-Tables */
    private void setupContentDownloaded(Map<String, List<String>> content) {
        for ( Map.Entry<String, List<String>> entry : content.entrySet() ) {
            String packageName = entry.getKey();
            String path = "content_packages/downloaded_packages/" + packageName;
            String name = this.parseFromOverview(packageName, path, "Name");
            String autor = this.parseFromOverview(packageName, path, "Autor");
            String id = this.parseFromOverview(packageName, path, "Id");
            this.setUpPackagesScreen(name, autor, id, "download");
            //setupPackageTable(packageName);

            List<String> allLevels = entry.getValue();
            for ( String allLevel : allLevels ) {
                this.setupLevelTable(allLevel);
            }
            this.toggleContent();
        }
    }

    /* Kontrolliert ob der Benutzer etwas eingegeben hat */
    public boolean checkInput() {
        String input = this.input.getText();
        return !input.isEmpty();
    }

    /* Kontrolliert ob die Benutzereingabe bestehende Packages überschreiben würde */
    public boolean checkOverwrite() {
        this.overwrite = false;
        String input = this.input.getText();
        if ( !input.isEmpty() ) {
            /* Wird ein Level überschrieben? */
            if ( this.levelActivated() ) {
                List<String> allLevels = this.allPackages.get(this.getPackageName(this.activePackageTable));
                for ( String level : allLevels ) {
                    if ( level.equals(input) ) {
                        this.overwrite = true;
                        break;
                    }
                }

                /* Wird ein Package überschrieben? */
            } else {
                for ( Map.Entry<String, List<String>> stringArrayListEntry : this.allPackages.entrySet() ) {
                    String packageName = stringArrayListEntry.getKey();
                    if ( packageName.equals(input) ) {
                        this.overwrite = true;
                        break;
                    }
                }
            }
        }
        return this.overwrite;
    }

    /* Beschreiben der Package-Overview in den Assets */
    private void createOverview(FileHandle dir, String name, String startLevel) {
        StringWriter writer = new StringWriter();
        XmlWriter xmlWriter = new XmlWriter(writer);
        try {
            xmlWriter.element("Package");

            xmlWriter.element("Id").text(this.generateRandomID()).pop();

            xmlWriter.element("Name").text(name).pop();

            xmlWriter.element("Autor").text("Superteam4000") //TODO: Noch hardcode
                     .pop();

            xmlWriter.element("StartLevel").text(startLevel).pop();

            xmlWriter.pop();
            xmlWriter.close();

        } catch ( IOException e ) {
            Gdx.app.log("Fehler", "Beim Schreiben der Overview.xml ist ein Fehler aufgetreten");
        }
        dir.writeString(writer.toString(), false);
    }

    /* Ein neues Package wird in der HashMap, in den Assets und im UI erstellt */
    public void createPackage() {
        String name = this.input.getText();

        if ( !name.isEmpty() ) {
            /* Komplette Löschung des überschriebenen Packages */
            if ( this.overwrite ) {
                this.deletePackage(name, Constants.PACKAGE_FOLDER);
            }

            /* Erstellung in der HashMap */
            this.allPackages.put(name, new ArrayList<String>());

            /* Erstellung in den Assets */
            FileHandle dir = Gdx.files.local(Constants.PACKAGE_FOLDER + this.convertForAssets(name) + "/overview.xml");
            StringWriter writer = new StringWriter();
            dir.writeString(writer.toString(), false);
            this.createOverview(dir, name, "");

            /* Erstellung im UI */
            //setupPackageTable(name);

            /* Muss am Ende nochmal neu gesetzt werden */
            if ( this.overwrite ) {
                this.deactivatePackage();
                this.deactivateLevel();
            }

            this.hideWindow();
        }
        this.menuRightInnerOfflineTable.clear();
        this.setupContent(this.allPackages);
    }

    /* Ein neues Level wird in der HashMap, in den Assets und im UI erstellt */
    public void createLevel() {
        String packageName = this.getPackageName(this.activePackageTable);
        String name = this.input.getText();

        if ( !name.isEmpty() ) {
            /* Komplette Löschung des überschriebenen Levels */
            if ( this.overwrite ) {
                this.deleteLevel(name);
            }

            /* Erstellung in der HashMap */
            this.allPackages.get(packageName).add(name);

            /* Erstellung in den Assets */
            FileHandle dir = Gdx.files.local(Constants.PACKAGE_FOLDER + this.convertForAssets(packageName) + "/levels/" + this.convertForAssets(name) + ".xml");
            StringWriter writer = new StringWriter();
            dir.writeString(writer.toString(), false);

            /* Erstellung im UI */
            this.setupLevelTable(name);

            /* Nach der Erstellung sollen die Level ausgeklappt werden */
            if ( !this.getPackageContentTable(this.activePackageTable).hasChildren() ) {
                this.toggleContent();
            }

            this.updateStartLevelSelectBox();
            this.hideWindow();
        }
    }

    /* Das aktive Package wird in der HashMap, in den Assets und im UI umbenannt */
    public void renamePackage() {
        String oldName = this.getPackageName(this.activePackageTable);
        String newName = this.input.getText();

        if ( !newName.isEmpty() ) {
            /* Das überschriebene Package wird komplett gelöscht */
            this.deletePackage(newName, Constants.PACKAGE_FOLDER);

            /* Umbenennung in der HashMap */
            this.allPackages.remove(oldName);
            this.allPackages.put(newName, this.parseLevels(oldName));

            /* Umbenennung im UI */
            this.setPackageName(this.activePackageTable, newName);

            /* Umbenennung in den Assets */
            Gdx.files.local(Constants.PACKAGE_FOLDER + this.convertForAssets(oldName))
                     .moveTo(Gdx.files.local(Constants.PACKAGE_FOLDER + this.convertForAssets(newName)));
            this.updateOverview(this.activePackageTable);

            /* Muss am Ende nochmal neu gesetzt werden */
            this.deactivatePackage();
            this.deactivateLevel();

            this.hideWindow();
        }
    }

    /* Das aktive Level wird in der HashMap, in den Assets und im UI umbenannt */
    public void renameLevel() {
        String packageName = this.getPackageName(this.activePackageTable);
        String oldName = this.getLevelName(this.activeLevelTable);
        String newName = this.input.getText();

        if ( !newName.isEmpty() ) {
            /* Das überschriebene Level wird komplett gelöscht */
            this.deleteLevel(newName);

            /* Umbenennung in der HashMap */
            this.allPackages.get(packageName).remove(oldName);
            this.allPackages.get(packageName).add(newName);

            /* Umbenennung in den Assets */
            Gdx.files.local(Constants.PACKAGE_FOLDER + this.convertForAssets(packageName) + "/levels/" + this.convertForAssets(oldName) + ".xml")
                     .moveTo(Gdx.files.local(Constants.PACKAGE_FOLDER
                                             + this.convertForAssets(packageName)
                                             + "/levels/"
                                             + this.convertForAssets(newName)
                                             + ".xml"));

            /* Umbenennung im UI */
            this.setLevelName(this.activeLevelTable, newName);

            /* Muss am Ende nochmal neu gesetzt werden */
            this.deactivateLevel();

            this.updateStartLevelSelectBox();
            this.hideWindow();
        }
    }

    /* Das gewählte Package wird in der HashMap, in den Assets und im UI gelöscht */
    public void deletePackage(String name, String path) {
        /* Löschung in der HashMap */
        this.allPackages.remove(name);

        /* Löschung in den Assets */
        Gdx.files.local(path + this.convertForAssets(name)).deleteDirectory();

        /* Löschung im UI */
        for ( Actor actor : this.menuRightInnerOfflineTable.getChildren() ) {
            Table table = (Table) actor;
            for ( Actor subActor : table.getChildren() ) {
                if ( subActor.getName().equals("packageHeaderTable") ) {
                    Table headerTable = (Table) subActor;
                    for ( Actor child : headerTable.getChildren() ) {
                        if ( child.getName().equals("label_packageName") ) {
                            Label tableName = (Label) child;
                            if ( tableName.getText().toString().equals(name) ) {
                                table.remove();
                            }
                        }
                    }
                }
            }
        }
        this.menuRightInnerOfflineTable.clear();
        this.setupContent(this.allPackages);
    }

    /* Das gewählte Level wird in der HashMap, in den Assets und im UI gelöscht */
    private void deleteLevel(String name) {
        String packageName = this.getPackageName(this.activePackageTable);

        /* Löschung in der HashMap */
        this.allPackages.get(packageName).remove(name);

        /* Löschung in den Assets */
        Gdx.files.local(Constants.PACKAGE_FOLDER + this.convertForAssets(packageName) + "/levels/" + this.convertForAssets(name) + ".xml").delete();

        /* Löschung im UI */
        for ( Actor actor : this.getPackageContentTable(this.activePackageTable).getChildren() ) {
            Table level = (Table) actor;
            for ( Actor subActor : level.getChildren() ) {
                if ( subActor.getName().equals("label_levelName") ) {
                    Label label = (Label) subActor;
                    if ( label.getText().toString().equals(name) ) {
                        level.remove();
                    }
                }
            }
        }
        this.updateStartLevelSelectBox();

    }

    /* Liefert den Namen des gewählten Package-Tables */
    private String getPackageName(Table packageTable) {
        String packageName = "";
        for ( Actor actor : packageTable.getChildren() ) {
            if ( actor.getName().equals("packageHeaderTable") ) {
                Table headerTable = (Table) actor;
                for ( Actor child : headerTable.getChildren() ) {
                    if ( child.getName().equals("label_packageName") ) {
                        Label name = (Label) child;
                        packageName = name.getText().toString();
                    }
                }
            }
        }
        return packageName;
    }

    /* Liefert den Namen des gewählten Level-Tables */
    private String getLevelName(Table levelTable) {
        String levelName = "";
        for ( Actor actor : levelTable.getChildren() ) {
            if ( actor.getName().equals("label_levelName") ) {
                Label name = (Label) actor;
                levelName = name.getText().toString();
            }
        }
        return levelName;
    }

    /* Setzt den Namen des gewählten Package-Tables */
    private void setPackageName(Table packageTable, String text) {
        for ( Actor actor : packageTable.getChildren() ) {
            if ( actor.getName().equals("packageHeaderTable") ) {
                Table headerTable = (Table) actor;
                for ( Actor child : headerTable.getChildren() ) {
                    if ( child.getName().equals("label_packageName") ) {
                        Label name = (Label) child;
                        name.setText(text);
                    }
                }
            }
        }
    }

    /* Setzt den Namen des gewählten Level-Tables */
    private void setLevelName(Table levelTable, String text) {
        for ( Actor actor : levelTable.getChildren() ) {
            if ( actor.getName().equals("label_levelName") ) {
                Label name = (Label) actor;
                name.setText(text);
            }
        }
    }

    /* Setzt den gewählten Table als aktives Package */
    private void activatePackage(Table table) {
        this.activePackageTable.setBackground(this.inactive);
        this.activePackageTable = table;
        this.activePackageTable.setSkin(this.ui.getSkin());
        this.activePackageTable.setBackground(this.active);
    }

    /* Setzt den gewählten Table als aktives Level */
    private void activateLevel(Table table) {
        this.activeLevelTable.setBackground(this.inactive);
        this.activeLevelTable = table;
        this.activeLevelTable.setSkin(this.ui.getSkin());
        this.activeLevelTable.setBackground(this.active);
        LevelManager.getInstance().setCurrentLevel(this.convertForAssets(this.getLevelName(this.activeLevelTable)));
    }

    /* Deaktiviert das aktive Package */
    private void deactivatePackage() {
        this.activePackageTable.setBackground(this.inactive);
        this.activePackageTable = new Table();
        this.activePackageTable.setName("");
    }

    /* Deaktiviert das aktive Level */
    private void deactivateLevel() {
        this.activeLevelTable.setBackground(this.inactive);
        this.activeLevelTable = new Table();
        this.activeLevelTable.setName("");
    }

    /* Kontrolliert ob ein Package ausgewählt ist */
    public boolean packageActivated() {
        return !this.activePackageTable.getName().isEmpty();
    }

    /* Kontrolliert ob ein Level ausgewählt ist */
    public boolean levelActivated() {
        return !this.activeLevelTable.getName().isEmpty();
    }

    /* Liefert die Level-Sektion des gewählten Package-Tables */
    private Table getPackageContentTable(Table packageTable) {
        Table packageContentTable = new Table();
        for ( Actor actor : packageTable.getChildren() ) {
            if ( actor.getName().equals("packageContentTable") ) {
                packageContentTable = (Table) actor;
            }
        }
        return packageContentTable;
    }

    /* Ein- und ausklappen der Levels eines Packages */
    /* Intern: Löschung und erneute Erstellung der Levels auf UI-Ebene */
    private void toggleContent() {
        Table activePackageContentTable = this.getPackageContentTable(this.activePackageTable);

        /* Einklappen der Level */
        if ( activePackageContentTable.hasChildren() ) {
            activePackageContentTable.clearChildren();
            this.deactivateLevel();

            /* Ausklappen der Level */
        } else {
            String packageName = this.getPackageName(this.activePackageTable);
            List<String> allLevels = this.allPackages.get(packageName);
            for ( String allLevel : allLevels ) {
                this.setupLevelTable(allLevel);
            }
        }
    }

    /* Ein Eingabefenster erscheint */
    private void showInputWindow(String leftButton, String rightButton, String leftHandle, String rightHandle) {
        this.hideWindow();

        this.input = new TextField("", this.ui.getSkin());
        this.windowTable.add(this.input).expandX().prefWidth(Constants.WIDTH / 3.0f);

        this.window = this.ui.addTwoButtonWindow(this.windowTable, leftButton, rightButton, leftHandle, rightHandle);
        this.window.setBackground(this.inactive);
        this.window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.window.setPosition(Constants.WIDTH / 2.0f - WINDOW_WIDTH / 2.0f, Constants.HEIGHT / 2.0f - WINDOW_HEIGHT / 2.0f);
    }

    /* Ein Hinweisfenster erscheint */
    public void showMessageWindow(String message, String leftButton, String rightButton, String leftHandle, String rightHandle) {
        if ( !this.activePackageTable.getName().isEmpty() ) {
            this.hideWindow();

            Label label = new Label(message, this.ui.getSkin());
            label.setAlignment(Align.center);
            this.windowTable.add(label).expandX().prefWidth(Constants.WIDTH / 3.0f);

            this.window = this.ui.addTwoButtonWindow(this.windowTable, leftButton, rightButton, leftHandle, rightHandle);
            this.window.setBackground(this.inactive);
            this.window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            this.window.setPosition(Constants.WIDTH / 2.0f - WINDOW_WIDTH / 2.0f, Constants.HEIGHT / 2.0f - WINDOW_HEIGHT / 2.0f);
        }
    }

    /* Lässt das aktive Fenster verschwinden */
    public void hideWindow() {
        if ( this.window != null ) {
            this.window.remove();
            this.windowTable.clearChildren();
        }
    }

    /* Liefert den Pfad des aktuell gewählten Levels */
    private String getLevelPath() {
        String packageName = this.convertForAssets(this.getPackageName(this.activePackageTable));
        String levelName = this.convertForAssets(this.getLevelName(this.activeLevelTable));

        return Constants.PACKAGE_FOLDER + packageName + "/levels/" + levelName + ".xml";
    }

    /* Parst die Package-Overview.xml und liefert das StartLevel */
    public String parseStartLevel(String packagePath) {
        XmlReader xmlReader = new XmlReader();
        FileHandle file = Gdx.files.internal(packagePath + "/overview.xml"); //TODO local/internal

        this.xmlScene = xmlReader.parse(file);

        return this.xmlScene.getChildByName("StartLevel").getText() + ".xml";
    }

    public String parseFromOverview(String packageName, String packagePath, String item) {
        //String namePackage = convertForAssets(packageName);
        XmlReader xmlReader = new XmlReader();
        FileHandle file = Gdx.files.internal(packagePath + "/overview.xml"); //TODO local/internal

        this.xmlScene = xmlReader.parse(file);
        String levelPath = "";
        if ( item.equals("Name") ) {
            levelPath = this.xmlScene.getChildByName("Name").getText();
        } else if ( item.equals("Autor") ) {
            levelPath = this.xmlScene.getChildByName("Autor").getText();
        } else if ( item.equals("Id") ) {
            levelPath = this.xmlScene.getChildByName("Id").getText();
        } else {
            levelPath = "falsches Parsing!!!";
        }

        return levelPath;
    }

    /* Setzt die im Scene-Menü gewählte Szene für das gerade erstellte Level */
    public void initNewLevel(String sceneName) {
        FileHandle
            currentCreatedLevel =
            Gdx.files.local(Constants.PACKAGE_FOLDER + this.convertForAssets(this.getPackageName(this.activePackageTable)) + "/levels/" + this.convertForAssets(
                this.getLevelName(this.activeLevelTable)) + ".xml");
        Gdx.files.local(Constants.AVAILABLE_SCENES_PATH + sceneName).copyTo(currentCreatedLevel);

        /* Muss am Ende nochmal neu gesetzt werden */
        if ( this.overwrite ) {
            this.deactivateLevel();
        }
    }

    /* Liefert alle Levels des übergebenen Packages als Array<String> */
    private Array<String> getLevels(String packageName) {

        List<String> array = this.allPackages.get(packageName);
        Array<String> allLevels = new Array<String>();
        allLevels.add(STARTLEVEL_HEADER);
        for ( String s : array ) {
            allLevels.add(s);
        }
        return allLevels;

    }

    /* Liefert das in der SelectBox gewählte Startlevel des gewählten Packages */
    private String getSelectBoxStartLevel(Table packageTable) {
        String startLevel = "";
        for ( Actor actor : packageTable.getChildren() ) {
            if ( actor.getName().equals("packageHeaderTable") ) {
                Table packageHeaderTable = (Table) actor;
                for ( Actor subActor : packageHeaderTable.getChildren() ) {
                    if ( subActor.getName().equals("selectboxTable") ) {
                        Table selectboxTable = (Table) subActor;
                        for ( Actor subsubActor : selectboxTable.getChildren() ) {
                            if ( subsubActor.getName().equals("selectbox_startLevel") ) {
                                SelectBox selectboxStartLevel = (SelectBox) subsubActor;
                                startLevel = selectboxStartLevel.getSelected().toString();
                            }
                        }
                    }
                }
            }
        }
        return startLevel;
    }

    /* Liefert das in der Overview stehende Startlevel des gewählten Packages */
    private String getOverviewStartLevel(Table packageTable) {
        XmlReader xmlReader = new XmlReader();
        FileHandle overview = Gdx.files.local(Constants.PACKAGE_FOLDER + this.convertForAssets(this.getPackageName(packageTable)) + "/overview.xml");

        XmlReader.Element root = xmlReader.parse(overview);

        return root.getChildByName("StartLevel").getText();
    }

    /* Aktualisiert die Startlevel-Selectbox des aktuell aktiven Packages */
    private void updateStartLevelSelectBox() {
        SelectBox selectboxStartLevel = this.ui.addSelectBox(this.getLevels(this.getPackageName(this.activePackageTable)), "selectbox_startLevel");
        for ( Actor actor : this.activePackageTable.getChildren() ) {
            if ( actor.getName().equals("packageHeaderTable") ) {
                Table packageHeaderTable = (Table) actor;
                for ( Actor subActor : packageHeaderTable.getChildren() ) {
                    if ( subActor.getName().equals("selectboxTable") ) {
                        Table selectboxTable = (Table) subActor;
                        selectboxTable.clearChildren();
                        selectboxTable.add(selectboxStartLevel).width(Constants.WIDTH / 5);
                    }
                }
            }
        }
    }

    /* Aktualisiert die Overview.xml des gewählten Packages */
    private void updateOverview(Table packageTable) {
        String packageName = this.getPackageName(packageTable);
        String startLevel = this.getSelectBoxStartLevel(packageTable);
        FileHandle dir = Gdx.files.local(Constants.PACKAGE_FOLDER + this.convertForAssets(packageName) + "/overview.xml");
        this.createOverview(dir, packageName, startLevel);
    }

    /* Wandelt einen Pfad für den User um (Leerzeichen und Uppercase) */
    private String convertForUser(String text) {
        String replace = text.replace("_", "_ ");

        String result = "";
        StringTokenizer st = new StringTokenizer(replace, "_ ");
        while ( st.hasMoreTokens() ) {
            String token = st.nextToken();
            token = Character.toUpperCase(token.charAt(0)) + token.substring(1);
            result += token;
            if ( st.hasMoreTokens() ) {
                result += " ";
            }
        }
        return result;
    }

    /* Wandelt einen Pfad für die Assets um (Unterstriche und Lowercase) */
    private String convertForAssets(String text) {
        return text.toLowerCase().replace(" ", "_");
    }

    /* Generiert eine zufällige 5-stellige ID */
    private int generateRandomID() {
        Random r = new Random(System.currentTimeMillis());
        return ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
    }
}