package de.loc.input.userinterface;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import de.loc.tools.Constants;

public class DialogWindow extends MenuWindow {

    public DialogWindow(UserInterface ui) {
        super(ui, Type.BOTTOM);

        this.scroll.setScrollingDisabled(true, false);
        this.setName("game_dialog_window");
        this.addClickListener(this);
    }

    public void addDialog(String name, String text, String pictureName) {
        this.mainTable.clearChildren();

        Table item = new Table();

        Texture texture = new Texture(Constants.ICON_PATH + pictureName);

        Image image = new Image(texture);
        this.scroll.setScrollingDisabled(true, true);

        Label textLabel = new Label(text, this.getSkin());

        textLabel.setWrap(true);

        float width = Constants.WIDTH - image.getImageWidth();

        item.add(image);
        item.add(textLabel).width(width - width * Constants.UI_RATIO);
        //item.add(nameTable);

        this.mainTable.top().left();

        this.mainTable.add(item).width(width);
    }
}
