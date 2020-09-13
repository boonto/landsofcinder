package de.loc.movement;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.PathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.loc.core.LandsOfCinderSystem;
import de.loc.core.LevelBackground;
import de.loc.core.LevelManager;
import de.loc.core.PositionComponent;
import de.loc.event.Event;
import de.loc.event.EventListener;
import de.loc.event.EventSystem;
import de.loc.graphics.RotationComponent;
import de.loc.movement.pathfinding.GraphPathImp;
import de.loc.movement.pathfinding.HeuristicImp;
import de.loc.movement.pathfinding.Node;
import de.loc.movement.steering.SteeringAgent;
import de.loc.tools.Constants;
import de.loc.tools.DimensionHelper;
import de.loc.tools.Position;

public class MovementSystem extends LandsOfCinderSystem implements EventListener {

    private final ComponentMapper<PositionComponent> positionMapper;
    private final ComponentMapper<MovableComponent> movableMapper;
    private final ComponentMapper<RotationComponent> rotationMapper;
    private final ComponentMapper<EntityStateComponent> entityStateMapper;

    private final HeuristicImp heuristic;

    private final Array<Steerable<Vector2>> agents;

    private ImmutableArray<Entity> entities;

    private boolean resetDeltaTime;

    public MovementSystem() {
        this.positionMapper = ComponentMapper.getFor(PositionComponent.class);
        this.movableMapper = ComponentMapper.getFor(MovableComponent.class);
        this.rotationMapper = ComponentMapper.getFor(RotationComponent.class);
        this.entityStateMapper = ComponentMapper.getFor(EntityStateComponent.class);

        this.heuristic = new HeuristicImp();

        this.agents = new Array<>();

        EventSystem.getInstance().addListener(this, EventSystem.EventType.KILL_EVENT);
    }

    @Override
    public void addedToEngine(Engine engine) {
        this.entities = engine.getEntitiesFor(Family.all(PositionComponent.class, MovableComponent.class, RotationComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        if ( this.resetDeltaTime ) {
            deltaTime = 0.0f;
            this.resetDeltaTime = false;
        }

        GdxAI.getTimepiece().update(deltaTime);

        for ( Entity entity : this.entities ) {
            MovableComponent movableComponent = this.movableMapper.get(entity);

            if ( !movableComponent.stopped ) {
                PositionComponent positionComponent = this.positionMapper.get(entity);
                RotationComponent rotationComponent = this.rotationMapper.get(entity);
                SteeringAgent steeringAgent = movableComponent.steeringAgent;

                // CollisionAvoidance: Alle SteeringAgents dem Array hinzufügen die noch nicht drin sind
                if ( !this.agents.contains(steeringAgent, true) ) {
                    this.movableMapper.get(entity).steeringAgent.setProximityAgents(this.agents);
                    this.agents.add(steeringAgent);
                }

                if ( movableComponent.changed ) {
                    movableComponent.changed = false;

                    if ( !positionComponent.position.equals(movableComponent.position) ) {
                        if ( LevelManager.getInstance().getLevel().getGameGrid().isInside(positionComponent.position) ) {
                            this.generatePath(positionComponent.position, movableComponent.position, movableComponent.waypoints);

                            steeringAgent.setPath(movableComponent.waypoints);
                        }
                    }
                }

                this.setEntityState(entity, steeringAgent);

                // übergeben vom rotationcomp, weil der angle an sich nicht by reference übergeben werden kann
                steeringAgent.update(deltaTime, positionComponent.position, positionComponent.positionF, rotationComponent);
            }
        }
    }

    private void generatePath(Position startPosition, Position endPosition, Array<Vector2> waypoints) {
        LevelBackground level = LevelManager.getInstance().getLevel();

        level.generateGraph();
        PathFinder<Node> pathFinder = new IndexedAStarPathFinder<Node>(level.getGraph(), false);

        Node startNode = level.getGraph().getNodeByPosition(startPosition);
        Node endNode = level.getGraph().getNodeByPosition(endPosition);

        GraphPath<Node> path = new GraphPathImp();

        if ( pathFinder.searchNodePath(startNode, endNode, this.heuristic, path) ) {
            waypoints.clear();

            for ( Node node : path ) {
                Position vec = (DimensionHelper.ind2sub(node.getIndex(), level.getGameGrid().getSize().x));
                waypoints.add(new Vector2((float) vec.x, (float) vec.y));
            }
        }
    }

    public void stopAllOtherEntities(Iterable<Entity> ents) {
        for ( Entity e : this.entities ) {
            this.movableMapper.get(e).stopped = true;
        }

        for ( Entity e : ents ) {
            this.movableMapper.get(e).stopped = false;
        }
    }

    public void startAllEntities() {
        for ( Entity e : this.entities ) {
            this.movableMapper.get(e).stopped = false;
        }
    }

    @Override
    public void update(Event e) {
        this.removeFromAgents((Entity) e.args[0]);
    }

    public void removeFromAgents(Entity entity) {
        if ( this.movableMapper.has(entity) ) {
            SteeringAgent steeringAgent = this.movableMapper.get(entity).steeringAgent;
            this.agents.removeValue(steeringAgent, true);
        }
    }

    @Override
    public void reset() {
        this.agents.clear();
        this.resetDeltaTime = true;
    }

    private void setEntityState(Entity entity, Steerable<Vector2> steeringAgent) {
        EntityStateComponent entityState = this.entityStateMapper.get(entity);

        if ( steeringAgent.getLinearVelocity().len2() < Constants.VELOCITY_WALKING ) {
            if ( entityState.fighting ) {
                entityState.fight();
            } else {
                entityState.stand();
            }
        } else if ( steeringAgent.getLinearVelocity().len2() < Constants.VELOCITY_RUNNING ) {
            entityState.walk();
        } else {
            entityState.run();
        }
    }
}
