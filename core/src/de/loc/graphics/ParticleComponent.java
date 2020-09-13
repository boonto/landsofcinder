package de.loc.graphics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;

public class ParticleComponent implements Component {

    public final String particlePath;
    public Vector3 particlePosition;

    public ParticleComponent(String particlePath, Vector3 particlePosition) {
        this.particlePath = particlePath;
        this.particlePosition = particlePosition;
    }
}
