package de.loc.quest;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.Queue;

import de.loc.event.Event;
import de.loc.event.EventSystem;
import de.loc.tools.Constants;

public class Quest {

    public final String NAME;

    public final ArrayList<Event> TRIGGER_EVENTS;
    public final String path;
    public String description;

    private int progress;
    private final Queue<QuestEvent> events;

    public boolean active = false;

    private String clientName;

    public Quest(
        String questPath, String name, String desc, String dialog, ArrayList<Event> triggerEvents, Queue<QuestEvent> events) {
        this.NAME = name;
        this.description = desc;
        this.TRIGGER_EVENTS = triggerEvents;

        this.path = questPath;
        this.events = events;

        this.progress = 0;
    }

    public int getProgress() {
        return this.progress;
    }

    // this update is only called by the QuestSystem, NOT the EventSystem!
    public void update(Event e) {

        if ( this.events.peek() == null ) {
            return;
        }

        // ist das Event nicht reproduzierbar?
        if ( e.eventType == EventSystem.EventType.FETCH_EVENT || e.eventType == EventSystem.EventType.KILL_EVENT ) {
            for ( QuestEvent qe : this.events ) {
                qe.update(e);
            }
        } else {
            this.events.peek().update(e);
        }

        // while weil: das nächste QuestEvent könnte schon abgeschlossen sein!
        // (z.B. weil ein Item sich schon im Inventar befindet)
        // Deshalb auch das neue Event auf dem Stack durchgehen, und auch das danach usw...
        // BIS: ein Event noch nicht erreicht wurde!
        while ( this.events.peek() != null && this.events.peek().isFinished() ) {
            this.description += "\n --- \n";
            this.description += this.events.peek().desc;
            Gdx.app.log("Quest", this.events.peek().desc);
            for ( Event trigger : this.events.poll().triggerEvents ) {
                this.handleTriggerEvent(trigger);
            }
            this.progress++;
            EventSystem.getInstance().commitEvent(new Event(EventSystem.EventType.QUEST_EVENT));
        }

    }

    public void init() {
        // Quest-Dialog zum Quest-Client hinzufügen:
        // NEIN!
        //handleTriggerEvent(new Event(EventType.ADD_DIALOG, clientName, DIALOG.dialog));

        // restliche TriggerEvents (optional)
        if ( this.TRIGGER_EVENTS != null ) {
            for ( Event e : this.TRIGGER_EVENTS ) {
                this.handleTriggerEvent(e);
            }
        }

    }

    public boolean isFinished() {
        return this.events.isEmpty();
    }

    private void handleTriggerEvent(Event triggerEvent) {

        if ( triggerEvent != null ) {
            EventSystem.getInstance().commitEvent(triggerEvent);
        }
    }

    public String getClientName() {
        return this.clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;

        // Alle ClientName tags in den ADD_DIALOG TriggerEvents
        // durch den echten ClientName austauschen
        for ( Event e : this.TRIGGER_EVENTS ) {
            if ( (e.eventType == EventSystem.EventType.ADD_DIALOG) && e.args[0].equals(Constants.CLIENT_NAME) ) {
                e.args[0] = clientName;
            }
        }
        for ( QuestEvent qe : this.events ) {
            // goalEvents austauschen
            if ( (qe.goalEvent != null) && (qe.goalEvent.args.length > 0) && qe.goalEvent.args[0].equals(Constants.CLIENT_NAME) ) {
                qe.goalEvent.args[0] = clientName;
            }

            for ( Event trigger : qe.triggerEvents ) {
                if ( (trigger.args.length > 0) && trigger.args[0].equals(Constants.CLIENT_NAME) ) {
                    trigger.args[0] = clientName;
                }
            }

        }

    }

    public void setProgress(int progress) {
        for ( int i = 0; i < progress; i++ ) {
            QuestEvent e = this.events.poll();
            this.description += e.desc;
            for ( Event triggerEvent : e.triggerEvents ) {
                EventSystem.getInstance().commitEvent(triggerEvent);
            }
        }
        this.progress = progress;
    }

}

