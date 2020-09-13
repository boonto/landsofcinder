package de.loc.core;

import com.badlogic.ashley.core.Component;

public class TypeComponent implements Component {

    public Type type;
    public String name;

    public TypeComponent(Type t, String name) {
        this.type = t;
        this.name = name;
    }

    public enum Type {
        PLAYER("PLAYER"),
        OBJECT("OBJECT"),
        COMMENTARY_OBJECT("COMMENTARY_OBJECT"),
        MOB("MOB"),
        NPC("NPC"),
        ITEM("ITEM"),
        CONSUMABLE("CONSUMABLE"),
        EQUIPPABLE("EQUIPPABLE"),
        PARTICLE("PARTICLE"),
        CHEST("CHEST"),
        EMPTY("EMPTY"),
        TILE("TILE");

        private final String text;

        Type(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return this.text;
        }

        public static Type fromString(String text) {
            for ( Type type : values() ) {
                if ( type.toString().equals(text) ) {
                    return type;
                }
            }
            return null;
        }
    }
}

