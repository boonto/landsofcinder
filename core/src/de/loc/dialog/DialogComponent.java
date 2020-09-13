package de.loc.dialog;

import com.badlogic.ashley.core.Component;

import java.util.ArrayList;

public class DialogComponent implements Component {

    public String id;
    public ArrayList<Dialog> dialogs;

    public DialogComponent(String id, ArrayList<Dialog> dialogs) {
        this.id = id;
        this.dialogs = dialogs;
    }
}
