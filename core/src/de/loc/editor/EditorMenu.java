package de.loc.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.XmlReader;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import de.loc.input.userinterface.LandsOfCinderWindow;
import de.loc.input.userinterface.UserInterface;
import de.loc.tools.Constants;
import de.loc.tools.ListItem;
import de.loc.tools.Pair;
import de.loc.tools.XmlHelper;

public class EditorMenu extends LandsOfCinderWindow {
    protected UserInterface ui;
    protected Table mainTable;
    private Table editormenuTable;
    private Table flagContainerTable;
    private Table entryContainerTable;
    private Table mouseOverTable;
    private Table activeFlag;
    private Table activeEntry;

    private NinePatchDrawable flag_active;
    private NinePatchDrawable flag_inactive;
    private NinePatchDrawable entry_active;
    private NinePatchDrawable entry_inactive;
    private NinePatchDrawable editormenu_style;
    private NinePatchDrawable mouseover_style;
    private List<Pair<NinePatchDrawable, String>> iconList;
    private float entrySize;
    private float entryIconSize;
    private float entryOffset;
    private int flagSize;
    private boolean over, cancelled;

    /* Breite und Höhe des EditorMenüs */
    private static final float MENU_WIDTH = Constants.WIDTH * 0.75f;
    private static final float MENU_HEIGHT = Constants.HEIGHT * 0.85f;

    /* Abstand zwischen den Reitern */
    private static final int FLAG_OFFSET = 5;

    /* Größe der Einträge */
    private static final float ENTRY_SIZE_VERY_SMALL = Constants.WIDTH / 14.0f;
    private static final float ENTRY_SIZE_SMALL = Constants.WIDTH / 12.0f;
    private static final float ENTRY_SIZE_NORMAL = Constants.WIDTH / 10.0f;
    private static final float ENTRY_SIZE_LARGE = Constants.WIDTH / 8.0f;

    /* Abstände */
    private static final float ENTRY_TO_ICON_OFFSET = 0.2f;
    private static final float ENTRY_TO_ENTRY_OFFSET = 0.05f;

    /* Größe der Reiter */
    private static final int FLAG_SIZE_SMALL = (int) (50.0f * 0.75f);
    private static final int FLAG_SIZE_LARGE = 50;

    /* Konstruktor */
    public EditorMenu(UserInterface ui) {
        super(ui.getSkin());
        this.ui = ui;

        this.setupEditorUI();
        this.createEditorUI();
    }

    /* Aufbau des EditorMenüs */
    private void setupEditorUI() {
        /* Breite und Höhe des EditorMenus */
        this.setWidth(MENU_WIDTH);
        this.setHeight(MENU_HEIGHT);
        this.setX(Constants.WIDTH / 2.0f - MENU_WIDTH / 2.0f);
        this.setY(Constants.HEIGHT / 2.0f - MENU_HEIGHT / 2.3f);

        /* MainTable */
        this.mainTable = new Table();
        this.mainTable.setName("mainTable");
        this.mainTable.setFillParent(true);

        /* mainScrollPane */
        ScrollPane mainScrollPane = new ScrollPane(this.mainTable, this.getSkin());
        mainScrollPane.setName("Main Scrollpane");
        mainScrollPane.setScrollingDisabled(true, true);
        this.add(mainScrollPane).width(this.getWidth()).height(this.getHeight());

        /* Aktives Item */
        this.activeEntry = new Table();
        this.activeEntry.setName("");

        /* Aktiver Reiter */
        this.activeFlag = new Table();
        this.activeFlag.setName("");

        /* Icons */
        this.iconList = new ArrayList<>();
        this.iconList.add(new Pair<>(new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_ICON_NPCS)), "flag_player_and_npc"));
        this.iconList.add(new Pair<>(new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_ICON_MOBS)), "flag_mob"));
        this.iconList.add(new Pair<>(new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_ICON_ITEMS)), "flag_item"));
        this.iconList.add(new Pair<>(new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_ICON_CONSUMABLES)), "flag_consumable"));
        this.iconList.add(new Pair<>(new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_ICON_EQUIPPABLES)), "flag_equippable"));
        this.iconList.add(new Pair<>(new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_ICON_OBJECTS)), "flag_object"));
        this.iconList.add(new Pair<>(new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_ICON_CHEST)), "flag_chest"));
        this.iconList.add(new Pair<>(new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_ICON_EMPTIES)), "flag_empty"));
        this.iconList.add(new Pair<>(new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_ICON_QUESTS)), "flag_quest"));
        this.iconList.add(new Pair<>(new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_ICON_DIALOGS)), "flag_dialog"));
        this.iconList.add(new Pair<>(new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_ICON_SETTINGS)), "flag_settings"));
        this.iconList.add(new Pair<>(new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_ICON_MAINMENU)), "flag_mainmenu"));

        /* Entry/Icon Size */
        if ( Constants.WIDTH >= 960.0f && Constants.HEIGHT >= 540.0f ) {
            this.entrySize = ENTRY_SIZE_SMALL;
        } else if ( Constants.WIDTH >= 1280.0f && Constants.HEIGHT >= 720.0f ) {
            this.entrySize = ENTRY_SIZE_NORMAL;
        } else if ( Constants.WIDTH >= 1920.0f && Constants.HEIGHT >= 1080.0f ) {
            this.entrySize = ENTRY_SIZE_LARGE;
        } else {
            this.entrySize = ENTRY_SIZE_VERY_SMALL;
        }
        this.entryIconSize = this.entrySize - (this.entrySize * ENTRY_TO_ICON_OFFSET);
        this.entryOffset = this.entrySize * ENTRY_TO_ENTRY_OFFSET;

        /* Flag Size */
        if ( Constants.WIDTH >= 1280.0f && Constants.HEIGHT >= 720.0f ) {
            this.flagSize = FLAG_SIZE_LARGE;
        } else {
            this.flagSize = FLAG_SIZE_SMALL;
        }

        /* Sonstiges */
        this.flag_active = new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_FLAG_ACTIVE));
        this.flag_inactive = new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_FLAG_INACTIVE));
        this.entry_active = new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_QUICKSLOT_ACTIVE));
        this.entry_inactive = new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_QUICKSLOT_INACTIVE));
        this.editormenu_style = new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_EDITORMENU));
        this.mouseover_style = new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_MOUSEOVER));
    }

    /* Erstellung des EditorMenus */
    private void createEditorUI() {
        /* editormenuTable */
        this.editormenuTable = new Table();
        this.editormenuTable.setName("editormenuTable");

        /* Reiter-Container-Table */
        this.flagContainerTable = new Table();
        this.flagContainerTable.setName("flagContainerTable");

        /* Reiter */
        for ( Pair<NinePatchDrawable, String> ninePatchDrawableStringPair : this.iconList ) {
            NinePatchDrawable drawable = ninePatchDrawableStringPair.getLeft();
            String handleID = ninePatchDrawableStringPair.getRight();
            this.addFlag(drawable, handleID);
        }

        /* entryContainerTable */
        this.entryContainerTable = new Table();
        this.entryContainerTable.setName("entryContainerTable");
        this.entryContainerTable.setBackground(this.editormenu_style);

        /* mouseOverTable */
        this.mouseOverTable = new Table();
        this.mouseOverTable.setName("mouseOverTable");
        this.mouseOverTable.setBackground(this.mouseover_style);

        /* Hinzufügen */
        this.editormenuTable.add(this.entryContainerTable).expand().prefWidth(Constants.WIDTH).prefHeight(MENU_HEIGHT);
        this.mainTable.add(this.flagContainerTable).left().row();
        this.mainTable.add(this.editormenuTable);

        /* ScrollPane */
        ScrollPane scrollpane = new ScrollPane(this.entryContainerTable, this.getSkin());
        scrollpane.setName("scrollpane");
        scrollpane.setScrollingDisabled(false, false);
        this.editormenuTable.add(scrollpane).width(MENU_WIDTH).prefHeight(MENU_HEIGHT).row();
    }

    /* Fügt einen Reiter zum bestehenden Reiter-Container hinzu */
    private void addFlag(NinePatchDrawable drawable, String handleID) {
        /* Reiter-Table */
        Table flagTable = new Table();
        flagTable.setName("flagTable");
        flagTable.setBackground(this.flag_inactive);

        /* Reiter-Bild */
        final Image flagImage = new Image();
        flagImage.setName(handleID);
        flagImage.setDrawable(drawable);
        this.addFlagListener(flagImage, flagTable);
        flagImage.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if ( (pointer == -1 && !EditorMenu.this.cancelled) ) {
                    EditorMenu.this.over = true;

                    /* Flag Info */
                    String flagInfo = "";
                    if ( flagImage.getName().equals("flag_player_and_npc") ) {
                        flagInfo = "Spieler und NPCs";
                    } else if ( flagImage.getName().equals("flag_mob") ) {
                        flagInfo = "Gegner";
                    } else if ( flagImage.getName().equals("flag_item") ) {
                        flagInfo = "Werkzeug und Sonstiges";
                    } else if ( flagImage.getName().equals("flag_consumable") ) {
                        flagInfo = "Verpflegung";
                    } else if ( flagImage.getName().equals("flag_equippable") ) {
                        flagInfo = "Ausrüstung";
                    } else if ( flagImage.getName().equals("flag_object") ) {
                        flagInfo = "Bäume, Steine, Möbel, etc.";
                    } else if ( flagImage.getName().equals("flag_chest") ) {
                        flagInfo = "Behälter, Truhen, Schränke, etc.";
                    } else if ( flagImage.getName().equals("flag_empty") ) {
                        flagInfo = "Unsichtbare Objekte, Levelwechsel, Lichtquellen, etc.";
                    } else if ( flagImage.getName().equals("flag_quest") ) {
                        flagInfo = "Questeditor";
                    } else if ( flagImage.getName().equals("flag_dialog") ) {
                        flagInfo = "NPC-Editor";
                    } else if ( flagImage.getName().equals("flag_settings") ) {
                        flagInfo = "Licht, Schatten Musik, Gitter, etc.";
                    } else if ( flagImage.getName().equals("flag_mainmenu") ) {
                        flagInfo = "Menü";
                    } else {
                        System.out.println("Unbekannter Flag!");
                    }
                    Label label_flagInfo = EditorMenu.this.ui.addLabel(flagInfo);

                    /* Style des MouseOver-Tables */
                    label_flagInfo.setColor(com.badlogic.gdx.graphics.Color.WHITE);

                    /* Größe des MouseOver-Tables an Informationen anpassen */
                    EditorMenu.this.mouseOverTable.setSize(label_flagInfo.getWidth() + 20.0f, label_flagInfo.getHeight());

                    /* Setzen der Position des MouseOver-Tables */
                    EditorMenu.this.mouseOverTable.setPosition((float) Gdx.input.getX(), Constants.HEIGHT - (float) Gdx.input.getY());

                    /* Vorhandene Informationen hinzufügen */
                    EditorMenu.this.mouseOverTable.add(label_flagInfo).expand().left().row();

                    EditorMenu.this.ui.add(EditorMenu.this.mouseOverTable);
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if ( (pointer == -1 && !EditorMenu.this.cancelled) ) {
                    EditorMenu.this.over = false;
                    EditorMenu.this.mouseOverTable.clearChildren();
                    EditorMenu.this.ui.remove(EditorMenu.this.mouseOverTable);
                }
            }
        });

        /* Hinzufügen */
        flagTable.add(flagImage).size((float) this.flagSize, (float) this.flagSize);
        this.flagContainerTable.add(flagTable).padRight((float) FLAG_OFFSET);
    }

    public void showItems(String listPath) {
        ArrayList<ListItem> currentEntityList = XmlHelper.parseXmlList(listPath);
        this.setContent(currentEntityList);
    }

    /* Parsen des Listeneintrags und anschließende Erstellung */
    public void setContent(ArrayList<ListItem> listItems) {
        this.entryContainerTable.clearChildren();
        for ( int i = 0; i < listItems.size(); i++ ) {
            ListItem item = listItems.get(i);
            XmlReader.Element node = item.xmlElement;
            String listType = node.getName();

            String description = "";
            if ( node.getChildByName("Description") != null ) {
                description = node.getChildByName("Description").getText();
            } else if ( node.getChildByName("Health") != null ) {
                description = "Health = " + node.getChildByName("Health").getText();
            }

            String stat = "";
            if ( node.getChildByName("Stat") != null ) {
                String[] statParts = node.getChildByName("Stat").getText().split(",");
                stat = statParts[0].toLowerCase();
                stat = Character.toString(stat.charAt(0)).toUpperCase() + stat.substring(1) + " +" + statParts[1];
            } else if ( node.getChildByName("HP") != null ) {
                stat = "Health = " + node.getChildByName("HP").getText();
            } else if ( node.getChildByName("Attack") != null ) {
                stat = "Attack = " + node.getChildByName("Attack").getText();
            }

            if ( node.getChildByName("Armour") != null ) {
                stat += "\n" + "Armour = " + node.getChildByName("Armour").getText();
            }

            /* Hinzufügen des Eintrags */
            this.addEntry(item.name, description, Constants.ICON_PATH + item.icon, stat, "List_" + listType + "_" + i);
        }
    }

    public void setMenuAsContent(Table table) {
        this.entryContainerTable.clearChildren();
        this.entryContainerTable.add(table);
    }

    /* Fügt dem Hauptfenster einen neuen Eintrag hinzu */
    public void addEntry(final String name, final String description, String iconPath, final String itemStat, String handleID) {
        /* entryTable */
        final Table entryTable = new Table();
        entryTable.setName(handleID);
        entryTable.setBackground(this.entry_inactive);

        /* entryIcon */
        Button entryIcon = new Button(new TextureRegionDrawable(new TextureRegion(this.ui.getAsset(iconPath, Texture.class))));
        entryIcon.setSkin(this.getSkin());
        entryIcon.setName(handleID);
        entryIcon.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if ( (pointer == -1 && !EditorMenu.this.cancelled) ) {
                    EditorMenu.this.over = true;
                    EditorMenu.this.activateEntry(entryTable);

                    /* Kontrolle vorhandener Informationen */
                    boolean nameAvailable = false;
                    boolean descriptionAvailable = false;
                    boolean itemStatAvailable = false;

                    String nameLabel = name;
                    String descriptionLabel = description;
                    if ( !name.isEmpty() ) {
                        nameAvailable = true;
                    }
                    if ( !description.isEmpty() ) {
                        descriptionAvailable = true;
                        nameLabel += "\n";
                    }
                    if ( !itemStat.isEmpty() ) {
                        itemStatAvailable = true;
                        descriptionLabel += "\n";
                    }

                    /* Label */
                    Label entryName = EditorMenu.this.ui.addLabel(nameLabel);
                    Label entryDescription = EditorMenu.this.ui.addLabel(descriptionLabel);
                    Label entryStats = EditorMenu.this.ui.addLabel(itemStat);

                    /* Style des MouseOver-Tables */
                    entryName.setColor(com.badlogic.gdx.graphics.Color.WHITE);
                    entryDescription.setColor(Color.LIGHT_GRAY);
                    entryStats.setColor(com.badlogic.gdx.graphics.Color.GREEN);

                    /* Größe des MouseOver-Tables an Informationen anpassen */
                    float mouseoverOffset = 20.0f;
                    float longestLabel = entryName.getWidth() + mouseoverOffset;
                    if ( entryDescription.getWidth() > longestLabel ) {
                        longestLabel = entryDescription.getWidth() + mouseoverOffset;
                    } else if ( entryStats.getWidth() > longestLabel ) {
                        longestLabel = entryStats.getWidth() + mouseoverOffset;
                    }
                    EditorMenu.this.mouseOverTable.setSize(longestLabel, (entryName.getHeight() + entryDescription.getHeight() + entryStats.getHeight()));

                    /* Setzen der Position des MouseOver-Tables */
                    EditorMenu.this.mouseOverTable.setPosition((float) Gdx.input.getX(), Constants.HEIGHT - (float) Gdx.input.getY());

                    /* Vorhandene Informationen hinzufügen */
                    if ( nameAvailable ) {
                        EditorMenu.this.mouseOverTable.add(entryName).expand().left().row();
                    }
                    if ( descriptionAvailable ) {
                        EditorMenu.this.mouseOverTable.add(entryDescription).expand().left().row();
                    }
                    if ( itemStatAvailable ) {
                        EditorMenu.this.mouseOverTable.add(entryStats).expand().left().row();
                    }
                    EditorMenu.this.ui.add(EditorMenu.this.mouseOverTable);
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if ( (pointer == -1 && !EditorMenu.this.cancelled) ) {
                    EditorMenu.this.over = false;
                    EditorMenu.this.mouseOverTable.clearChildren();
                    EditorMenu.this.ui.remove(EditorMenu.this.mouseOverTable);
                }
            }
        });
        entryTable.add(entryIcon).width(this.entryIconSize).height(this.entryIconSize);

        /* Berechnung der Anzahl an Menüeinträgen */
        int entriesPerRow = (int) ((this.entryContainerTable.getWidth()) / (this.entrySize + this.entryOffset));

        this.entryContainerTable.top().left();
        if ( (this.entryContainerTable.getCells().size != 0) && ((this.entryContainerTable.getCells().size + 1) % entriesPerRow) == 0 ) {
            this.entryContainerTable.add(entryTable).pad(this.entryOffset / 2).size(this.entrySize, this.entrySize).row();
        } else {
            this.entryContainerTable.add(entryTable).pad(this.entryOffset / 2).size(this.entrySize, this.entrySize);
        }
        this.addEntryListener(entryIcon);
    }

    /* Erstellt ein Settings UI */
    private void createSettingsUI() {

    }

    /* Aktiviert den übergebenen Reiter-Table */
    private void activateFlag(Table flagTable) {
        this.activeFlag.setBackground(this.flag_inactive);
        this.activeFlag = flagTable;
        this.activeFlag.setBackground(this.flag_active);
    }

    /* Aktiviert den übergebenen Entry-Table */
    private void activateEntry(Table entryTable) {
        this.activeEntry.setBackground(this.entry_inactive);
        this.activeEntry = entryTable;
        this.activeEntry.setBackground(this.entry_active);
    }

    /* ClickListener für Flags */
    protected void addFlagListener(final Actor actor, final Table table) {
        if ( actor.getName() != null ) {
            actor.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    EditorMenu.this.activateFlag(table);
                    EditorMenu.this.entryContainerTable.clearChildren();
                    EditorMenu.this.ui.getInputHandler().handle(actor.getName());
                }
            });
        } else {
            Gdx.app.log("EDITOR", "Fehler: Es wurde kein Clicklistener hinzugefügt!");
        }
    }

    /* ClickListener für Entrys */
    protected void addEntryListener(final Actor actor) {
        if ( actor.getName() != null ) {
            actor.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    EditorMenu.this.activateEntry((Table) actor);
                    EditorMenu.this.ui.getInputHandler().handle(actor.getName());
                    EditorMenu.this.hide();
                }
            });
        } else {
            Gdx.app.log("EDITOR", "Fehler: Es wurde kein Clicklistener hinzugefügt!");
        }
    }

    public void reload(String handleID) {
        this.entryContainerTable.clearChildren();

        handleID = handleID.substring(5);
        StringTokenizer tokenizer = new StringTokenizer(handleID, "_");
        String listType = tokenizer.nextToken().toLowerCase();
        String listIndex = tokenizer.nextToken();

        if ( listType.contains("npc") || listType.contains("player") ) {
            listType = "player_and_npc";
        }
        this.ui.getInputHandler().handle("flag_" + listType);
    }

    public boolean isAscendantOfTable(Actor actor) {
        return this.entryContainerTable.isAscendantOf(actor);
    }

    public void hide() {
        this.setVisible(false);
    }

    public void show() {
        this.setVisible(true);
    }

    public void toggle() {
        this.setVisible(!this.isVisible());
    }
}
