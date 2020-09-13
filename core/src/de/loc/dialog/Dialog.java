package de.loc.dialog;

import de.loc.event.Event;

public class Dialog {

    public final String name;
    public final String text;
    public final String iconPath;
    public final Event triggerEvent;

    public Dialog(String name, String text) {
        this.name = name;
        this.text = text;
        this.iconPath = null;
        this.triggerEvent = null;
    }

    public Dialog(String name, String text, String iconPath) {
        this.name = name;
        this.text = text;
        this.iconPath = iconPath;
        this.triggerEvent = null;
    }

    public Dialog(String name, String text, Event triggerEvent) {
        this.name = name;
        this.text = text;
        this.iconPath = null;
        this.triggerEvent = triggerEvent;
    }

    public Dialog(String name, String text, String iconPath, Event triggerEvent) {
        this.name = name;
        this.text = text;
        this.iconPath = iconPath;
        this.triggerEvent = triggerEvent;
    }
}
