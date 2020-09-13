package de.loc.physics;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;

public class PhysicsGenerator implements Disposable {

    private static final HashMap<Model, BoundingBox> boundMap = new HashMap<>();

    private PhysicsGenerator() {
    }

    private static final class PhysicsGeneratorHolder {
        static final PhysicsGenerator physicsGenerator = new PhysicsGenerator();
    }

    public static PhysicsGenerator getInstance() {
        return PhysicsGeneratorHolder.physicsGenerator;
    }

    public PhysicsComponent createPhysicsComponentForNpcs() {
        BoundingBox bb = new BoundingBox(new Vector3(0.3f, 0.0f, 0.3f), new Vector3(-0.3f, 1.75f, -0.3f));
        return new PhysicsComponent(bb);
    }

    public PhysicsComponent createPhysicsComponent(Model m) {

        BoundingBox bb = boundMap.get(m);

        if ( bb != null ) {
            return new PhysicsComponent(bb);
        } else {
            bb = new BoundingBox();
            m.calculateBoundingBox(bb);
            bb.mul(new Matrix4().setToScaling(0.5f, 1.0f, 1.0f));
            boundMap.put(m, bb);
            return new PhysicsComponent(bb);
        }
    }

    @Override
    public void dispose() {
        //TODO
        boundMap.clear();
    }

}
