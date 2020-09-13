package de.loc.movement;

import com.badlogic.ashley.core.Component;

public class EntityStateComponent implements Component {

    public boolean fighting; //TODO was schöneres überlegen
    private byte state;

    public EntityStateComponent() {
        this.state = EntityState.STANDING;
    }

    public void setState(byte state) {
        this.state = state;
    }

    public void stand() {
        this.state = EntityState.STANDING;
    }

    public void walk() {
        this.state = EntityState.WALKING;
    }

    public void run() {
        this.state = EntityState.RUNNING;
    }

    public void fight() {
        this.state = EntityState.FIGHTING;
    }

    public boolean isStanding() {
        return (int) this.state == (int) EntityState.STANDING;
    }

    public boolean isWalking() {
        return (int) this.state == (int) EntityState.WALKING;
    }

    public boolean isRunning() {
        return (int) this.state == (int) EntityState.RUNNING;
    }

    public boolean isFighting() {
        return (int) this.state == (int) EntityState.FIGHTING;
    }

    public enum EntityState {
        ;
        public static final byte STANDING = (byte) 0;
        public static final byte WALKING = (byte) 1;
        public static final byte RUNNING = (byte) 2;
        public static final byte FIGHTING = (byte) 3;
    }
}
