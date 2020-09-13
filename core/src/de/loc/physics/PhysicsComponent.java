package de.loc.physics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import de.loc.graphics.ModelComponentCreator;

public class PhysicsComponent implements Component {

    public final BoundingBox modelBounds;
    public final BoundingBox bounds;
    public ModelInstance boundingModel;
    private final Vector3 boundMin;
    private final Vector3 boundMax;
    private final Vector3 position;
    public final Matrix4 m;

    private final Vector3 TRANS;
    private final float ROT;

    public PhysicsComponent(BoundingBox modelBounds) {
        this.modelBounds = modelBounds;
        this.bounds = new BoundingBox();
        this.boundMin = new Vector3();
        this.boundMax = new Vector3();
        this.position = new Vector3();
        this.m = new Matrix4();

        this.boundingModel = new ModelInstance(ModelComponentCreator.getInstance().createModelFromBounds(modelBounds));

        this.TRANS = new Vector3();
        this.ROT = 0.f;
    }

    public BoundingBox getBoundsMatrix() {
        this.modelBounds.getMin(this.boundMin);
        this.modelBounds.getMax(this.boundMax);
        //boundMin.mul(m);
        //boundMax.mul(m);
        this.bounds.set(this.boundMin, this.boundMax);

        this.bounds.mul(this.m);

        return this.bounds;
    }

    public void setPosition(Matrix4 m) {
        this.m.set(m);
    }

}
