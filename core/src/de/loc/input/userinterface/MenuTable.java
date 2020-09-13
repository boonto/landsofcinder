package de.loc.input.userinterface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;

import de.loc.tools.Constants;

public class MenuTable extends Table {

    private final ScrollPane scroll;
    private final Table mainTable;
    private final UserInterface ui;
    private final Type type;
    //For debugging purposes
    private final boolean debugging = false;

    public MenuTable(UserInterface ui, Type type) {
        super(ui.getSkin());
        this.ui = ui;
        this.type = type;

        this.mainTable = new Table();
        this.scroll = new ScrollPane(this.mainTable, this.getSkin());

        this.mainTable.setBackground(new NinePatchDrawable(this.getNinePatch(Constants.UI_NINEPATCH_PAPER_BRIGHT)));

        this.scroll.setScrollingDisabled(true, false);

        this.scroll.getStyle().background = null;

        this.setWidth(Constants.WIDTH);
        this.setHeight(Constants.HEIGHT);
        this.add(this.scroll)/*.width(getWidth()).height(getHeight())*/;

        if ( this.debugging ) {
            this.setDebug(true);
            this.scroll.setDebug(true);
            this.mainTable.setDebug(true);
        }
    }

    public ScrollPane getScroll() {
        return this.scroll;
    }

    private NinePatch getNinePatch(String name) {
        Texture t = new Texture(Gdx.files.internal(name));

        return new NinePatch(new TextureRegion(t, 1, 1, t.getWidth() - 2, t.getHeight() - 2), 10, 10, 10, 10);
    }

    public void addHeadingItem() {

        //Image image = new Image(new Texture("UI/Ninepatches/heading.png"));
        //Image image = new Image(new Texture("UI/LoC_Logo/LoC_Logo_Blank.png"));
        //image.setScaling(Scaling.none);
        //addItem(image);
    }

    public void addLabelItem(String text) {
        Label label = new Label(text, this.getSkin());
        label.setAlignment(Align.center);
        this.addItem(label);
    }

    public void addTextButtonItem(String text, String handleID) {
        TextButton textButton = new TextButton(text, this.getSkin());

        textButton.setName(handleID);
        this.addItem(textButton);
    }

    private void addItem(Actor actor) {
        Table item = new Table();

        if ( this.debugging ) {
            item.setDebug(true);
        }

        item.setName(actor.getName());

        this.addActorToItem(actor, item);
        this.addItemToTable(item);

        this.addClickListener(item);
    }

    private void addActorToItem(Actor actor, Table item) {
        switch ( this.type ) {
            default:
            case STANDARD:
                item.add(actor).width(Constants.UI_MENU_BUTTON_WIDTH * Constants.UI_RATIO_I).height(Constants.UI_MENU_BUTTON_HEIGHT * Constants.UI_RATIO_I);
                break;
            case CHOOSEWORLD:
                item.add(actor).width(Constants.UI_MENU_CHOOSE_WIDTH * Constants.UI_RATIO_I).height(Constants.UI_MENU_CHOOSE_HEIGHT * Constants.UI_RATIO_I);
                break;
        }
    }

    private void addItemToTable(Table item) {
        switch ( this.type ) {
            default:
            case STANDARD:
                this.mainTable.add(item).width(Constants.UI_MENU_BUTTON_WIDTH).height(Constants.UI_MENU_BUTTON_HEIGHT).row();
                break;
            case CHOOSEWORLD:
                if ( this.mainTable.getCells().size != 0 && ((this.mainTable.getCells().size + 1) % (Constants.UI_MENU_CHOOSE_AMOUNT_HOR)) == 0 ) {
                    this.mainTable.add(item).width(Constants.UI_MENU_CHOOSE_WIDTH).height(Constants.UI_MENU_CHOOSE_HEIGHT).row();
                } else {
                    this.mainTable.add(item).width(Constants.UI_MENU_CHOOSE_WIDTH).height(Constants.UI_MENU_CHOOSE_HEIGHT);
                }
                break;
        }
    }

    private void addClickListener(final Table item) {
        if ( item.getName() != null ) {
            item.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    MenuTable.this.ui.getInputHandler().handle(item.getName());
                }
            });
        } else {
            System.out.println("kein Inputhandle angegeben");
        }
    }

    public enum Type {
        STANDARD,
        CHOOSEWORLD
    }
}
