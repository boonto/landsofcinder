package de.loc.game;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

import de.loc.combat.CombatComponent;
import de.loc.core.LandsOfCinderSystem;
import de.loc.core.LevelManager;
import de.loc.core.PositionComponent;
import de.loc.event.Event;
import de.loc.event.EventSystem;
import de.loc.input.ClickableComponent;
import de.loc.input.IInput;
import de.loc.input.InputComponent;
import de.loc.input.InteractableComponent;
import de.loc.movement.MovableComponent;
import de.loc.physics.PhysicsComponent;
import de.loc.tools.Position;

public class GameInputSystem extends LandsOfCinderSystem implements IInput {

    // wird gebraucht damit update nicht mehrfach für die selbe Position aufgerufen wird:
    private boolean clicked = false;

    private Position position;
    private Ray pickRay;

    private ImmutableArray<Entity> clickableEntities;
    private ImmutableArray<Entity> interactableEntities;

    private final ComponentMapper<ClickableComponent> clickableMapper;
    private final ComponentMapper<MovableComponent> movableMapper;
    private final ComponentMapper<PositionComponent> positionMapper;
    private final ComponentMapper<PhysicsComponent> physicsMapper;

    public boolean combatMode = false; //TODO !!!

    public GameInputSystem() {
        this.position = new Position();

        this.clickableMapper = ComponentMapper.getFor(ClickableComponent.class);
        this.movableMapper = ComponentMapper.getFor(MovableComponent.class);
        this.positionMapper = ComponentMapper.getFor(PositionComponent.class);
        this.physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);
    }

    public void addedToEngine(Engine engine) {
        this.clickableEntities = engine.getEntitiesFor(Family.all(PositionComponent.class, ClickableComponent.class, PhysicsComponent.class).get());

        this.interactableEntities =
            engine.getEntitiesFor(Family.all(PositionComponent.class, ClickableComponent.class, PhysicsComponent.class, InteractableComponent.class).get());
    }

    public void update(float deltaTime) {

        //wenn geklickt wurde die geklickte entity auf markiert setzten oder dorthin bewegen
        if ( this.clicked ) {

            Entity interactableEntity = null;
            for ( Entity entity : this.interactableEntities ) {
                PhysicsComponent physicsComponent = this.physicsMapper.get(entity);
                ClickableComponent clickComp = this.clickableMapper.get(entity);

                //alle entities demarkieren
                clickComp.marked = false;

                if ( this.intersect(physicsComponent) ) {
                    interactableEntity = entity;
                    //entity markieren
                    clickComp.clicked = true;
                    System.out.println("Entity interagiert");
                    // damit der Spieler nicht noch irgendwo hin rennt wird hier 
                    // clicked auf false gesetzt und dann returned! ACHTUNG!!!
                    this.clicked = false;
                    return;
                }
            }

            Entity clickedEntity = null;
            //herausfinden welche entity geklickt wurde
            for ( Entity entity : this.clickableEntities ) {
                PhysicsComponent physicsComponent = this.physicsMapper.get(entity);
                ClickableComponent clickComp = this.clickableMapper.get(entity);

                //alle entities demarkieren
                clickComp.marked = false;

                if ( this.intersect(physicsComponent) ) {
                    clickedEntity = entity;
                    //entity markieren
                    clickComp.marked = true;
                    System.out.println("Entity angeklickt");
                    this.clicked = false;

                }
            }

            //TODO: irgendwie schöner!!!!!
            if ( this.combatMode ) {
                for ( Entity entity : this.clickableEntities ) {
                    if ( this.clickableMapper.get(entity).marked ) {
                        if ( (entity.getComponent(CombatComponent.class) != null) && (entity.getComponent(InputComponent.class) == null) ) {
                            EventSystem.getInstance().commitEvent(new Event(EventSystem.EventType.COMBAT_ENTITY_CLICKED, entity));
                        }
                    }
                }
                this.clicked = false;
                return;
            } //TODO OH GOOTT

            if ( clickedEntity == null ) {
                //wenn keine entity geklickt wurde hinlaufen
                this.movePlayer();
            } else {
                Position entityPosition = this.positionMapper.get(clickedEntity).position;
                Position playerPosition = this.positionMapper.get(LevelManager.getInstance().getPlayerEntity()).position;

                //wenn eine geklickt wurde nebendran laufen
                this.movePlayerAdjacent(playerPosition, entityPosition);
            }

            //clicked zurücksetzen
            this.clicked = false;
        }

        //clickable entities überprüfen ob sie markiert sind und der charakter sich daneben befindet
        for ( Entity entity : this.clickableEntities ) {
            Position entityPosition = this.positionMapper.get(entity).position;
            Position playerPosition = this.positionMapper.get(LevelManager.getInstance().getPlayerEntity()).position;
            ClickableComponent clickComp = this.clickableMapper.get(entity);

            if ( clickComp.marked && playerPosition.adjacentTo(entityPosition) ) {
                //wenn der charakter daneben ist interagieren
                clickComp.clicked = true;
                clickComp.marked = false;
            } else {
                //wenn nicht oder demarkiert wurde dann clicked zurücksetzten
                clickComp.clicked = false;
            }
        }
    }

    private boolean movePlayer() {
        if ( LevelManager.getInstance().getLevel().getGameGrid().isWalkable(this.position) && LevelManager.getInstance().hasPlayer() ) {
            MovableComponent movableComponent = this.movableMapper.get(LevelManager.getInstance().getPlayerEntity());
            movableComponent.move(this.position.x, this.position.y);

            return true;
        }

        return false;
    }

    private void movePlayerAdjacent(Position playerPosition, Position entityPosition) {
        //wenn der spieler bereits neben der entity steht nichts machen
        if ( playerPosition.adjacentTo(entityPosition) ) {
            return;
        }

        //die richtung von der aus das item zuerst benutzt werden soll
        Position direction = playerPosition.subtract(entityPosition);
        direction.direct();

        //die richtung wird auf die geklickte position angewandt und der spieler soll hinlaufen
        this.position = this.position.add(direction);

        //wenn der spieler sich nicht erfolgreich bewegen konnte, dh das die ausgerechnete zwischenposition falsch war
        if ( !this.movePlayer() ) {
            //werden sich ausgehend von der ersten zwischenposition die anderen anliegenden position geholt und ausprobiert
            Position[] array = entityPosition.getOtherAdjacents(direction);
            for ( Position value : array ) {
                this.position = value;

                if ( this.movePlayer() ) {
                    return;
                }
            }
        }
    }

    private boolean intersect(PhysicsComponent physicsComponent) {
        BoundingBox bb = physicsComponent.getBoundsMatrix();
        return Intersector.intersectRayBoundsFast(this.pickRay, bb);
    }

    public void clicked(Ray pickRay, Position position) {
        this.pickRay = pickRay;
        this.position = position;

        if ( position != null ) {
            this.clicked = true;
        }

    }

    public void resetClicked() {
        this.clicked = false;
    }

    public void dragged(Position position) {

    }

    @Override
    public void reset() {

    }

    public void rightClick() {
        Gdx.app.log("INPUT", "RightClick: currently no action!");
    }
}
