package de.loc.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import de.loc.core.LevelManager;
import de.loc.dialog.NameComponent;
import de.loc.graphics.IconComponent;
import de.loc.input.userinterface.BaseScreen;
import de.loc.input.userinterface.LandsOfCinderWindow;
import de.loc.input.userinterface.LocTextArea;
import de.loc.input.userinterface.UserInterface;
import de.loc.main.LandsOfCinder;
import de.loc.quest.QuestEditorScreen;
import de.loc.tools.Constants;

public class DialogEditorScreen extends BaseScreen {
    private final EditorScreen editor;
    private QuestEditorScreen questEditor;
    public boolean launchedFromQuesteditor;

    private FileHandle file;
    private XmlWriter xmlWriter;
    private XmlReader xmlReader;

    private final String currentPackage;
    private final String currentLevel;

    private String playerName;
    private String playerIconPath;
    private String npcName;
    private String npcIconPath;

    protected Dialog dialog;
    private ArrayList<Dialog> dialogList;
    private Array<String> speakerList;

    private Table mainTable;
    private Table dialogEditorTable;
    private Table dialogContainerTable;
    private Table menuTable;
    private Table dialogTable;
    private Table activeDialogTable;
    private Table windowTable;
    private Actor activeDeletion;

    private LandsOfCinderWindow window;
    private TextField input;

    private Label infoLog;
    private int iconSize;
    private NinePatchDrawable active;
    private NinePatchDrawable inactive;

    /* UI Schriftgröße und UI Schriftfarbe */
    private static final int UI_TEXTSIZE = 18;
    private static final Color UI_TEXTCOLOR = Color.BLACK;

    /* Window Breite und Höhe */
    private static final float WINDOW_WIDTH = Constants.WIDTH / 2.0f;
    private static final float WINDOW_HEIGHT = Constants.HEIGHT / 5.0f;

    /* Abstand zwischen den einzelnen UI-Elementen */
    private static final int TABLE_OFFSET = 20;

    /* Dimension der Dialog-Textfelder */
    private static final int DIALOGFIELD_WIDTH = (int) (Constants.WIDTH * 0.70f);
    //private static final int DIALOGFIELD_HEIGHT = (int) (Constants.HEIGHT * 0.15);

    /* Dimension des Icons */
    private static final int ICON_SIZE_SMALL = (int) (181.0f * 0.75f);
    private static final int ICON_SIZE_LARGE = 181;

    /* Default-Konstruktor */
    public DialogEditorScreen(LandsOfCinder game, EditorScreen editor) {
        super(game);
        this.editor = editor;
        this.launchedFromQuesteditor = false;
        this.currentPackage = LevelManager.getInstance().getCurrentPackage();
        this.currentLevel = LevelManager.getInstance().getCurrentLevelFileName();
        this.setupDialogEditor();
    }

    /* Konstruktor (vom Questeditor aus gestartet) */
    public DialogEditorScreen(LandsOfCinder game, EditorScreen editor, QuestEditorScreen questEditor) {
        super(game);
        this.editor = editor;
        this.questEditor = questEditor;
        this.launchedFromQuesteditor = true;
        this.currentPackage = LevelManager.getInstance().getCurrentPackage();
        this.currentLevel = LevelManager.getInstance().getCurrentLevelFileName();
        this.setupDialogEditor();
    }

    /* Initialisierung von Daten und Aufbau der Benutzeroberfläche */
    public void setupDialogEditor() {
        this.init();
        this.setupUserInterface();
    }

    /* Initialisierung von UI, Sprecher, Dialog, Arrays, etc. */
    private void init() {
        /* UI */
        this.inputHandler = new DialogEditorInputHandler(this, this.editor);
        this.ui = new UserInterface(this.inputHandler);

        /* Sprecher */
        this.initPlayer();
        this.initNPC();

        /* Dialog */
        this.dialog = new Dialog();
        this.dialog.speaker = this.npcName;
        this.dialog.iconPath = this.npcIconPath;

        /* Arrays */
        this.dialogList = new ArrayList<>();
        this.initSpeakerList();

        /* Icon Size */
        if ( Constants.WIDTH >= 1280.0f && Constants.HEIGHT >= 720.0f ) {
            this.iconSize = ICON_SIZE_LARGE;
        } else {
            this.iconSize = ICON_SIZE_SMALL;
        }

        /* Sonstiges */
        this.activeDialogTable = new Table();
        this.active = new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_PAPER_DARK));
        this.inactive = new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_PAPER_BRIGHT));
    }

    /* Initialisierung des Spielers anhand der PlayerList.xml */
    private void initPlayer() {
        this.playerName = "HERO";
        FileHandle playerFile = Gdx.files.local(Constants.PLAYER_LIST_PATH);

        this.xmlReader = new XmlReader();
        XmlReader.Element xmlRoot = this.xmlReader.parse(playerFile);
        this.playerIconPath = xmlRoot.getChildByName("Player").getChildByName("Icon").getText();

    }

    /* Initialisierung des Sprecher-NPCs */
    private void initNPC() {
        if ( this.launchedFromQuesteditor ) {
            this.npcName = this.questEditor.getSpeakerFromQuestEditor();
            FileHandle
                npcFile =
                Gdx.files.local(Constants.PACKAGE_FOLDER
                                + this.currentPackage
                                + "/npcs/"
                                + this.npcName.replace(" ", "_")
                                + "/"
                                + this.npcName.replace(" ",
                                                       "_")
                                + ".xml");

            this.xmlReader = new XmlReader();
            XmlReader.Element xmlRoot = this.xmlReader.parse(npcFile);
            this.npcIconPath = xmlRoot.getChildByName("Icon").getText();

        } else {
            this.npcName = this.editor.getCurrentEntity().getComponent(NameComponent.class).name;
            this.npcIconPath = this.editor.getCurrentEntity().getComponent(IconComponent.class).iconPath;
        }
    }

    /* Initialisierung der verfügbaren Sprecher */
    private void initSpeakerList() {
        this.speakerList = new Array<String>();
        this.speakerList.add(this.npcName);
        this.speakerList.add(this.playerName);
    }

    /* Aufbau der Benutzeroberfläche */
    private void setupUserInterface() {
        /* Schriftgröße und Schriftfarbe */
        this.ui.setScreenSize(UI_TEXTSIZE, UI_TEXTCOLOR);

        this.setupMainTable();
        this.setupDialogEditorTable();
        this.setupDialogContainerTable();
        this.setupMenuTable();
        this.setupDialogTable();

        this.setupWindowTable();
    }

    /* Aufbau des MainTables */
    private void setupMainTable() {
        /* MainTable */
        this.mainTable = this.ui.getMainTable();
        this.mainTable.setName("mainTable");
        this.mainTable.setFillParent(true);
        this.mainTable.setBackground(new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_GRAY)));
    }

    /* Aufbau des DialogEditorTables */
    private void setupDialogEditorTable() {
        /* DialogEditorTable */
        this.dialogEditorTable = new Table();
        this.dialogEditorTable.setName("dialogEditorTable");
        this.dialogEditorTable.pad((float) TABLE_OFFSET);

        /* Hinzufügen */
        this.mainTable.add(this.dialogEditorTable).top().row();
    }

    /* Aufbau des dialogContainerTables */
    private void setupDialogContainerTable() {
        /* DialogContainerTable */
        this.dialogContainerTable = new Table();
        this.dialogContainerTable.setName("dialogContainerTable");
        this.dialogContainerTable.pad((float) TABLE_OFFSET);

        /* Hinzufügen */
        this.dialogEditorTable.add(this.dialogContainerTable).row();

        /* Die ScrollPane muss am Ende hinzugefügt werden */
        this.setupScrollPane();
    }

    /* Aufbau der ScrollPane im DialogContainerTable */
    private void setupScrollPane() {
        /* ScrollPane */
        ScrollPane scrollPane = new ScrollPane(this.dialogContainerTable);
        scrollPane.setName("scrollPane");
        scrollPane.setScrollingDisabled(true, false);

        /* Hinzufügen */
        this.dialogEditorTable.add(scrollPane).width(Constants.WIDTH).row();
    }

    /* Aufbau des MenuTables */
    private void setupMenuTable() {
        /* MenuTable */
        this.menuTable = new Table();
        this.menuTable.setName("menuTable");
        this.menuTable.pad((float) TABLE_OFFSET);
        this.menuTable.setBackground(this.inactive);

        /* Back Button */
        Button buttonBack = this.ui.addButton(Constants.UI_ICON_BACK, 0, 0, "button_back");

        /* InfoLog Label */
        this.infoLog = this.ui.addLabel("Willkommen im DialogEditor 2.0!");
        this.infoLog.setName("label_infoLog");
        this.infoLog.setAlignment(Align.center);

        /* AddDialog Button */
        Button buttonAddDialog = this.ui.addButton(Constants.UI_ICON_PLUS, 0, 0, "button_addDialog");
        buttonAddDialog.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                DialogEditorScreen.this.setupDialogTable();
            }
        });

        /* SaveDialog Button */
        Button buttonSaveDialog = this.ui.addTextButton("Speichern", 0, 0, "button_saveDialog");

        /* Hinzufügen */
        this.menuTable.add(buttonBack).pad((float) (TABLE_OFFSET / 2));
        this.menuTable.add(this.infoLog).pad((float) (TABLE_OFFSET / 2)).expandX().prefWidth(Constants.WIDTH);
        this.menuTable.add(buttonAddDialog).pad((float) (TABLE_OFFSET / 2));
        this.menuTable.add(buttonSaveDialog).pad((float) (TABLE_OFFSET / 2));
        this.mainTable.add(this.menuTable).expand().bottom().row();
    }

    /* Aufbau eines DialogTables */
    private void setupDialogTable() {
        final Table dialogTable = new Table();
        dialogTable.setName("dialogTable");
        dialogTable.pad((float) TABLE_OFFSET);
        dialogTable.setBackground(this.inactive);
        dialogTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                DialogEditorScreen.this.activeDialogTable.setBackground(DialogEditorScreen.this.inactive);
                DialogEditorScreen.this.activeDialogTable = dialogTable;
                DialogEditorScreen.this.activeDialogTable.setSkin(DialogEditorScreen.this.ui.getSkin());
                DialogEditorScreen.this.activeDialogTable.setBackground(DialogEditorScreen.this.active);
            }
        });
        this.dialogTable = dialogTable;

        this.setupSpeakerTable();
        this.setupTextTable();

        /* Delete */
        final Label labelDelete = this.ui.addLabel(" X ");
        labelDelete.setName("label_delete");
        labelDelete.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                DialogEditorScreen.this.activeDeletion = labelDelete;
                DialogEditorScreen.this.activeDeletion.getParent().remove();
            }
        });

        /* Hinzufügen */
        dialogTable.add(labelDelete).padLeft((float) (TABLE_OFFSET / 2)).expand().top().right();
        this.dialogContainerTable.add(dialogTable).expandX().prefWidth(Constants.WIDTH).row();
    }

    /* Aufbau des SpeakerTables */
    private void setupSpeakerTable() {
        /* SpeakerTable */
        Table speakerTable = new Table();
        speakerTable.setName("speakerTable");
        speakerTable.pad((float) (TABLE_OFFSET / 2));

        /* Speaker-Icon Button */
        Table iconTable = new Table();
        iconTable.setName("iconTable");
        Button buttonIcon = this.ui.addButton(Constants.ICON_PATH + this.npcIconPath, 0, 0, "button_icon");

        /* Speaker SelectBox */
        SelectBox selectboxSpeaker = this.ui.addSelectBox(this.speakerList, "selectbox_speaker");

        /* Hinzufügen */
        iconTable.add(buttonIcon).expand().width((float) this.iconSize).height((float) this.iconSize);
        speakerTable.add(iconTable).row();
        speakerTable.add(selectboxSpeaker).expandX().prefWidth((float) (int) ((double) this.iconSize * 1.5)).bottom();
        this.dialogTable.add(speakerTable).expand().prefWidth(Constants.WIDTH);
    }

    /* Aufbau eines TextTables */
    private void setupTextTable() {
        /* TextTable */
        Table textTable = new Table();
        textTable.setName("textTable");
        textTable.pad((float) (TABLE_OFFSET / 2));

        /* Dialogfeld TextArea */
        LocTextArea textarea_dialogField = this.ui.addLocTextArea("");
        textarea_dialogField.setName("textarea_dialogField");
        textarea_dialogField.setMaxLength(120);

        /* Hinzufügen */
        textTable.add(textarea_dialogField).padRight((float) TABLE_OFFSET).width((float) DIALOGFIELD_WIDTH).prefHeight(Constants.HEIGHT / 5.0f);
        this.dialogTable.add(textTable);
    }

    /* Aufbau eines WindowTables */
    private void setupWindowTable() {
        /* windowTable */
        this.windowTable = new Table();
        this.windowTable.setName("windowTable");
        this.windowTable.pad(20.0f);
    }

    /* Parst das UI, kontrolliert die Daten und speichert den Dialog */
    public void saveDialog() {
        this.dialogList.clear();
        this.parseDialogEditor();

        StringWriter writer = new StringWriter();
        this.xmlWriter = new XmlWriter(writer);

        if ( this.checkData() ) {
            try {
                this.xmlWriter.element("DialogSheet");
                this.xmlWriter.element("ID").text(this.input.getText()).pop();
                this.xmlWriter.element("Dialogs");

                for ( Dialog value : this.dialogList ) {
                    this.xmlWriter.element("Dialog");
                    this.xmlWriter.element("Name").text(value.speaker).pop();
                    this.xmlWriter.element("Icon").text(value.iconPath).pop();
                    this.xmlWriter.element("Text").text(value.text).pop();
                    this.xmlWriter.pop();
                }
                this.xmlWriter.close();

            } catch ( IOException e ) {
                this.setLogText("Fehler: Bitte kontrolliere deine Eingaben.");
            }

            String relativeFilePath = "/npcs/" + this.npcName.replace(" ", "_").toLowerCase() + "/" + this.input.getText().replace(" ", "_") + ".dialog";
            this.file = Gdx.files.local(Constants.PACKAGE_FOLDER + this.currentPackage + relativeFilePath);
            this.file.writeString(writer.toString(), false);

            if ( this.launchedFromQuesteditor == true ) {
                this.questEditor.addDialog(relativeFilePath);
            }

            this.setLogText("Dialog erfolgreich hinzugefügt!");
            this.hideWindow();
        }
    }

    /* Auslesen aller Dialogfelder */
    private void parseDialogEditor() {
        for ( Actor actor : this.dialogContainerTable.getChildren() ) {
            this.dialog = new Dialog();
            Table dialogTable = (Table) actor;
            for ( Actor subActor : dialogTable.getChildren() ) {
                if ( subActor.getName().equals("speakerTable") ) {
                    this.parseSpeakerTable((Table) subActor);
                }
                if ( subActor.getName().equals("textTable") ) {
                    this.parseTextTable((Table) subActor);
                }
            }
            this.dialogList.add(this.dialog);
        }
    }

    /* Auslesen des Dialogsprechers und dessen Icon */
    private void parseSpeakerTable(Table speakerTable) {
        for ( Actor actor : speakerTable.getChildren() ) {
            if ( actor.getName().equals("selectbox_speaker") ) {
                SelectBox speakerSelection = (SelectBox) actor;
                this.dialog.speaker = speakerSelection.getSelected().toString();
                if ( this.dialog.speaker.equals(this.npcName) ) {
                    this.dialog.iconPath = this.npcIconPath;
                } else {
                    this.dialog.iconPath = this.playerIconPath;
                }
            }
        }
    }

    /* Auslesen des gesprochenen Textes */
    private void parseTextTable(Table textTable) {
        for ( Actor actor : textTable.getChildren() ) {
            if ( actor.getName().equals("textarea_dialogField") ) {
                LocTextArea dialogfield = (LocTextArea) actor;
                this.dialog.text = dialogfield.getText();
            }
        }
    }

    /* Kontrolliert die vom Benutzer eingegebenen Daten */
    private boolean checkData() {
        if ( this.input.getText().isEmpty() ) {
            this.setLogText("Bitte wähle einen Namen für den Dialog.");
            return false;
        }

        if ( this.dialogList.isEmpty() ) {
            this.setLogText("Du hast keine Dialoge zum Speichern!");
            this.hideWindow();
            return false;
        }

        for ( Dialog dialog : this.dialogList ) {
            if ( dialog.getText().isEmpty() ) {
                this.setLogText("Es wurden nicht alle Dialogfenster ausgefüllt!");
                this.hideWindow();
                return false;
            }
        }

        return true;
    }

    /* Setzt das Icon des jeweils gewählten Sprechers */
    public void setSpeaker(String speaker) {
        String iconPath;
        if ( speaker.equals(this.npcName) ) {
            iconPath = this.npcIconPath;
        } else {
            iconPath = this.playerIconPath;
        }

        Button buttonIcon;
        Table iconTable = new Table();
        for ( Actor actor : this.activeDialogTable.getChildren() ) {
            if ( actor.getName().equals("speakerTable") ) {
                Table speakerTable = (Table) actor;
                for ( Actor subActor : speakerTable.getChildren() ) {
                    if ( subActor.getName().equals("iconTable") ) {
                        iconTable = (Table) subActor;
                    }
                }
            }
        }
        buttonIcon = this.ui.addButton(Constants.ICON_PATH + iconPath, 0, 0, "button_icon");
        iconTable.clear();
        iconTable.add(buttonIcon).expand().width((float) this.iconSize).height((float) this.iconSize);
    }

    /* Ein Speicherfenster erscheint */
    public void showSaveWindow() {
        this.showInputWindow("Zurück", "Speichern", "dialogeditor_window_back", "dialogeditor_window_save");
    }

    /* Ein Eingabefenster erscheint */
    private void showInputWindow(String leftButton, String rightButton, String leftHandle, String rightHandle) {
        this.hideWindow();

        this.input = new TextField("", this.ui.getSkin());
        this.windowTable.add(this.input).expandX().prefWidth(Constants.WIDTH / 3);

        this.window = this.ui.addTwoButtonWindow(this.windowTable, leftButton, rightButton, leftHandle, rightHandle);
        this.window.setBackground(this.inactive);
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

    /* Setzen des infoLog-Textes */
    public void setLogText(String text) {
        this.infoLog.clear();
        this.infoLog.setText(text);
    }

    /* Liefert den QuestEditorScreen vom dem aus der DialogEditor gestartet wurde */
    public QuestEditorScreen getQuestEditor() {
        return this.questEditor;
    }

    /* Eine Dialog-Datenstruktur */
    public class Dialog {
        String speaker;
        String iconPath;
        String text;

        private String getName() {
            return this.speaker;
        }

        private String getIconPath() {
            return this.iconPath;
        }

        private String getText() {
            return this.text;
        }
    }
}
