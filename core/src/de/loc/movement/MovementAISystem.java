package de.loc.movement;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;

import de.loc.core.LandsOfCinderSystem;
import de.loc.core.LevelManager;
import de.loc.core.PositionComponent;
import de.loc.event.Event;
import de.loc.event.EventListener;
import de.loc.event.EventSystem;

public class MovementAISystem extends LandsOfCinderSystem implements EventListener {

    private final ComponentMapper<MovableComponent> movableMapper;
    private final ComponentMapper<MovementAIComponent> movementAIMapper;
    private final ComponentMapper<PositionComponent> positionMapper;

    private final float interval;
    private float counter;

    private ImmutableArray<Entity> entities;

    public MovementAISystem(float interval) {
        this.interval = interval;

        this.movableMapper = ComponentMapper.getFor(MovableComponent.class);
        this.movementAIMapper = ComponentMapper.getFor(MovementAIComponent.class);
        this.positionMapper = ComponentMapper.getFor(PositionComponent.class);

        EventSystem.getInstance().addListener(this, EventSystem.EventType.DIALOG_STARTED, EventSystem.EventType.DIALOG_ENDED);
    }

    @Override
    public void addedToEngine(Engine engine) {
        this.entities = engine.getEntitiesFor(Family.all(MovementAIComponent.class, MovableComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        this.counter += deltaTime;

        for ( Entity entity : this.entities ) {
            MovementAIComponent movementAIComponent = this.movementAIMapper.get(entity);
            MovableComponent movableComponent = this.movableMapper.get(entity);

            if ( this.counter >= this.interval ) {
                if ( movementAIComponent.center != null ) {
                    int x = movementAIComponent.center.x;
                    int y = movementAIComponent.center.y;
                    int range = movementAIComponent.range;

                    x = MathUtils.random(x - (range / 2), x + (range / 2));
                    y = MathUtils.random(y - (range / 2), y + (range / 2));

                    if ( LevelManager.getInstance().getLevel().getGameGrid().isWalkable(x, y) ) {
                        movableComponent.move(x, y);
                    }
                }
            }
            if ( movementAIComponent.target != null ) {
                movableComponent.steeringAgent.setTarget(movementAIComponent.target);
            }
        }

        if ( this.counter >= this.interval ) {
            this.counter = 0.0f;
        }
    }

    @Override
    public void reset() {
        //do nothing
    }

    @Override
    public void update(Event e) {
        if ( e.eventType == EventSystem.EventType.DIALOG_STARTED ) {
            Entity entity = (Entity) e.args[2];
            if ( this.movementAIMapper.has(entity) ) {
                this.movementAIMapper.get(entity).target = this.positionMapper.get(LevelManager.getInstance().getPlayerEntity()).positionF;
            }
        } else if ( e.eventType == EventSystem.EventType.DIALOG_ENDED ) {
            Entity entity = (Entity) e.args[0];
            if ( entity != null && this.movementAIMapper.has(entity) ) {
                this.movementAIMapper.get(entity).target = null;
            }
        }
    }
}
