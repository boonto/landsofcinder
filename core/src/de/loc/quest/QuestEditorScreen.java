package de.loc.quest;

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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.loc.core.LevelManager;
import de.loc.editor.DialogEditorScreen;
import de.loc.editor.EditorScreen;
import de.loc.event.EventSystem;
import de.loc.input.userinterface.BaseScreen;
import de.loc.input.userinterface.LocTextArea;
import de.loc.input.userinterface.UserInterface;
import de.loc.main.LandsOfCinder;
import de.loc.tools.Constants;
import de.loc.tools.Helper;
import de.loc.tools.XmlHelper;

public class QuestEditorScreen extends BaseScreen {
    private final EditorScreen editor;
    private DialogEditorScreen dialogEditor;
    private final String currentPackage;
    private final String currentLevel;

    private Quest quest;
    private QuestEvent questEvent;
    private FileHandle file;
    private XmlWriter xmlWriter;
    private Label infoLog;
    private NinePatchDrawable active;
    private NinePatchDrawable inactive;

    private Table mainTable;
    private Table questEditorTable;
    private Table questEditorLeftTable;
    private Table questEditorRightTable;
    private Table questEditorMenuTable;
    private Table questEventTable;

    private Table activeQuestEventTable;
    private Table activeDialogTable;
    private Label activeDeletion;

    private final Array<String> availableEvents = new Array<String>();
    private final Array<String> allMobs = new Array<String>();
    private final Array<String> allNPCs = new Array<String>();
    private final Array<String> allItems = new Array<String>();
    private HashMap<EventSystem.EventType, String> eventMap;

    /* UI Schriftgröße und UI Schriftfarbe */
    private static final int UI_TEXTSIZE = 18;
    private static final Color UI_TEXTCOLOR = Color.BLACK;

    /* Breite der rechten Seite des Questeditors */
    private static final float RIGHT_SIDE_WIDTH = Constants.WIDTH / 2;

    /* Höhe der TextAreas für die Tagebucheinträge */
    private static final float JOURNAL_HEIGHT = Constants.HEIGHT / 8;

    /* Abstand zwischen den einzelnen UI-Elementen */
    private static final int TABLE_OFFSET = 20;

    /* Englische Sprache */
    private static final boolean LANGUAGE_ENGLISH = false;

    /* Benennungen verschiedener Elemente */
    private static String KILL_EVENT_STRING;
    private static String FETCH_EVENT_STRING;
    private static String DIALOG_STARTED_STRING;
    public static String EVENT_SELECTION_HEADER;
    private static String MOB_SELECTION_HEADER;
    private static String ITEM_SELECTION_HEADER;
    private static String NPC_SELECTION_HEADER;
    private static String DIALOG_SELECTION_HEADER;
    private static String NEW_DIALOG_STRING;
    private static String START_DIALOG_STRING;
    private static String SAVE_STRING;
    private static String JOURNAL_STRING;
    private static String WELCOME_MESSAGE;

    /* Konstruktor */
    public QuestEditorScreen(LandsOfCinder game, EditorScreen editor) {
        super(game);
        this.editor = editor;
        this.currentPackage = LevelManager.getInstance().getCurrentPackage();
        this.currentLevel = LevelManager.getInstance().getCurrentLevelFileName();
        this.setupQuestEditor();
    }

    /* Initialisierung von Daten, Einstellung von Spielinhalten und Aufbau der Benutzeroberfläche */
    private void setupQuestEditor() {
        this.init();
        this.setupAvailableContent();
        this.setupUserInterface();
    }

    /* Initialisierung von UI, Sprache, etc. */
    private void init() {
        /* UI */
        this.inputHandler = new QuestEditorInputHandler(this, this.editor);
        this.ui = new UserInterface(this.inputHandler);

        /* Sprache */
        if ( LANGUAGE_ENGLISH ) {
            KILL_EVENT_STRING = "Kill...";
            FETCH_EVENT_STRING = "Fetch...";
            DIALOG_STARTED_STRING = "Speak with...";
            EVENT_SELECTION_HEADER = "Choose a task";
            MOB_SELECTION_HEADER = "Choose an opponent";
            ITEM_SELECTION_HEADER = "Choose an item";
            NPC_SELECTION_HEADER = "Choose a charakter";
            DIALOG_SELECTION_HEADER = "Choose a dialog";
            NEW_DIALOG_STRING = " + New dialog";
            START_DIALOG_STRING = "Questgiver:";
            JOURNAL_STRING = "Journal:";
            SAVE_STRING = "Save";
            WELCOME_MESSAGE = "Welcome to Questeditor 2.0!";

        } else {
            KILL_EVENT_STRING = "Töte...";
            FETCH_EVENT_STRING = "Besorge...";
            DIALOG_STARTED_STRING = "Sprich mit...";
            EVENT_SELECTION_HEADER = "Wähle eine Aufgabe";
            MOB_SELECTION_HEADER = "Wähle einen Gegner";
            ITEM_SELECTION_HEADER = "Wähle ein Item";
            NPC_SELECTION_HEADER = "Wähle einen Charakter";
            DIALOG_SELECTION_HEADER = "Wähle einen Dialog";
            NEW_DIALOG_STRING = " + Neuer Dialog";
            START_DIALOG_STRING = "Questgeber:";
            JOURNAL_STRING = "Tagebuch:";
            SAVE_STRING = "Speichern";
            WELCOME_MESSAGE = "Willkommen zum Questeditor 2.0!";
        }

        /* Sonstiges */
        this.activeQuestEventTable = new Table();
        this.activeDialogTable = new Table();
        this.active = new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_PAPER_DARK));
        this.inactive = new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_PAPER_BRIGHT));
    }

    /* Diese Spielinhalte können in den SelectBoxes gewählt werden */
    private void setupAvailableContent() {
        this.initAvailableEvents();
        this.initAvailableMobs();
        this.initAvailableItems();
        this.initAvailableNPCs();
    }

    /* Diese Elemente werden in der Event-SelectBox angezeigt */
    private void initAvailableEvents() {
        this.eventMap = new HashMap<EventSystem.EventType, String>();
        this.eventMap.put(EventSystem.EventType.KILL_EVENT, KILL_EVENT_STRING);
        this.eventMap.put(EventSystem.EventType.DIALOG_STARTED, DIALOG_STARTED_STRING);
        this.eventMap.put(EventSystem.EventType.FETCH_EVENT, FETCH_EVENT_STRING);

        this.availableEvents.add(EVENT_SELECTION_HEADER);
        for ( Map.Entry<EventSystem.EventType, String> entry : this.eventMap.entrySet() ) {
            this.availableEvents.add(entry.getValue());
        }
    }

    /* Diese Elemente werden in der Mob-SelectBox angezeigt */
    private void initAvailableMobs() {
        this.allMobs.add(MOB_SELECTION_HEADER);
        for ( XmlReader.Element element : XmlHelper.getFile(Constants.MOB_LIST_PATH).getChildrenByName("Mob") ) {
            this.allMobs.add(element.getChildByName("Name").getText());
        }
    }

    /* Diese Elemente werden in der Item-SelectBox angezeigt */
    private void initAvailableItems() {
        this.allItems.add(ITEM_SELECTION_HEADER);
        for ( XmlReader.Element element : XmlHelper.getFile(Constants.ITEM_LIST_PATH).getChildrenByName("Item") ) {
            this.allItems.add(element.getChildByName("Name").getText());
        }
        for ( XmlReader.Element element : XmlHelper.getFile(Constants.CONSUMABLE_LIST_PATH).getChildrenByName("Consumable") ) {
            this.allItems.add(element.getChildByName("Name").getText());
        }
        for ( XmlReader.Element element : XmlHelper.getFile(Constants.EQUIPPABLE_LIST_PATH).getChildrenByName("Equippable") ) {
            this.allItems.add(element.getChildByName("Name").getText());
        }
    }

    /* Diese Elemente werden in der NPC-SelectBox angezeigt */
    private void initAvailableNPCs() {
        this.allNPCs.add(NPC_SELECTION_HEADER);
        FileHandle npcRootFolder = Gdx.files.local(Constants.PACKAGE_FOLDER + this.currentPackage + "/npcs/");
        for ( FileHandle npcFolder : npcRootFolder.list() ) {
            if ( npcFolder.isDirectory() ) {
                FileHandle npc = new FileHandle(npcFolder.path() + "/" + npcFolder.name() + ".xml");
                this.allNPCs.add(XmlHelper.getFile(npc.path()).getChildByName("Name").getText());
            } else {
                System.out.println("NPC Liste: Problem beim Parsen der NPC-Ordner.");
            }
        }
    }

    /* Aufbau der Benutzeroberfläche */
    private void setupUserInterface() {
        /* Schriftgröße und Schriftfarbe */
        this.ui.setScreenSize(UI_TEXTSIZE, UI_TEXTCOLOR);

        this.setupMainTable();
        this.setupQuestEditorTable();
        this.setupQuestEditorLeftTable();
        this.setupQuestEditorRightTable();
        this.setupQuestEditorMenuTable();
    }

    /* Aufbau des MainTables */
    private void setupMainTable() {
        /* MainTable */
        this.mainTable = this.ui.getMainTable();
        this.mainTable.setName("mainTable");
        this.mainTable.setFillParent(true);
        this.mainTable.setBackground(new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_GRAY)));
    }

    /* Aufbau des QuestEditorTables */
    private void setupQuestEditorTable() {
        /* QuestEditorTable */
        this.questEditorTable = new Table();
        this.questEditorTable.setName("questEditorTable");
        this.questEditorTable.pad(TABLE_OFFSET);

        /* Hinzufügen */
        this.mainTable.add(this.questEditorTable).top().row();
    }

    /* Aufbau der linken Hälfte des Questeditors */
    private void setupQuestEditorLeftTable() {
        /* QuestEditorLeftTable */
        this.questEditorLeftTable = new Table();
        this.questEditorLeftTable.setName("questEditorLeftTable");
        this.questEditorLeftTable.pad(TABLE_OFFSET);
        this.questEditorLeftTable.setBackground(this.inactive);

        this.setupQuestNameTable();
        this.setupStartDialogTable();
        this.setupStartJournalEntryTable();
        this.setupQuestEventSelectionTable();

        /* Hinzufügen */
        this.questEditorTable.add(this.questEditorLeftTable).expand().top();
    }

    /* Aufbau des Tables in dem der Questname gewählt wird */
    private void setupQuestNameTable() {
        /* questNameTable */
        Table questNameTable = new Table();
        questNameTable.setName("questNameTable");
        questNameTable.pad(TABLE_OFFSET);

        /* Questname Label*/
        Label label_questName = this.ui.addLabel("Name:");
        label_questName.setName("label_questName");

        /* Questname Textfield */
        TextField textfield_questName = this.ui.addTextField("");
        textfield_questName.setName("textfield_questName");
        textfield_questName.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if ( LANGUAGE_ENGLISH ) {
                    QuestEditorScreen.this.setLogText("Choose a name for the quest.");
                } else {
                    QuestEditorScreen.this.setLogText("Wähle einen Namen für die Quest.");
                }
            }
        });

        /* Hinzufügen */
        questNameTable.add(label_questName).left().row();
        questNameTable.add(textfield_questName).expandX().prefWidth(Constants.WIDTH).row();
        this.questEditorLeftTable.add(questNameTable).row();
    }

    /* Aufbau des Tables in dem der erste Dialog gewählt wird */
    private void setupStartDialogTable() {
        /* startDialogTable */
        final Table startDialogTable = new Table();
        startDialogTable.setName("startDialogTable");
        startDialogTable.pad(TABLE_OFFSET);
        startDialogTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                QuestEditorScreen.this.activeDialogTable = startDialogTable;
            }
        });

        /* StartDialog Label */
        Label label_startDialog = this.ui.addLabel(START_DIALOG_STRING);
        label_startDialog.setName("label_startDialog");

        /* Questgiver SelectBox */
        SelectBox selectbox_speaker = this.ui.addSelectBox(this.allNPCs, "selectbox_speaker");
        selectbox_speaker.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                QuestEditorScreen.this.activeDialogTable = startDialogTable;
                if ( LANGUAGE_ENGLISH ) {
                    QuestEditorScreen.this.setLogText("Write a dialog for the questgiver.");
                } else {
                    QuestEditorScreen.this.setLogText("Schreibe einen Dialog für den Questgeber.");
                }
            }
        });

        /* CreateDialog Button */
        Button button_createdialog = this.ui.addButton(Constants.UI_ICONS_PATH + "newdialog_icon_small.png", 0, 0, "button_createdialog");
        button_createdialog.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                QuestEditorScreen.this.activeDialogTable = startDialogTable;
                QuestEditorScreen.this.createNewDialog();
            }
        });

        /* StartDialog SelectBox */
        SelectBox selectbox_dialog = this.ui.addSelectBox(new Array<String>(), "selectbox_dialog");

        /* Hinzufügen */
        startDialogTable.add(label_startDialog).expandX().left().row();
        startDialogTable.add(selectbox_speaker).expandX().prefWidth(Constants.WIDTH);
        startDialogTable.add(button_createdialog).padLeft(TABLE_OFFSET / 2).row();
        startDialogTable.add(selectbox_dialog).padTop(TABLE_OFFSET / 2).expandX().prefWidth(Constants.WIDTH).row();
        this.questEditorLeftTable.add(startDialogTable).row();
    }

    /* Aufbau des Tables in dem der erste Tagebucheintrag gewählt wird */
    private void setupStartJournalEntryTable() {
        /* startJournalEntryTable */
        Table startJournalEntryTable = new Table();
        startJournalEntryTable.setName("startJournalEntryTable");
        startJournalEntryTable.pad(TABLE_OFFSET);

        /* StartJournalEntry Label */
        Label label_startJournalEntry = this.ui.addLabel(JOURNAL_STRING);
        label_startJournalEntry.setName("label_startJournalEntry");

        /* StartJournalEntry TextArea */
        LocTextArea textarea_startJournalEntry = this.ui.addLocTextArea("");
        textarea_startJournalEntry.setName("textarea_startJournalEntry");
        textarea_startJournalEntry.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if ( LANGUAGE_ENGLISH ) {
                    QuestEditorScreen.this.setLogText("Write an entry for the journal.");
                } else {
                    QuestEditorScreen.this.setLogText("Schreibe einen Tagebucheintrag.");
                }
            }
        });

        /* Hinzufügen */
        startJournalEntryTable.add(label_startJournalEntry).left().row();
        startJournalEntryTable.add(textarea_startJournalEntry).expand().prefWidth(Constants.WIDTH).prefHeight(JOURNAL_HEIGHT).row();
        this.questEditorLeftTable.add(startJournalEntryTable).row();

    }

    /* Aufbau des Tables in dem die Quest-Events gewählt werden */
    private void setupQuestEventSelectionTable() {
        /* questEventSelectionTable */
        Table questEventSelectionTable = new Table();
        questEventSelectionTable.setName("questEventSelectionTable");
        questEventSelectionTable.pad(TABLE_OFFSET);

        /* QuestEvents SelectBox */
        final SelectBox selectbox_questEventSelection = this.ui.addSelectBox(this.availableEvents, "selectbox_questEventSelection");
        selectbox_questEventSelection.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectbox_questEventSelection.setSelected(EVENT_SELECTION_HEADER);
                if ( LANGUAGE_ENGLISH ) {
                    QuestEditorScreen.this.setLogText("Choose a task for the quest.");
                } else {
                    QuestEditorScreen.this.setLogText("Wähle eine Aufgabe für die Quest aus.");
                }
            }
        });

        /* Hinzufügen */
        questEventSelectionTable.add(selectbox_questEventSelection).expandX().prefWidth(Constants.WIDTH);
        this.questEditorLeftTable.add(questEventSelectionTable).row();
    }

    /* Aufbau der rechten Hälfte des Questeditors */
    private void setupQuestEditorRightTable() {
        /* questEditorRightTable */
        this.questEditorRightTable = new Table();
        this.questEditorRightTable.setName("questEditorRightTable");
        this.questEditorRightTable.pad((float) TABLE_OFFSET);

        /* Hinzufügen */
        this.questEditorTable.add(this.questEditorRightTable);

        /* Die ScrollPane muss am Ende hinzugefügt werden */
        this.setupScrollPane();
    }

    /* Aufbau der ScrollPane in der rechten Hälfte des Questeditors */
    private void setupScrollPane() {
        /* ScrollPane */
        ScrollPane scrollPane = new ScrollPane(this.questEditorRightTable);
        scrollPane.setName("scrollPane");
        scrollPane.setScrollingDisabled(true, false);

        /* Hinzufügen */
        this.questEditorTable.add(scrollPane).width(RIGHT_SIDE_WIDTH).row();
    }

    /* Aufbau der unteren Menüleiste des Questeditors */
    private void setupQuestEditorMenuTable() {
        /* questEditorMenuTable */
        this.questEditorMenuTable = new Table();
        this.questEditorMenuTable.setName("questEditorMenuTable");
        this.questEditorMenuTable.pad((float) TABLE_OFFSET);
        this.questEditorMenuTable.setBackground(this.inactive);

        /* BackToEditor Button */
        Button buttonBackToEditor = this.ui.addButton(Constants.UI_ICON_BACK, 0, 0, "button_backToEditor");

        /* InfoLog Label */
        this.infoLog = this.ui.addLabel(WELCOME_MESSAGE);
        this.infoLog.setAlignment(Helper.Alignment.CENTER);

        /* SaveQuest Button */
        Button buttonSaveQuest = this.ui.addTextButton(SAVE_STRING, 0.0f, 0.0f, "button_saveQuest");

        /* Hinzufügen */
        this.questEditorMenuTable.add(buttonBackToEditor).pad((float) TABLE_OFFSET / 2.0f);
        this.questEditorMenuTable.add(this.infoLog).pad((float) TABLE_OFFSET / 2.0f).expandX().prefWidth(Constants.WIDTH);
        this.questEditorMenuTable.add(buttonSaveQuest).pad((float) TABLE_OFFSET / 2.0f);
        this.mainTable.add(this.questEditorMenuTable).expand().bottom().row();
    }

    /* Aufbau eines EventTables */
    public void setupQuestEventTable(String selectedEvent) {
        /* questEventTable */
        final Table questEventTable = new Table();
        questEventTable.setName("questEventTable");
        questEventTable.pad((float) TABLE_OFFSET);
        questEventTable.setBackground(this.inactive);
        questEventTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                QuestEditorScreen.this.activeQuestEventTable.setBackground(QuestEditorScreen.this.inactive);
                QuestEditorScreen.this.activeQuestEventTable = questEventTable;
                QuestEditorScreen.this.activeQuestEventTable.setSkin(QuestEditorScreen.this.ui.getSkin());
                QuestEditorScreen.this.activeQuestEventTable.setBackground(QuestEditorScreen.this.active);
            }
        });
        this.questEventTable = questEventTable;

        this.setupQuestEventHeaderTable(selectedEvent);
        this.setupQuestEventArgumentTable(selectedEvent);
        this.setupQuestEventJournalTable();
        this.setupQuestEventDialogTable();

        /* Hinzufügen */
        this.questEditorRightTable.add(this.questEventTable).row();

        if ( LANGUAGE_ENGLISH ) {
            this.setLogText("Fill the contextwindow which appeared on the right.");
        } else {
            this.setLogText("Fülle das rechts erschienene Kontextfenster aus.");
        }
    }

    /* QuestEvent-Table: Header-Sektion */
    public void setupQuestEventHeaderTable(String selectedEvent) {
        /* questEventHeaderTable */
        Table questEventHeaderTable = new Table();
        questEventHeaderTable.setName("questEventHeaderTable");
        questEventHeaderTable.pad((float) TABLE_OFFSET / 2.0f);

        /* QuestEventHeader Label */
        Label labelQuestEventHeader = this.ui.addLabel(selectedEvent);
        labelQuestEventHeader.setName("label_questEventHeader");

        /* Deletion Label */
        final Label labelDeletion = this.ui.addLabel(" X ");
        labelDeletion.setName("label_deletion");
        labelDeletion.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                QuestEditorScreen.this.activeDeletion = labelDeletion;
                QuestEditorScreen.this.activeDeletion.getParent().getParent().remove();
            }
        });

        /* Hinzufügen */
        questEventHeaderTable.add(labelQuestEventHeader).expandX().prefWidth(Constants.WIDTH).left();
        questEventHeaderTable.add(labelDeletion).padLeft((float) TABLE_OFFSET / 2.0f);
        this.questEventTable.add(questEventHeaderTable).row();
    }

    /* QuestEvent-Table: Argument-Sektion */
    public void setupQuestEventArgumentTable(String selectedEvent) {
        /* questEventArgumentTable */
        Table questEventArgumentTable = new Table();
        questEventArgumentTable.setName("questEventArgumentTable");
        questEventArgumentTable.pad((float) TABLE_OFFSET / 2.0f);

        /* Argument SelectBox */
        SelectBox selectboxArgument = null;
        if ( selectedEvent.equals(KILL_EVENT_STRING) ) {
            selectboxArgument = this.ui.addSelectBox(this.allMobs, "selectbox_argument");
        } else if ( selectedEvent.equals(FETCH_EVENT_STRING) ) {
            selectboxArgument = this.ui.addSelectBox(this.allItems, "selectbox_argument");
        } else if ( selectedEvent.equals(DIALOG_STARTED_STRING) ) {
            selectboxArgument = this.ui.addSelectBox(this.allNPCs, "selectbox_argument");
        } else {
            System.out.println("Fehler: Unbekanntes Event!");
        }

        /* Value TextField */
        TextField textfieldValue = this.ui.addTextField("");
        textfieldValue.setName("textfield_value");
        textfieldValue.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if ( LANGUAGE_ENGLISH ) {
                    QuestEditorScreen.this.setLogText("Choose a value.");
                } else {
                    QuestEditorScreen.this.setLogText("Wähle eine Anzahl.");
                }
            }
        });

        /* Hinzufügen */
        questEventArgumentTable.add(selectboxArgument).expandX().prefWidth(Constants.WIDTH);
        if ( selectedEvent.equals(KILL_EVENT_STRING) || selectedEvent.equals(FETCH_EVENT_STRING) ) {
            questEventArgumentTable.add(textfieldValue).expandX().prefWidth(Constants.WIDTH).row();
        }
        this.questEventTable.add(questEventArgumentTable).expandX().prefWidth(Constants.WIDTH).row();
    }

    /* QuestEvent-Table: Tagebuch-Sektion */
    private void setupQuestEventJournalTable() {
        /* questEventJournalTable */
        Table questEventJournalTable = new Table();
        questEventJournalTable.setName("questEventJournalTable");
        questEventJournalTable.pad((float) TABLE_OFFSET / 2.0f);

        /* Journal Label */
        Label labelJournal = this.ui.addLabel(JOURNAL_STRING);
        labelJournal.setName("label_journal");

        /* Journal TextArea */
        LocTextArea textareaJournal = this.ui.addLocTextArea("");
        textareaJournal.setName("textarea_journal");
        textareaJournal.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if ( LANGUAGE_ENGLISH ) {
                    QuestEditorScreen.this.setLogText("This entry shows up after completion of the task.");
                } else {
                    QuestEditorScreen.this.setLogText("Dieser Tagebucheintrag erscheint bei Erfüllung der Aufgabe.");
                }
            }
        });

        /* Hinzufügen */
        questEventJournalTable.add(labelJournal).left().row();
        questEventJournalTable.add(textareaJournal).expand().prefWidth(Constants.WIDTH).prefHeight(JOURNAL_HEIGHT).row();
        this.questEventTable.add(questEventJournalTable).row();
    }

    /* QuestEvent-Table: AddDialogButton-Sektion */
    private void setupQuestEventDialogTable() {
        /* questEventDialogTable */
        final Table questEventDialogTable = new Table();
        questEventDialogTable.setName("questEventDialogTable");
        questEventDialogTable.pad((float) TABLE_OFFSET / 2.0f);

        /* NewDialog TextButton */
        TextButton textbuttonNewDialog = this.ui.addTextButton(NEW_DIALOG_STRING, 0.0f, 0.0f, "textbutton_newDialog");
        textbuttonNewDialog.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                QuestEditorScreen.this.activeQuestEventTable.setBackground(QuestEditorScreen.this.inactive);
                QuestEditorScreen.this.activeQuestEventTable = (Table) questEventDialogTable.getParent();
                QuestEditorScreen.this.addDialogToQuestEvent();
            }
        });

        /* Hinzufügen */
        questEventDialogTable.add(textbuttonNewDialog).expandX().prefWidth(Constants.WIDTH).row();
        this.questEventTable.add(questEventDialogTable).row();
    }

    /* QuestEvent-Table: Aufbau eines hinzugefügten Dialog-Tables */
    private void addDialogToQuestEvent() {
        /* dialogTable */
        final Table dialogTable = new Table();
        dialogTable.setName("dialogTable");
        dialogTable.pad((float) TABLE_OFFSET / 2.0f);
        dialogTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                QuestEditorScreen.this.activeDialogTable = dialogTable;
            }
        });

        /* Dialog SelectBox */
        SelectBox selectboxSpeaker = this.ui.addSelectBox(this.allNPCs, "selectbox_speaker");
        selectboxSpeaker.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                QuestEditorScreen.this.activeDialogTable = dialogTable;
            }
        });

        /* CreateDialog Button */
        Button buttonCreatedialog = this.ui.addButton(Constants.UI_ICONS_PATH + "newdialog_icon_small.png", 0, 0, "button_createdialog");
        buttonCreatedialog.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                QuestEditorScreen.this.activeDialogTable = dialogTable;
                QuestEditorScreen.this.createNewDialog();
            }
        });

        /* DialogDeletion Label */
        final Label labelDialogDeletion = this.ui.addLabel(" X ");
        labelDialogDeletion.setName("label_dialogDeletion");
        labelDialogDeletion.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                QuestEditorScreen.this.activeDeletion = labelDialogDeletion;
                QuestEditorScreen.this.activeDeletion.getParent().remove();
            }
        });

        /* Dialog SelectBox */
        SelectBox selectboxDialog = this.ui.addSelectBox(new Array<String>(), "selectbox_dialog");

        /* Hinzufügen */
        dialogTable.add(selectboxSpeaker).expandX().prefWidth(Constants.WIDTH);
        dialogTable.add(buttonCreatedialog).padLeft((float) TABLE_OFFSET / 2.0f);
        dialogTable.add(labelDialogDeletion).padLeft((float) TABLE_OFFSET / 2.0f).row();
        dialogTable.add(selectboxDialog).padTop((float) TABLE_OFFSET / 2.0f).expandX().prefWidth(Constants.WIDTH);
        this.activeQuestEventTable.add(dialogTable).row();
    }

    /* Parsen, Kontrollieren, Speichern */
    public void saveQuest() {
        this.parseQuestEditor();
        if ( this.checkData() ) {
            StringWriter writer = new StringWriter();
            this.xmlWriter = new XmlWriter(writer);

            try {
                this.xmlWriter.element("Quest");
                this.saveQuestHeader();
                this.saveQuestEvents();
                this.xmlWriter.close();
            } catch ( IOException e ) {
                System.out.println("Fehler beim Speichern der Quest!");
                e.printStackTrace();
            }
            this.file = Gdx.files.local(Constants.PACKAGE_FOLDER + this.currentPackage + "/quests/" + this.quest.name.replace(" ", "_").toLowerCase() + ".xml");
            this.file.writeString(writer.toString(), false);

            if ( LANGUAGE_ENGLISH ) {
                this.setLogText("The quest was successfully saved!");
            } else {
                this.setLogText("Die Quest wurde erfolgreich gespeichert!");
            }
        }
    }

    /* Liest das gesamte UserInterface aus und erstellt daraus eine Quest */
    private Quest parseQuestEditor() {
        this.quest = new Quest();

        /* Die linke Seite des Questeditors wird ausgelesen */
        for ( Actor actor : this.questEditorLeftTable.getChildren() ) {
            Table table = (Table) actor;
            if ( table.getName().equals("questNameTable") ) {
                this.parseQuestName(table);
            }
            if ( table.getName().equals("startDialogTable") ) {
                this.parseStartDialog(table);
            }
            if ( table.getName().equals("startJournalEntryTable") ) {
                this.parseStartJournalEntry(table);
            }
        }

        /* Die rechte Seite des Questeditors wird ausgelesen */
        for ( Actor actor : this.questEditorRightTable.getChildren() ) {
            Table table = (Table) actor;
            this.parseQuestEvent(table);
        }
        return this.quest;
    }

    private void parseQuestName(Table table) {
        for ( Actor actor : table.getChildren() ) {
            if ( actor.getName().equals("textfield_questName") ) {
                TextField questName = (TextField) actor;
                this.quest.name = questName.getText();
            }
        }
    }

    private void parseStartDialog(Table table) {
        for ( Actor actor : table.getChildren() ) {
            if ( actor.getName().equals("selectbox_speaker") ) {
                SelectBox questgiver = (SelectBox) actor;
                this.quest.questgiver = questgiver.getSelected().toString();
            }
            if ( actor.getName().equals("selectbox_dialog") ) {
                SelectBox startDialog = (SelectBox) actor;
                try {
                    this.quest.startDialog =
                        "/npcs/" + this.quest.questgiver.toLowerCase() + "/" + startDialog.getSelected().toString().toLowerCase() + ".dialog";
                } catch ( Exception e ) {
                    this.quest.startDialog = "";
                }
            }
        }
    }

    private void parseStartJournalEntry(Table table) {
        this.quest.startJournalEntry = "";
        for ( Actor actor : table.getChildren() ) {
            if ( actor.getName().equals("textarea_startJournalEntry") ) {
                LocTextArea startJournalEntry = (LocTextArea) actor;
                this.quest.startJournalEntry = startJournalEntry.getText();
            }
        }
    }

    private void parseQuestEvent(Table table) {
        this.questEvent = new QuestEvent();
        this.questEvent.triggerEvent = false;

        for ( Actor actor : table.getChildren() ) {
            if ( actor.getName().equals("questEventHeaderTable") ) {
                this.parseHeaderTable((Table) actor);
            }
            if ( actor.getName().equals("questEventArgumentTable") ) {
                this.parseArgumentTable((Table) actor);
            }
            if ( actor.getName().equals("questEventJournalTable") ) {
                this.parseJournalTable((Table) actor);
            }
            if ( actor.getName().equals("questEventDialogTable") ) {
                /* Das QuestEvent schließt an diesem Punkt ab */
                /* Weitere Actors werden als eigene TriggerEvents behandelt */
                this.quest.eventList.add(this.questEvent);
            }
            if ( actor.getName().equals("dialogTable") ) {
                this.parseDialogTable((Table) actor);
            }
        }
    }

    private void parseHeaderTable(Table table) {
        for ( Actor actor : table.getChildren() ) {
            if ( actor.getName().equals("label_questEventHeader") ) {
                Label eventType = (Label) actor;
                this.questEvent.name = this.getEventTypeFromString(eventType.getText().toString()).toString();
            }
        }
    }

    private void parseArgumentTable(Table table) {
        for ( Actor actor : table.getChildren() ) {
            if ( actor.getName().equals("selectbox_argument") ) {
                SelectBox argument = (SelectBox) actor;
                this.questEvent.argument = argument.getSelected().toString();
            }
            if ( actor.getName().equals("textfield_value") ) {
                TextField value = (TextField) actor;
                this.questEvent.value = value.getText();
            }
        }
    }

    private void parseJournalTable(Table table) {
        this.questEvent.journalEntry = "";
        for ( Actor actor : table.getChildren() ) {
            if ( actor.getName().equals("textarea_journal") ) {
                LocTextArea journal = (LocTextArea) actor;
                this.questEvent.journalEntry = journal.getText();
            }
        }
    }

    private void parseDialogTable(Table table) {
        this.questEvent = new QuestEvent();
        this.questEvent.triggerEvent = true;
        this.questEvent.name = EventSystem.EventType.ADD_DIALOG.toString();

        for ( Actor actor : table.getChildren() ) {
            if ( actor.getName().equals("selectbox_speaker") ) {
                SelectBox argument = (SelectBox) actor;
                this.questEvent.argument = argument.getSelected().toString().replace(" ", "_");
            }
            if ( actor.getName().equals("selectbox_dialog") ) {
                SelectBox dialog = (SelectBox) actor;
                try {
                    this.quest.dialogs.add(("/npcs/" + this.questEvent.argument.toLowerCase() + "/" + dialog.getSelected()
                                                                                                            .toString()
                                                                                                            .replace(" ", "_")
                                                                                                            .toLowerCase() + ".dialog"));
                } catch ( Exception e ) {
                    this.quest.dialogs.add("");
                }
            }
        }
        this.quest.eventList.add(this.questEvent);
    }

    /* Kontrolliert alle Nutzereingaben auf Gültigkeit */
    private boolean checkData() {
        /* Kontrolliert ob die Quest einen Namen hat */
        if ( this.quest.name.isEmpty() ) {
            if ( LANGUAGE_ENGLISH ) {
                this.setLogText("Please choose a name for your quest!");
            } else {
                this.setLogText("Wähle einen Namen für die Quest!");
            }
            return false;
        }

        /* Kontrolliert ob der Quest überhaupt Events hinzugefügt worden sind */
        if ( this.quest.eventList.isEmpty() ) {
            if ( LANGUAGE_ENGLISH ) {
                this.setLogText("The quest has no tasks.");
            } else {
                this.setLogText("Die Quest hat keine Aufgaben.");
            }
            return false;
        }

        for ( int i = 0; i < this.quest.eventList.size(); i++ ) {
            QuestEvent questEvent = this.quest.eventList.get(i);

            /* Kontrolliert ob die QuestEvent-Argumente alle gültig sind */
            if ( questEvent.argument.equals(MOB_SELECTION_HEADER) || questEvent.argument.equals(NPC_SELECTION_HEADER) || questEvent.argument.equals(
                ITEM_SELECTION_HEADER) ) {
                if ( LANGUAGE_ENGLISH ) {
                    this.setLogText("You have to fill all contextwindows!");
                } else {
                    this.setLogText("Du musst alle Kontextfenster ausfüllen!");
                }
                return false;
            }

            /* Kontrolliert ob die eingetragenen Anzahlen gültig sind */
            if ( questEvent.name.equals(EventSystem.EventType.KILL_EVENT.toString()) || questEvent.name.equals(EventSystem.EventType.FETCH_EVENT.toString()) ) {
                try {
                    int num = Integer.parseInt(questEvent.value);
                    if ( num < 1 ) {
                        if ( LANGUAGE_ENGLISH ) {
                            this.setLogText("You inserted invalid values!");
                        } else {
                            this.setLogText("Du hast ungültige Anzahlen eingeben!");
                        }
                        return false;
                    }
                } catch ( NumberFormatException e ) {
                    if ( LANGUAGE_ENGLISH ) {
                        this.setLogText("You inserted invalid values!");
                    } else {
                        this.setLogText("Du hast ungültige Anzahlen eingeben!");
                    }
                    return false;
                }
            }
        }

        /* Kontrolliert ob die Pfade der Dialoge gültig sind */
        if ( this.quest.startDialog.equals("") | this.quest.startDialog.equals(Constants.PACKAGE_FOLDER
                                                                               + this.currentPackage
                                                                               + "/npcs/"
                                                                               + this.quest.questgiver
                                                                               + "/"
                                                                               + DIALOG_SELECTION_HEADER
                                                                               + ".dialog") ) {
            if ( LANGUAGE_ENGLISH ) {
                this.setLogText("You forgot to choose a dialog!");
            } else {
                this.setLogText("Du hast vergessen Dialoge auszuwählen!");
            }
            return false;
        }

        for ( int j = 0; j < this.quest.dialogs.size(); j++ ) {
            String dialog = this.quest.dialogs.get(j);
            if ( dialog.equals("") | dialog.equals(Constants.PACKAGE_FOLDER
                                                   + this.currentPackage
                                                   + "/npcs/"
                                                   + this.questEvent.argument
                                                   + "/"
                                                   + DIALOG_SELECTION_HEADER
                                                   + ".dialog") ) {
                if ( LANGUAGE_ENGLISH ) {
                    this.setLogText("You forgot to choose a dialog!");
                } else {
                    this.setLogText("Du hast vergessen Dialoge auszuwählen!");
                }
                return false;
            }
        }
        return true;
    }

    /* Speichert den Questnamen und den jeweils ersten Dialog und Tagebucheintrag */
    private void saveQuestHeader() throws IOException {
        this.xmlWriter.element("QuestName").text(this.quest.name.replace(" ", "_")).pop();

        this.xmlWriter.element("Description").text(this.quest.startJournalEntry).pop();

        this.xmlWriter.element("Dialog").text(this.quest.startDialog.replace(" ", "_")).pop();
    }

    /* Speichert alle QuestEvents */
    private void saveQuestEvents() throws IOException {
        this.xmlWriter.element("QuestEvents");

        int dialogCounter = 0;
        for ( int i = 0; i < this.quest.eventList.size(); i++ ) {
            if ( this.quest.eventList.get(i).triggerEvent ) {
                this.xmlWriter.element("TriggerEvent");
            } else {
                this.xmlWriter.element("QuestEvent").element("Description").text(this.quest.eventList.get(i).journalEntry).pop().element("GoalEvent");
                if ( this.quest.eventList.get(i).name.equals(EventSystem.EventType.KILL_EVENT.toString())
                     | this.quest.eventList.get(i).name.equals(EventSystem.EventType.FETCH_EVENT.toString()) ) {
                    this.xmlWriter.attribute("Anzahl", this.quest.eventList.get(i).value);
                }
            }

            this.xmlWriter.element("Event").element("Type").text(this.quest.eventList.get(i).name).pop().element("Argument");

            this.xmlWriter.text(this.quest.eventList.get(i).argument).pop();

            if ( this.quest.eventList.get(i).name.equals(EventSystem.EventType.ADD_DIALOG.toString()) ) {
                this.xmlWriter.element("Argument").text(this.quest.dialogs.get(dialogCounter).replace(" ", "_")).pop();
                dialogCounter++;
            }

            this.xmlWriter.pop().pop();

            if ( i != this.quest.eventList.size() - 1 ) {
                if ( !this.quest.eventList.get(i + 1).triggerEvent ) {
                    this.xmlWriter.pop();
                }
            } else {
                this.xmlWriter.pop();
            }
        }
        this.xmlWriter.pop();
    }

    /* Erstellt mit Hilfe des Dialogeditors einen neuen Dialog */
    private void createNewDialog() {
        String speaker = this.getSelectedSpeaker();
        if ( !speaker.equals(NPC_SELECTION_HEADER) ) {
            this.dialogEditor = new DialogEditorScreen(this.getGame(), this.editor, this);
            this.getGame().setScreen(this.dialogEditor);
        } else {
            if ( LANGUAGE_ENGLISH ) {
                this.setLogText("You have to choose a speaker!");
            } else {
                this.setLogText("Du musst zuerst einen Sprecher wählen!");
            }
        }
    }

    /* Diese Funktion wird am Anfang der Dialogerstellung vom DialogEditor aus aufgerufen */
    /* Der DialogEditor benötigt den Namen des aktuell gewählten Sprechers */
    public String getSpeakerFromQuestEditor() {
        return this.getSelectedSpeaker();
    }

    /* Diese Funktion wird am Ende der Dialogerstellung vom DialogEditor aus aufgerufen */
    /* Die Dialog-Selectbox des aktuell aktiven Dialog-Tables wird aktualisiert */
    /* Die Dialog-Selectbox zeigt den eben erstellten Dialog an */
    public void addDialog(String path) {
        String activeSpeaker = this.getSelectedSpeaker();
        this.updateActiveDialogSelectBox(activeSpeaker);
        path = path.substring("/npcs/".length() + activeSpeaker.length() + 1).replace(".dialog", "");
        for ( Actor actor : this.activeDialogTable.getChildren() ) {
            if ( actor.getName().equals("selectbox_dialog") ) {
                SelectBox activeDialog = (SelectBox) actor;
                activeDialog.setSelected(path);
            }
        }
    }

    /* Liefert den ausgewählten Dialogsprecher vom aktuell aktiven Dialog-Table */
    private String getSelectedSpeaker() {
        String speaker = "";
        for ( Actor actor : this.activeDialogTable.getChildren() ) {
            if ( actor.getName().equals("selectbox_speaker") ) {
                SelectBox selectedSpeaker = (SelectBox) actor;
                speaker = selectedSpeaker.getSelected().toString().toLowerCase();
            }
        }
        return speaker;
    }

    /* Aktualisiert die Dialog-Selectbox des aktuell aktiven Dialog-Tables */
    /* Die Dialog-Selectbox bekommt alle Dialoge des übergegebenen NPCs als Inhalt */
    public void updateActiveDialogSelectBox(String selectedSpeaker) {
        /* Füllt ein Array mit den Dialogen des ausgewählten Sprechers */
        Array<String> speakerDialogs = new Array<String>();
        speakerDialogs.add(DIALOG_SELECTION_HEADER);
        FileHandle npcRootFolder = Gdx.files.local(Constants.PACKAGE_FOLDER + this.currentPackage + "/npcs/");
        for ( FileHandle npcFolder : npcRootFolder.list() ) {
            if ( npcFolder.isDirectory() && npcFolder.name().equals(selectedSpeaker.replace(" ", "_").toLowerCase()) ) {
                for ( FileHandle dialog : npcFolder.list() ) {
                    if ( dialog.extension().equals("dialog") ) {
                        speakerDialogs.add(dialog.nameWithoutExtension().replace("_", " "));
                    }
                }
            }
        }
        /* Aktualisiert den Inhalt der Dialog-SelectBox mit dem des eben befüllten Arrays */
        for ( Actor actor : this.activeDialogTable.getChildren() ) {
            if ( actor.getName().equals("selectbox_dialog") ) {
                SelectBox selectbox_dialog = (SelectBox) actor;
                selectbox_dialog.setItems(speakerDialogs);
                this.activeDialogTable.add(selectbox_dialog).expandX().prefWidth(Constants.WIDTH).row();
            }
        }
    }

    /* Aktualisiert das Informationsfenster */
    private void setLogText(String text) {
        this.infoLog.clear();
        this.infoLog.setText(text);
    }

    /* Wandelt den in den Selectboxen verwendeten String in den dazugehörigen EventType um */
    private EventSystem.EventType getEventTypeFromString(String eventName) {
        EventSystem.EventType eventType = null;
        for ( Map.Entry<EventSystem.EventType, String> entry : this.eventMap.entrySet() ) {
            if ( entry.getValue().equals(eventName) ) {
                eventType = entry.getKey();
            }
        }
        return eventType;
    }

    public class Quest {
        public Quest() {
            this.dialogs = new ArrayList<>();
            this.eventList = new ArrayList<>();
        }

        String name;
        String questgiver;
        String startDialog;
        String startJournalEntry;
        ArrayList<String> dialogs;
        ArrayList<QuestEvent> eventList;
    }

    private class QuestEvent {
        boolean triggerEvent;
        String name;
        String argument;
        String value;
        String journalEntry;
    }
}
