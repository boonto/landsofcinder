package de.loc.tools;

import de.loc.core.TypeComponent;

public class Helper {
    public static String getIconPath(TypeComponent t) {
        return Constants.ICON_PATH + t.type + "/" + t.name + "_icon.png";
    }

    public static String getIconPath(String type, String name) {
        return Constants.ICON_PATH + type + "/" + name + "_icon.png";
    }

    public static class Alignment {
        public static final int LEFT = 1;
        public static final int CENTER = 2;
        public static final int RIGHT = 3;
    }
}
