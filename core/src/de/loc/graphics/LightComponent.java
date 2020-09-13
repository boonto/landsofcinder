package de.loc.graphics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;

public class LightComponent implements Component {

    public Vector3 color;
    public float intensity;

    public LightComponent() {
        this.color = new Vector3(1.0f, 1.0f, 1.0f);
        this.intensity = 10.0f;
    }
}
