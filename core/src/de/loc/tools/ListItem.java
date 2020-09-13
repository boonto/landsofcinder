package de.loc.tools;

import com.badlogic.gdx.utils.XmlReader;

public class ListItem {
    public final String name;
    public final String icon;
    public final XmlReader.Element xmlElement;

    public ListItem(XmlReader.Element element) {
        this.name = element.getChildByName("Name").getText();
        this.icon = element.getChildByName("Icon").getText();
        this.xmlElement = element;
    }
}