package de.loc.event;

import com.badlogic.gdx.Gdx;

import java.util.Collection;
import java.util.Deque;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.loc.tools.Constants;

public final class EventSystem {

    private static EventSystem eventSystem;

    public enum EventType {
        KILL_EVENT, //global, [0] type of killed, [1] entityID of killed

        FETCH_EVENT, //global, [0] name of fetched item

        EXPLORE_EVENT,

        TALK_EVENT, //global, [0] name of npc , [1], dialog

        QUEST_EVENT,

        TOUCH_GRID_EVENT,

        DESTROY_EVENT,

        ADD_DIALOG,

        CONTAINS_ITEM,

        COMMENTARY,

        DIALOG_STARTED, // [0] name of NPC, [1] ID of the DialogSheet

        DIALOG_ENDED,

        //CombatSystem & AnimationSystem -> keine QuestEvents

        EQUIP_ITEM, //global, [0] entityID of equipped item, [1] entityID of target that should be equipped

        CONSUME_ITEM, //global, [0] entityID of used item, [1] entityID of target the item should be used on

        ATTACK_CLICKED, //global, super combatevent

        ATTACK_EVENT, //global, [0] entityID of attacker, [1] entityID of defender

        COMBAT_ENTITY_CLICKED, //global [0] entityID of clicked entity

        COMBAT_STARTED, //local, [0] entityID of combattant

        COMBAT_ENEMY, //local, [0] entityID of enemy combattant

        COMBAT_ENDED, //local

        GAME_OVER, //local

        EMPTY_EVENT, // only for triggering, for example Questlog update

        DIALOG_WINDOW_CLICKED,

        SHOW_INVENTORY_LIST,

        SHOW_MERCHANT_WINDOW,
        LEVELUP_EVENT,
        LEVELUP_ANSWER_EVENT,
    }

    private final Map<EventType, LinkedList<EventListener>> listeners;

    private EventSystem() {
        this.listeners = new EnumMap<>(EventType.class);

        for ( EventType event : EventType.values() ) {
            this.listeners.put(event, new LinkedList<EventListener>());
        }
        eventSystem = this;

    }

    public static EventSystem getInstance() {
        if ( eventSystem != null ) {
            return eventSystem;
        } else {
            return new EventSystem();
        }
    }

    public void addListener(EventListener listener, EventType... eventType) {
        for ( EventType event : eventType ) {
            Deque<EventListener> list = this.listeners.get(event);
            if ( list != null ) {
                list.add(listener);
            }

        }
    }

    public void commitEvent(Event e) {
        List<EventListener> list = this.listeners.get(e.eventType);
        if ( Constants.LOG_EVENTS ) {
            StringBuilder message = new StringBuilder(50);
            message.append("Type: ").append(e.eventType);
            for ( Object arg : e.args ) {
                message.append(" Argument: ").append(arg);
            }
            Gdx.app.log("Event", message.toString());
        }
        for ( EventListener listener : list ) {
            listener.update(e);
        }
    }

    public void clear() {
        for ( Collection<EventListener> l : this.listeners.values() ) {
            l.clear();
        }
    }
}
