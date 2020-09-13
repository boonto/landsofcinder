package de.loc.core;

import com.badlogic.ashley.core.Component;

public class EmptyComponent implements Component {

    public float x;
    public float y;
    public float z;

    public EmptyComponent() {
        this.x = 1.0f;
        this.y = 1.0f;
        this.z = 1.0f;
    }

}
