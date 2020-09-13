package de.loc.quest;

import java.util.ArrayList;

import de.loc.event.Event;

public class QuestEvent {

    // Das Event das zum Erreichen dieses Quest-Ziels nötig ist
    public final Event goalEvent;

    // Beschreibung des aktuellen Quest-Ziels
    public final String desc;
    // Event das durch das erfolgreiche Abschließen des Quest-Ziels ausgelöst wird!
    // (Kann auch leer sein! Dafür die zwei Konstruktoren!)
    //public final Event triggerEvent;
    public final ArrayList<Event> triggerEvents;

    private int goalEventCount;

    public QuestEvent(Event goalEvent, String description) {
        this.triggerEvents = new ArrayList<Event>();
        this.goalEvent = goalEvent;
        this.desc = description;
        //this.triggerEvent = null;
        this.goalEventCount = 1;
    }

    public QuestEvent(Event goalEvent, String description, ArrayList<Event> triggerList) {
        this.goalEvent = goalEvent;
        this.desc = description;
        this.triggerEvents = triggerList;

        this.goalEventCount = 1;
    }

    public QuestEvent(Event goalEvent, int goalEventCount, String description, ArrayList<Event> triggerList) {
        this.goalEvent = goalEvent;
        this.desc = description;
        this.triggerEvents = triggerList;
        this.goalEventCount = goalEventCount;
    }

    public QuestEvent(Event goalEvent, int goalEventCount, String description) {
        this.triggerEvents = new ArrayList<>();
        this.goalEvent = goalEvent;
        this.desc = description;
        //this.triggerEvent = null;
        this.goalEventCount = goalEventCount;
    }

    public void update(Event e) {
        if ( this.goalEvent.compareTo(e) ) {
            this.goalEventCount -= 1;
        }
    }

    public boolean isFinished() {
        return this.goalEventCount <= 0;
    }

}
