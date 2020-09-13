package de.loc.combat;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import java.util.ArrayList;
import java.util.List;

import de.loc.core.LandsOfCinderSystem;
import de.loc.event.Event;
import de.loc.event.EventListener;
import de.loc.event.EventSystem;
import de.loc.event.Observable;
import de.loc.input.ClickableComponent;
import de.loc.tools.StateMachine;

public class CombatSystem extends LandsOfCinderSystem implements Observable {
    private final List<EventListener> listeners;

    private ImmutableArray<Entity> combatEntities = null;

    private final StateMachine stateMachine;
    private static final String WAIT_FOR_COMBAT_STATE = "waitForCombat";
    private static final String COMBAT_STATE = "combat";

    public CombatSystem() {
        this.listeners = new ArrayList<>(1);

        this.stateMachine = new StateMachine();
    }

    @Override
    public void addedToEngine(Engine engine) {
        this.combatEntities = engine.getEntitiesFor(Family.all(CombatComponent.class, ClickableComponent.class).get());

        //erstellung der statemachine, zwei states: kein kampf und kampf
        this.stateMachine.add(WAIT_FOR_COMBAT_STATE, new WaitForCombatState(this));
        this.stateMachine.add(COMBAT_STATE, new CombatState(this));
        this.stateMachine.change(WAIT_FOR_COMBAT_STATE, this.combatEntities); //anfangs in keinem Kampf
    }

    @Override
    public void update(float deltaTime) {
        this.stateMachine.update(deltaTime); //updaten der statemachine
    }

    //package-private methoden zum wechseln des states
    void waitForCombat() {
        this.stateMachine.change(WAIT_FOR_COMBAT_STATE, this.combatEntities);
    }

    void combat(Object... objects) {
        this.stateMachine.change(COMBAT_STATE, objects);
    }

    @Override
    public void addListener(EventListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(EventListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void fire(EventSystem.EventType eventType, Object... args) {
        for ( EventListener listener : this.listeners ) {
            listener.update(new Event(eventType, args));
        }
    }

    @Override
    public void reset() {

    }
}