package de.loc.tools;

import java.util.HashMap;

public class StateMachine {
    private final HashMap<String, IState> states;
    private IState currentState;

    public StateMachine() {
        this.states = new HashMap<>();
        this.currentState = new EmptyState();
    }

    public void update(float deltaTime) {
        this.currentState.update(deltaTime);
    }

    public void change(String stateName, Object... objects) {
        this.currentState.onExit();
        this.currentState = this.states.get(stateName);
        this.currentState.onEnter(objects);
    }

    public void add(String name, IState state) {
        this.states.put(name, state);
    }
}