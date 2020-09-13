package de.loc.graphics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

public class ModelComponent implements Component {

    public final ModelInstance model;
    public String modelPath;

    public ModelComponent(ModelInstance model) {
        this.model = model;
        this.model.userData = new CustomEffectData();
    }

    public String getModelPath() {
        return this.modelPath;
    }
}
