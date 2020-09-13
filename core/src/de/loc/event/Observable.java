package de.loc.event;

public interface Observable {
    void addListener(EventListener listener);

    void removeListener(EventListener listener);

    void fire(EventSystem.EventType eventType, Object... args);
}
