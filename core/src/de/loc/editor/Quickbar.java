package de.loc.editor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import de.loc.input.userinterface.LandsOfCinderWindow;
import de.loc.input.userinterface.UserInterface;
import de.loc.tools.Constants;

public class Quickbar extends LandsOfCinderWindow {
    protected UserInterface ui;
    protected Table mainTable;
    private final int numberOfSlots;
    private float slotSize;
    private float slotIconSize;
    private boolean over, cancelled;

    private Table activeQuickslot;
    private NinePatchDrawable quickbar_inactive;
    private NinePatchDrawable quickbar_active;
    private NinePatchDrawable quickbar_image_empty;

    private static final float SLOT_SIZE_SMALL = Constants.WIDTH / 18;
    private static final float SLOT_SIZE_NORMAL = Constants.WIDTH / 16;
    private static final float SLOT_SIZE_LARGE = Constants.WIDTH / 15;

    /* Abstände */
    private static final float SLOT_TO_SLOT_OFFSET = 1;
    private static final float SLOT_TO_ICON_OFFSET = 0.2f;

    /* Konstruktor */
    public Quickbar(UserInterface ui, int numberOfSlots) {
        super(ui.getSkin());
        this.ui = ui;
        this.numberOfSlots = numberOfSlots;

        this.setupQuickbar();
        this.createQuickbar();
    }

    /* Aufbau der Quickbar */
    private void setupQuickbar() {
        /* Breite und Höhe der Quickbar */
        this.setWidth(Constants.WIDTH);
        this.setHeight(Constants.HEIGHT * 0.12f);

        /* MainTable */
        this.mainTable = new Table();
        this.mainTable.setName("mainTable");

        /* ScrollPane */
        ScrollPane scrollpane = new ScrollPane(this.mainTable, this.getSkin());
        scrollpane.setScrollingDisabled(false, true);
        this.add(scrollpane).width(this.getWidth()).height(this.getHeight());

        /* Aktive Tables */
        this.activeQuickslot = new Table();
        this.activeQuickslot.setName("");

        /* Größe der Slots */
        if ( Constants.WIDTH >= 1280.0f && Constants.HEIGHT >= 720.0f ) {
            this.slotSize = SLOT_SIZE_NORMAL;
        } else if ( Constants.WIDTH >= 1920.0f && Constants.HEIGHT >= 1080.0f ) {
            this.slotSize = SLOT_SIZE_LARGE;
        } else {
            this.slotSize = SLOT_SIZE_SMALL;
        }
        this.slotIconSize = this.slotSize - SLOT_TO_ICON_OFFSET;

        /* Sonstiges */
        this.quickbar_inactive = new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_QUICKSLOT_INACTIVE));
        this.quickbar_active = new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_QUICKSLOT_ACTIVE));
        this.quickbar_image_empty = new NinePatchDrawable(this.ui.getNinePatch(Constants.UI_NINEPATCH_EMPTYITEM));
    }

    /* Erstellung der Quickbar */
    private void createQuickbar() {
        for ( int i = 0; i < this.numberOfSlots; i++ ) {
            this.addQuickslot();
        }
    }

    /* Fügt einen Quickslot zur bestehenden Quickbar hinzu */
    private void addQuickslot() {
        /* quickslotTable */
        final Table quickslotTable = new Table();
        quickslotTable.setName("quickslotTable");
        quickslotTable.setBackground(this.quickbar_inactive);
        quickslotTable.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if ( (pointer == -1 && !Quickbar.this.cancelled) ) {
                    Quickbar.this.over = true;
                    Quickbar.this.activateQuickslot(quickslotTable);
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if ( (pointer == -1 && !Quickbar.this.cancelled) ) {
                    Quickbar.this.over = false;
                }
            }
        });

        /* quickslotImage */
        Image quickslotImage = new Image();
        quickslotImage.setName("quickslotImage");
        quickslotImage.setDrawable(this.quickbar_image_empty);

        /* Hinzufügen */
        quickslotTable.add(quickslotImage).size(this.slotIconSize);
        this.mainTable.add(quickslotTable).pad(SLOT_TO_SLOT_OFFSET).size(this.slotSize);
    }

    /* Aktiviert den übergebenen Slot-Table */
    public void activateQuickslot(Table quickslotTable) {
        this.activeQuickslot.setBackground(this.quickbar_inactive);
        this.activeQuickslot = quickslotTable;
        this.activeQuickslot.setName("quickbarslotTable");
        this.activeQuickslot.setBackground(this.quickbar_active);
    }

    public void addItemToTable(Actor item) {
        this.activeQuickslot.clearChildren();
        this.activeQuickslot.add(item).size(this.slotIconSize);
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