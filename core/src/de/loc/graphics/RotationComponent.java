package de.loc.graphics;

import com.badlogic.ashley.core.Component;

public class RotationComponent implements Component {
    public float angle;

    public RotationComponent() {
        this(0.0f);
    }

    public RotationComponent(float angle) {
        this.angle = angle;
    }
}
