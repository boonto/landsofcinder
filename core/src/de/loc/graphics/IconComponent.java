package de.loc.graphics;

import com.badlogic.ashley.core.Component;

public class IconComponent implements Component {

    public IconComponent(String path) {
        this.iconPath = path;
    }

    public String iconPath;
}
