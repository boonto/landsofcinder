package de.loc.input.userinterface;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import de.loc.tools.Constants;

public class SkillWindow extends LandsOfCinderWindow {

    public enum Type {
        LEFT,
        RIGHT,
        BASE,
        TOP,
        BOTTOM,

    }

    protected ScrollPane scroll;
    protected Table mainTable;
    private Table headingTable;
    private Table bodyTable;
    private int i = 0;

    protected UserInterface ui;
    private final Type type;

    //For debugging purposes
    private final boolean debugging = false;

    public SkillWindow(UserInterface ui, Type type) {
        super(ui.getSkin());
        this.ui = ui;
        this.type = type;

        this.mainTable = new Table();

        //mainTable.setColor(ui.);
        //setBackground( new TextureRegionDrawable( new TextureRegion( new Texture( Gdx.files.internal("UI/NinePatches/transparent.png")))));
        this.scroll = new ScrollPane(this.mainTable, this.getSkin());
        this.headingTable = new Table();
        this.bodyTable = new Table();

        this.setMovable(false);

        switch ( type ) {
            case LEFT:
                this.setWidth(Constants.UI_MIDDLE_WIDTH);
                this.setHeight(Constants.UI_MIDDLE_HEIGHT);
                this.setX(0);
                this.setY(0);
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
            case BASE:
                this.setWidth(Constants.UI_MIDDLE_WIDTH);
                this.setHeight(Constants.UI_MIDDLE_HEIGHT);
                this.setX(Constants.UI_MIDDLE_X);
                this.setY(Constants.UI_MIDDLE_Y);
                this.top();
                this.add(this.headingTable).row();
                //headingTable.setDebug(true);
                this.add(this.bodyTable).width(this.getWidth()).height(this.getHeight());
                //bodyTable.setDebug(true);

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

        }

        // addTable();
        if ( this.debugging ) {
            this.setDebug(true);
            this.scroll.setDebug(true);
            this.mainTable.setDebug(true);
        }
    }

    public void addTable() {
        this.headingTable = new Table();
        this.bodyTable = new Table();
        //headingTable.setDebug(true);
        this.mainTable.add(this.headingTable).width(this.getWidth() / 5).height(this.getHeight() / 5);
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

    public void addTest() {
        Image image = new Image(new TextureRegionDrawable(new TextureRegion(this.ui.getAsset(Constants.UI_ICON_BACK, Texture.class))));
        image.setWidth(20);
        image.setHeight(20);

        image.setX(this.bodyTable.getWidth() / 2);
        image.setY(this.getHeight() - Constants.UI_IMAGE_SIZE);
        this.bodyTable.add(image);
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

    public void addHeading(String text) {
        Label label = new Label(text, this.getSkin());
        //Table tableTop = new Table();
        this.headingTable.top();
        this.headingTable.add(label).row();
    }

    public void addSkillButton(String text) {
        TextButton textButton = new TextButton(text, this.getSkin());
        //addItem(textButton, false);
        this.addItem(textButton, true);
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
            case BASE:
                item.add(actor).width(this.getWidth() * Constants.UI_RATIO_I / 9).height(this.getHeight() * Constants.UI_RATIO_I / 9);
                break;
            case TOP:
            case BOTTOM:
                item.add(actor).width(this.getWidth() * Constants.UI_RATIO_I / Constants.UI_TOP_BOTTOM_AMOUNT).height(this.getHeight() * Constants.UI_RATIO_I);
                break;

        }
    }

    public void addItemToTable(Table item) {
        //mainTable.top().left();
        switch ( this.type ) {
            default:
            case LEFT:
            case RIGHT:
                this.mainTable.add(item).width(this.getWidth()).height(this.getHeight() / Constants.UI_LEFT_RIGHT_AMOUNT).row();
                break;
            case BASE:
                this.bodyTable.top().left();
                if ( this.i == 2 || this.i == 5 ) {
                    this.bodyTable.add(item).width(this.getWidth() / 3).height(this.getHeight() / 7).row();
                    this.i += 1;
                } else {
                    this.bodyTable.add(item).width(this.getWidth() / 3).height(this.getHeight() / 7);
                    this.i += 1;
                }
            	/*if (bodyTable.getCells().size != 0 && ((bodyTable.getCells().size + 1) % (Constants.UI_MENU_CHOOSE_AMOUNT_HOR)) == 0) {
            		bodyTable.add(item).width(getWidth() / 3).height(getHeight() / 7).row();
                } else {
                	bodyTable.add(item).width(getWidth() / 3).height(getHeight() / 7);
                }*/
                System.out.println(this.i);
                break;
            case TOP:
            case BOTTOM:
                this.mainTable.add(item).width(this.getWidth() / Constants.UI_TOP_BOTTOM_AMOUNT).height(this.getHeight());
                break;

        }
    }

    protected void addClickListener(final Actor actor) {
        if ( actor.getName() != null ) {
            actor.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    SkillWindow.this.ui.getInputHandler().handle(actor.getName());
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
                    SkillWindow.this.ui.getInputHandler().handle(actor.getName() + selectBox.getSelected());
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

