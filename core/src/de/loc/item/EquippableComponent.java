package de.loc.item;

import com.badlogic.ashley.core.Component;

public class EquippableComponent implements Component {
    public Type type;

    public EquippableComponent(Type type) {
        this.type = type;
    }

    public enum Type {
        WEAPON_LEFT("WEAPON_LEFT"),
        WEAPON_RIGHT("WEAPON_RIGHT"),
        ARMOR("ARMOR");

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
