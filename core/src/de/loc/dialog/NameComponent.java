package de.loc.dialog;

import com.badlogic.ashley.core.Component;

public class NameComponent implements Component {
    public final String name;

    public NameComponent(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
