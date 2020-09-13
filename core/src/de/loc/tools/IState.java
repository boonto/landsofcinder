package de.loc.tools;

public interface IState {
    void update(float deltaTime);

    void onEnter(Object... objects);

    void onExit();
}
