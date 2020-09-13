package de.loc.input.userinterface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import de.loc.tools.Constants;

public class MenuWindow extends LandsOfCinderWindow {

    public enum Type {
        LEFT,
        RIGHT,
        MIDDLE,
        TOP,
        BOTTOM,
        COMBATBASE,
        COMBATSKILLS,
        HEALTHBAR,
        STEAMMETER,
        SWITCHER,
        LISTVIEW
    }

    protected ScrollPane scroll;
    protected Table mainTable;

    protected UserInterface ui;
    private final Type type;

    //For debugging purposes
    private final boolean debugging = false;

    public MenuWindow(UserInterface ui, Type type) {
        super(ui.getSkin());
        this.ui = ui;
        this.type = type;

        this.mainTable = new Table();
        //mainTable.setColor(ui.);
        //setBackground( new TextureRegionDrawable( new TextureRegion( new Texture( Gdx.files.internal("UI/NinePatches/transparent.png")))));
        this.scroll = new ScrollPane(this.mainTable, this.getSkin());
        this.setMovable(false);

        switch ( type ) {
            case LEFT:
                this.setWidth(Constants.UI_LEFT_RIGHT_WIDTH);
                this.setHeight(Constants.UI_LEFT_RIGHT_HEIGHT);
                this.setX(Constants.UI_LEFT_X);
                this.setY(Constants.UI_LEFT_RIGHT_Y);
                this.scroll.setScrollingDisabled(true, false);
                this.bottom();
                this.add(this.scroll).width(this.getWidth()).height(this.getHeight() - Constants.UI_IMAGE_SIZE);
                break;
            default:
            case RIGHT:
                this.setWidth(Constants.UI_LEFT_RIGHT_WIDTH);
                this.setHeight(Constants.UI_LEFT_RIGHT_HEIGHT);
                this.setX(Constants.UI_RIGHT_X);
                this.setY(Constants.UI_LEFT_RIGHT_Y);
                this.scroll.setScrollingDisabled(true, false);
                this.bottom();
                this.add(this.scroll).width(this.getWidth()).height(this.getHeight() - Constants.UI_IMAGE_SIZE);
                break;
            case MIDDLE:
                this.setWidth(Constants.UI_MIDDLE_WIDTH);
                this.setHeight(Constants.UI_MIDDLE_HEIGHT);
                this.setX(Constants.UI_MIDDLE_X);
                this.setY(Constants.UI_MIDDLE_Y);
                this.scroll.setScrollingDisabled(true, false);
                this.right();
                this.add(this.scroll).width(this.getWidth() - Constants.UI_IMAGE_SIZE).height(this.getHeight());
                break;
            case LISTVIEW:
                this.setWidth((float) (Constants.WIDTH / 1.3));
                this.setHeight((float) (Constants.HEIGHT / 1.3));
                this.setX(Constants.WIDTH / 2 - this.getWidth() / 2);
                this.setY(Constants.HEIGHT / 2 - this.getHeight() / 2);
                this.scroll.setScrollingDisabled(true, false);
                this.right();
                this.add(this.scroll)
                    .width(this.getWidth() - Constants.UI_IMAGE_SIZE)
                    .height(this.getHeight() - this.getHeight() / 10)
                    .padTop(this.getHeight() / 10);
                break;
            case TOP:
                this.setWidth(Constants.UI_TOP_BOTTOM_WIDTH);
                this.setHeight(Constants.UI_TOP_BOTTOM_HEIGHT);
                this.setX(Constants.UI_TOP_BOTTOM_X);
                this.setY(Constants.UI_TOP_Y);
                this.scroll.setScrollingDisabled(false, true);
                this.right();
                this.add(this.scroll).width(this.getWidth() - Constants.UI_IMAGE_SIZE).height(this.getHeight());
                break;
            case BOTTOM:
                this.setWidth(Constants.UI_TOP_BOTTOM_WIDTH);
                this.setHeight(Constants.UI_TOP_BOTTOM_HEIGHT);
                this.setX(Constants.UI_TOP_BOTTOM_X);
                this.setY(Constants.UI_BOTTOM_Y);
                this.scroll.setScrollingDisabled(false, true);
                this.right();
                this.add(this.scroll).width(this.getWidth() - Constants.UI_IMAGE_SIZE).height(this.getHeight());
                break;
            case COMBATBASE:
                this.setWidth(Constants.WIDTH / 1.2f);
                this.setHeight(Constants.UI_TOP_BOTTOM_HEIGHT);
                this.setX(Constants.UI_TOP_BOTTOM_RIGHT);
                this.setY(Constants.UI_BOTTOM_Y);
                this.scroll.setScrollingDisabled(true, true);
                this.right();
                this.add(this.scroll).width(this.getWidth() - Constants.UI_IMAGE_SIZE).height(this.getHeight());
                break;
            case COMBATSKILLS:
                this.setWidth(Constants.UI_LEFT_RIGHT_WIDTH * 2);
                this.setHeight(Constants.UI_LEFT_RIGHT_HEIGHT - Constants.UI_TOP_BOTTOM_HEIGHT - Constants.HEIGHT / 20);
                this.setX(Constants.UI_LEFT_X);
                this.setY(Constants.UI_LEFT_RIGHT_Y + Constants.UI_TOP_BOTTOM_HEIGHT + Constants.HEIGHT / 20);
                this.scroll.setScrollingDisabled(true, false);
                this.bottom();
                this.add(this.scroll).width(this.getWidth()).height(this.getHeight() - Constants.UI_IMAGE_SIZE);
                break;
            case HEALTHBAR:
                this.setWidth(Constants.WIDTH / 2.3f);
                this.setHeight(Constants.HEIGHT / 20);
                this.setX(Constants.UI_TOP_BOTTOM_RIGHT);
                this.setY(Constants.UI_TOP_BOTTOM_HEIGHT);
                this.scroll.setScrollingDisabled(true, true);
                this.left();
                this.add(this.scroll).width(this.getWidth() - Constants.UI_IMAGE_SIZE).height(this.getHeight());
                break;
            case STEAMMETER:
                this.setWidth(Constants.UI_TOP_BOTTOM_RIGHT);
                this.setHeight(Constants.UI_TOP_BOTTOM_RIGHT);
                this.setX(Constants.UI_LEFT_X);
                this.setY(Constants.UI_LEFT_RIGHT_Y);
                this.scroll.setScrollingDisabled(true, false);
                this.bottom();
                this.add(this.scroll).width(this.getWidth()).height(this.getHeight() - Constants.UI_IMAGE_SIZE);
                break;
            case SWITCHER:
                this.setWidth(Constants.UI_LEFT_RIGHT_WIDTH / 2);
                this.setHeight(Constants.UI_LEFT_RIGHT_HEIGHT / 3);
                this.setX(Constants.UI_LEFT_RIGHT_WIDTH * 2 - 10);
                this.setY(Constants.HEIGHT - Constants.UI_LEFT_RIGHT_HEIGHT / 3);
                System.out.println("setY: " + Constants.HEIGHT);
                this.scroll.setScrollingDisabled(true, true);
                this.bottom();
                this.add(this.scroll).width(this.getWidth()).height(this.getHeight() - 10);
                break;
        }

        if ( this.debugging ) {
            this.setDebug(true);
            this.scroll.setDebug(true);
            this.mainTable.setDebug(true);
        }
    }

    public void addBackButton(String handleID) {
        Button button = new Button(new TextureRegionDrawable(new TextureRegion(this.ui.getAsset(Constants.UI_ICON_BACK, Texture.class))));

        button.setName(handleID);

        button.setWidth(Constants.UI_IMAGE_SIZE);
        button.setHeight(Constants.UI_IMAGE_SIZE);

        button.setX(0);
        button.setY(this.getHeight() - Constants.UI_IMAGE_SIZE);

        this.addClickListener(button);

        this.addActor(button);
    }

    public void addFileSelectBoxItem(String path, String handleID) {
        SelectBox<String> selectBox = new SelectBox<String>(this.getSkin());
        selectBox.setName(handleID);

        Array<String> items = new Array<String>();

        //Check the Selectbox Header
        if ( path.equals(Constants.EDITOR_SAVES_PATH) ) {
            items.add("Load EditorSave");
        } else if ( path.equals(Constants.GAME_SAVES_PATH) ) {
            items.add("Load GameSave");
        } else {
            items.add("Load Save");
        }

        FileHandle dirHandle = Gdx.files.internal(path);
        for ( FileHandle entry : dirHandle.list() ) {
            items.add(entry.toString());
        }

        selectBox.setItems(items);

        this.addItem(selectBox, true);
    }

    public void addTextButtonItem(String text, String handleID) {
        TextButton textButton = new TextButton(text, this.getSkin());
        textButton.setName(handleID);
        this.addItem(textButton, false);
    }

    public Actor addTextButtonItemWithoutListener(String text) {
        TextButton textButton = new TextButton(text, this.getSkin());
        return this.addItemWithoutListener(textButton);
    }

    public Button addButtonItem(String text, String path, String handleID) {
        Button button = new Button(new TextureRegionDrawable(new TextureRegion(this.ui.getAsset(path, Texture.class))));
        button.setSkin(this.getSkin());
        button.setName(handleID);

        this.addItem(button, false);

        return button;
    }

    public void addLabelItem(String text, String handleID) {
        Label label = new Label(text, this.getSkin());
        label.setName(handleID);
        this.addItem(label, false);
    }

    // TODO: doppelter Code, create und addInventoryEntry machene *FAST* das selbe. 
    public Actor createInventoryEntry(String name, String description, String iconPath, String itemStat, int amount, String handleID) {
        Table item = new Table();
        Table entry = new Table();

        Button button = new Button(new TextureRegionDrawable(new TextureRegion(this.ui.getAsset(iconPath, Texture.class))));
        button.setSkin(this.getSkin());
        button.setName(handleID);
        item.add(button);

        TextArea itemDescription = new TextArea("\n" + name + "\nAnzahl: " + amount + "\n" + description + "\n" + itemStat, this.getSkin());
        itemDescription.setDisabled(true);
        entry.add(itemDescription).expandX().prefWidth(Constants.WIDTH / 2).expandY().prefHeight(button.getHeight()).row();

        item.add(entry);
        item.setName(handleID);
        this.mainTable.add(item).top().row();

        return item;
    }

    public void addInventoryEntry(String name, String description, String iconPath, String itemStat, String handleID) {
        Table item = new Table();
        Table entry = new Table();

        Button button = new Button(new TextureRegionDrawable(new TextureRegion(this.ui.getAsset(iconPath, Texture.class))));
        button.setSkin(this.getSkin());
        button.setName(handleID);
        item.add(button);

        TextArea itemDescription = new TextArea("\n" + name + "\n" + description + "\n" + itemStat, this.getSkin());
        itemDescription.setDisabled(true);
        entry.add(itemDescription).expandX().prefWidth(Constants.WIDTH / 2).expandY().prefHeight(button.getHeight()).row();

        item.add(entry);
        item.setName(handleID);
        this.mainTable.add(item).top().row();

        this.addClickListener(item);
    }

    void addItem(Actor actor, boolean change) {
        Table item = new Table();

        if ( this.debugging ) {
            item.setDebug(true);
        }

        item.setName(actor.getName());

        this.addActorToItem(actor, item);
        this.addItemToTable(item);

        if ( !change ) {
            this.addClickListener(item);
        } else {
            this.addChangeListener(item);
        }
    }

    private Table addItemWithoutListener(Actor actor) {
        Table item = new Table();

        if ( this.debugging ) {
            item.setDebug(true);
        }

        this.addActorToItem(actor, item);
        this.addItemToTable(item);

        return item;
    }

    private void addActorToItem(Actor actor, Table item) {
        switch ( this.type ) {
            default:
            case LEFT:
            case RIGHT:
                item.add(actor).width(this.getWidth() * Constants.UI_RATIO_I).height(this.getHeight() * Constants.UI_RATIO_I / Constants.UI_LEFT_RIGHT_AMOUNT);
                break;
            case MIDDLE:
                item.add(actor)
                    .width(this.getWidth() * Constants.UI_RATIO_I / Constants.UI_MIDDLE_AMOUNT)
                    .height(this.getHeight() * Constants.UI_RATIO_I / Constants.UI_MIDDLE_AMOUNT);
                break;
            case TOP:
            case BOTTOM:
                item.add(actor).width(this.getWidth() * Constants.UI_RATIO_I / Constants.UI_TOP_BOTTOM_AMOUNT).height(this.getHeight() * Constants.UI_RATIO_I);
                break;
            case COMBATBASE:
                item.add(actor).width(this.getWidth() * Constants.UI_RATIO_I / 7/* TODO :( */).height(this.getHeight() * Constants.UI_RATIO_I);
                break;
            case COMBATSKILLS:
                item.add(actor).width(this.getWidth() * Constants.UI_RATIO_I).height(this.getHeight() * Constants.UI_RATIO_I / Constants.UI_LEFT_RIGHT_AMOUNT);
                break;
            case SWITCHER:
                item.add(actor).width(Constants.HEIGHT / Constants.UI_ICON_SIZE).height(Constants.HEIGHT / Constants.UI_ICON_SIZE);
                break;
        }
    }

    public void addItemToTable(Table item) {
        this.mainTable.top().left();
        switch ( this.type ) {
            default:
            case LEFT:
            case RIGHT:
                this.mainTable.add(item).width(this.getWidth()).height(this.getHeight() / Constants.UI_LEFT_RIGHT_AMOUNT).row();
                break;
            case MIDDLE:
                if ( this.mainTable.getCells().size != 0 && ((this.mainTable.getCells().size + 1) % (Constants.UI_MIDDLE_AMOUNT - 1) == 0) ) {
                    this.mainTable.add(item).width(this.getWidth() / Constants.UI_MIDDLE_AMOUNT).height(this.getHeight() / Constants.UI_MIDDLE_AMOUNT).row();
                } else {
                    this.mainTable.add(item).width(this.getWidth() / Constants.UI_MIDDLE_AMOUNT).height(this.getHeight() / Constants.UI_MIDDLE_AMOUNT);
                }
                break;
            case TOP:
            case BOTTOM:
                this.mainTable.add(item).width(this.getWidth() / Constants.UI_TOP_BOTTOM_AMOUNT).height(this.getHeight());
                break;
            case COMBATBASE:
                this.mainTable.add(item).width(this.getWidth() / 6 + this.getWidth() / 90).height(this.getHeight()); //TODO philiiiiipp
                break;
            case COMBATSKILLS:
                this.mainTable.add(item).width(this.getWidth()).height(this.getHeight() / Constants.UI_LEFT_RIGHT_AMOUNT).row();
                break;
            case SWITCHER:
                this.mainTable.add(item).width(Constants.HEIGHT / Constants.UI_ICON_SIZE).height(Constants.HEIGHT / Constants.UI_ICON_SIZE).row();
                break;
        }
    }

    protected void addClickListener(final Actor actor) {
        if ( actor.getName() != null ) {
            actor.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    MenuWindow.this.ui.getInputHandler().handle(actor.getName());
                }
            });
        } else if ( actor.getName().equals("attack") || actor.getName().equals("defend") || actor.getName().equals("trank") ) {
            actor.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    //combatInputHandler.handle(actor.getName());

                }
            });
        } else {
            System.out.println("keinen Clicklistener hinzugefügt");
        }
    }

    private void addChangeListener(Actor actor) {
        if ( actor.getName() != null ) {
            actor.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    SelectBox<String> selectBox = (SelectBox<String>) actor;
                    MenuWindow.this.ui.getInputHandler().handle(actor.getName() + selectBox.getSelected());
                }
            });
        } else {
            System.out.println("keinen ChangeListener hinzugefügt");
        }
    }

    public void removeActorTable(Actor actor) {
        this.mainTable.removeActor(actor.getParent());

        Array<Table> array = new Array<Table>();
        for ( Actor item : this.mainTable.getChildren() ) {
            array.add((Table) item);
        }
        this.mainTable.clearChildren();
        for ( Table item : array ) {
            this.addItemToTable(item);
        }
    }

    public boolean isAscendantOfTable(Actor actor) {
        return this.mainTable.isAscendantOf(actor);
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
