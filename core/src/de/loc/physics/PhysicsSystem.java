package de.loc.physics;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector3;

import de.loc.core.LandsOfCinderSystem;
import de.loc.graphics.ModelComponent;
import de.loc.graphics.RotationComponent;

public class PhysicsSystem extends LandsOfCinderSystem {

    private ImmutableArray<Entity> physicsEntities;
    private ImmutableArray<Entity> rotationEntities;

    private final ComponentMapper<ModelComponent> positionMapper;
    private final ComponentMapper<PhysicsComponent> physicsMapper;
    private final ComponentMapper<RotationComponent> rotationMapper;

    private final Vector3 ROTATION = new Vector3(0.0f, 1.0f, 0.0f);

    public PhysicsSystem() {

        this.positionMapper = ComponentMapper.getFor(ModelComponent.class);
        this.physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);
        this.rotationMapper = ComponentMapper.getFor(RotationComponent.class);
    }

    public void addedToEngine(Engine engine) {
        this.physicsEntities = engine.getEntitiesFor(Family.all(ModelComponent.class, PhysicsComponent.class).exclude(RotationComponent.class).get());

        this.rotationEntities = engine.getEntitiesFor(Family.all(ModelComponent.class, PhysicsComponent.class, RotationComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        for ( Entity entity : this.physicsEntities ) {
            ModelComponent p = this.positionMapper.get(entity);
            PhysicsComponent ph = this.physicsMapper.get(entity);
            ph.setPosition(p.model.transform);
        }
        for ( Entity entity : this.rotationEntities ) {
            ModelComponent p = this.positionMapper.get(entity);
            PhysicsComponent ph = this.physicsMapper.get(entity);
            RotationComponent r = this.rotationMapper.get(entity);
            //MAT4.setToRotationRad(ROTATION, r.angle);
            //MAT4.translate(position);
            ph.setPosition(p.model.transform);

        }
    }

    @Override
    public void reset() {
        //do nothing
    }
}
