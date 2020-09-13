package de.loc.quest;

import com.badlogic.ashley.core.Component;

public class QuestComponent implements Component {

    public final String NAME;
    public final String DIALOG;
    public final String path;
    public final String CLIENT_NAME;
    public boolean active = false;

    public QuestComponent(String questPath, String name, String dialog, String clientName) {
        this.NAME = name;
        this.DIALOG = dialog;
        this.path = questPath;
        this.CLIENT_NAME = clientName;
    }

}
