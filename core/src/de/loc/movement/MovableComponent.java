package de.loc.movement;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.loc.movement.steering.SteeringAgent;
import de.loc.movement.steering.SteeringSetup;
import de.loc.tools.Position;

public class MovableComponent implements Component {

    Position position;
    boolean changed; //absichtlich package-local, da nur das movement system das verändern soll
    boolean stopped;

    public SteeringAgent steeringAgent;

    public Array<Vector2> waypoints;

    public MovableComponent(Position position, SteeringSetup steering) {
        this.position = new Position(position);
        this.changed = false;
        this.stopped = false;

        this.steeringAgent = new SteeringAgent(position, steering);

        this.waypoints = new Array<>();
    }

    public void move(int x, int y) {
        this.position.x = x;
        this.position.y = y;
        this.changed = true;
    }

    public void move(Position position) {
        this.move(position.x, position.y);
    }
}
