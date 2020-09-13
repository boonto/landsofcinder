package de.loc.input.userinterface;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.XmlReader;

import java.util.ArrayList;

import de.loc.tools.Constants;
import de.loc.tools.ListItem;

public class OnlinePackageListView extends MenuWindow {

    public OnlinePackageListView(UserInterface ui) {
        super(ui, Type.LISTVIEW);

    }

    public void setContent(ArrayList<ListItem> listItems) {

        this.mainTable.clearChildren();
        for ( int i = 0; i < listItems.size(); i++ ) {
            ListItem item = listItems.get(i);
            XmlReader.Element node = item.xmlElement;

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

            this.addInventoryEntry(item.name, description, Constants.ICON_PATH + item.icon, stat, "ListItem_" + i);
        }

    }

    public void setContent(FileHandle dirHandle) {
        this.mainTable.clearChildren();
        String path = dirHandle.path();
        for ( FileHandle entry : dirHandle.list() ) {
            String name = entry.toString().substring(path.length() + 1).replace("_", " ");
            this.addListEntry(name);
        }

    }

    public void addListEntry(String name) {
        Table item = new Table();

        Label nameLabel = new Label(name, this.getSkin());

        float width = this.getWidth() - Constants.UI_IMAGE_SIZE;

        item.add(nameLabel).width(width / 2);
        item.setName("QuestItem_" + name);
        this.addClickListener(item);

        item.row();
        item.pad(10, 10, 10, 10);
        this.mainTable.top().left();

        this.mainTable.add(item).width(width).expandX().prefWidth(Constants.WIDTH / 2).row();
    }

    public void addListItem(String title, String author) {
        Table item = new Table();

        Table name = new Table();
        Table location = new Table();
        Table rating = new Table();

        Label nameLabel = new Label(title, this.getSkin());
        Label nameLabel2 = new Label(author, this.getSkin());
        Label nameLabel3 = new Label(title, this.getSkin());

        float width = this.getWidth() - Constants.UI_IMAGE_SIZE;

        name.add(nameLabel);
        location.add(nameLabel2);
        rating.add(nameLabel3);

        //name.setDebug(true);
        //location.setDebug(true);
        //rating.setDebug(true);

        item.left();
        item.add(name).width(width / 3);
        item.add(location).width(width / 3);
        item.add(rating).width(width / 3);
        item.setName("QuestItem_" + title);
        //addClickListener(item);

        item.row();
        item.pad(10, 10, 10, 10);
        this.mainTable.top().left();

        this.mainTable.add(item).width(width).expandX().prefWidth(Constants.WIDTH / 2).row();
    }

}
