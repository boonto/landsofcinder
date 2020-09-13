package de.loc.quest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.loc.event.Event;
import de.loc.event.EventListener;
import de.loc.event.EventSystem;
import de.loc.event.Observable;

public class QuestLog implements Observable, EventListener {
    private final List<EventListener> listeners;
    private final HashMap<String, Quest> activeQuests;

    public QuestLog(HashMap<String, Quest> questList) {
        this.listeners = new ArrayList<>();
        this.activeQuests = questList;
        EventSystem.getInstance().addListener(this, EventSystem.EventType.QUEST_EVENT);
    }

    public HashMap<String, Quest> getActiveQuests() {
        return this.activeQuests;
    }

    public void addListener(EventListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(EventListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void fire(EventSystem.EventType eventType, Object... args) {
        for ( EventListener listener : this.listeners ) {
            listener.update(new Event(eventType));
        }
    }

    @Override
    public void update(Event e) {
        if ( e.eventType == EventSystem.EventType.QUEST_EVENT ) {
            this.fire(null);
        }
    }
}
