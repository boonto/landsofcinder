package de.loc.dialog;

import com.badlogic.ashley.core.Entity;

import de.loc.event.Event;
import de.loc.event.EventListener;
import de.loc.event.EventSystem;
import de.loc.tools.IState;

public class DialogState implements IState, EventListener {

    private DialogComponent dialog;
    private String name;
    private String iconPath;
    private int currentDialogIndex;
    private int numberOfDialogs;
    private Entity entity;

    public DialogState() {
        EventSystem.getInstance().addListener(this, EventSystem.EventType.DIALOG_WINDOW_CLICKED);
    }

    @Override
    public void update(float deltaTime) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onEnter(Object... objects) {
        this.name = (String) objects[0];
        this.iconPath = (String) objects[1];
        this.dialog = (DialogComponent) objects[2];
        this.entity = (Entity) objects[3];
        this.currentDialogIndex = 0;
        this.numberOfDialogs = this.dialog.dialogs.size();

        EventSystem.getInstance().commitEvent(new Event(EventSystem.EventType.DIALOG_STARTED, this.name, this.dialog.id, this.entity));

        this.triggerDialog();

    }

    private void triggerDialog() {
        Dialog d = this.dialog.dialogs.get(this.currentDialogIndex);

        EventSystem.getInstance()
                   .commitEvent(new Event(EventSystem.EventType.TALK_EVENT,
                                          (d.name != null ? d.name : this.name),
                                          d.text,
                                          (d.iconPath != null ? d.iconPath : this.iconPath)));
    }

    @Override
    public void onExit() {
        this.name = null;
        this.iconPath = null;
        this.dialog = null;
        this.currentDialogIndex = 0;
        this.numberOfDialogs = 0;
    }

    @Override
    public void update(Event e) {

        if ( this.currentDialogIndex < (this.numberOfDialogs - 1) ) {
            this.currentDialogIndex++;
            this.triggerDialog();
        } else {
            EventSystem.getInstance().commitEvent(new Event(EventSystem.EventType.DIALOG_ENDED, this.entity));
        }

    }

}
