package de.loc.movement;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

import de.loc.tools.Position;

public class MovementAIComponent implements Component {
    public Position center; // may be null
    public int range;
    public Vector2 target; // may be null

    public MovementAIComponent() {
        this(null, 0, null);
    }

    public MovementAIComponent(Vector2 target) {
        this(null, 0, target);
    }

    public MovementAIComponent(Position center, int range) {
        this(center, range, null);
    }

    public MovementAIComponent(Position center, int range, Vector2 target) {
        if ( center != null ) {
            this.center = new Position(center);
        }
        this.range = range;
        this.target = target;
    }
}
