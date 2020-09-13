package de.loc.combat;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import de.loc.core.GameGrid;
import de.loc.core.LevelManager;
import de.loc.event.Event;
import de.loc.event.EventSystem;
import de.loc.game.GameInputSystem;
import de.loc.game.ViewportSystem;
import de.loc.graphics.CameraManager;
import de.loc.input.InputComponent;
import de.loc.movement.EntityStateComponent;
import de.loc.movement.MovableComponent;
import de.loc.movement.MovementAISystem;
import de.loc.movement.MovementSystem;
import de.loc.movement.steering.SteeringAgent;
import de.loc.tools.Constants;
import de.loc.tools.IAction;
import de.loc.tools.IState;
import de.loc.tools.Position;
import de.loc.tools.StateMachine;

public class CombatState implements IState {

    private final CombatSystem combatSystem;

    //action queue
    private final LinkedList<IAction> actions;
    //am kampf beteiligte entities
    private final ArrayList<Entity> entities;

    //substate des combatstates
    private final StateMachine combatStates;
    static final String TICK_STATE = "tick";
    static final String EXECUTE_STATE = "execute";

    private final ComponentMapper<InputComponent> inputMapper;
    private final ComponentMapper<CombatComponent> combatMapper;
    private final ComponentMapper<EntityStateComponent> entityStateMapper;
    private final ComponentMapper<MovableComponent> movableMapper;

    private int combatExp;

    public CombatState(CombatSystem combatSystem) {
        this.combatSystem = combatSystem;

        this.actions = new LinkedList<>();

        this.entities = new ArrayList<>(Constants.MAX_ENEMIES + 1); // max enemies + player

        //zwei substates: tick und execute
        this.combatStates = new StateMachine();
        this.combatStates.add(TICK_STATE, new CombatTick(this.combatStates, this.actions)); //wartet ab bis die action an erster stelle packageListReady ist
        this.combatStates.add(EXECUTE_STATE, new CombatExecute(this.combatStates, this.actions)); //führt diese action aus
        this.combatStates.change(TICK_STATE); //anfangs im tick modus

        this.inputMapper = ComponentMapper.getFor(InputComponent.class);
        this.combatMapper = ComponentMapper.getFor(CombatComponent.class);
        this.entityStateMapper = ComponentMapper.getFor(EntityStateComponent.class);
        this.movableMapper = ComponentMapper.getFor(MovableComponent.class);
    }

    @Override
    public void update(float deltaTime) {
        //in jedem cycle prüfen ob ein teilnehmer gestorben ist, oder alle spieler/gegner tot sind
        this.checkForDeaths();

        //updaten der substates
        this.combatStates.update(deltaTime);

        this.checkForCombatOver();
    }

    @Override
    public void onEnter(Object... objects) {
        this.combatSystem.fire(EventSystem.EventType.COMBAT_STARTED);

        //fügt den/die spielercharaktere dem kampf hinzu
        for ( Entity playerEntity : this.combatSystem.getEngine().getEntitiesFor(Family.all(InputComponent.class).get()) ) {
            this.entities.add(playerEntity);
        }

        //fügt die vom waitforcombatstate übergebenen gegner dem kampf hinzu
        for ( Object o : objects ) {
            this.entities.add((Entity) o);
        }

        this.disableSystems(); //disabled systeme die während dem kampf nicht erwünscht sind

        this.setupCombatEntities();

        for ( Entity e : this.entities ) {
            if ( this.inputMapper.has(e) ) {
                //für jeden spielercharakter wird eine playerdecide action hinzugefügt
                this.actions.add(new PlayerDecide(e));
            } else {
                //für jeden gegner wird eine aidecide action hinzugefügt
                this.actions.add(new AIDecide(e, this.entities));
                //fügt jedem gegner einen attack button hinzu
                this.combatSystem.fire(EventSystem.EventType.COMBAT_ENEMY, e);
            }
        }
    }

    private void setupCombatEntities() {
        int i = 1;

        Position playerPos = new Position();
        Position enemyPos = new Position();

        for ( Entity entity : this.entities ) {
            this.entityStateMapper.get(entity).fighting = true; //entitystate von allen kampfteilnehmern wird gesetzt

            MovableComponent mC = this.movableMapper.get(entity);

            Position position;

            if ( this.inputMapper.has(entity) ) {
                position = CameraManager.getGridFromScreenCoords(Gdx.graphics.getWidth() / 3, Gdx.graphics.getHeight() / 2);

                position = this.findSuitablePosition(position);

                playerPos = position;
            } else {
                position = CameraManager.getGridFromScreenCoords((Gdx.graphics.getWidth() / 3) << 1, (Gdx.graphics.getHeight() / 3) * i);

                position = this.findSuitablePosition(position);

                enemyPos = position;

                i++;
            }
            mC.move(position);
        }
        this.enableFacing(playerPos.toVector2(), enemyPos.toVector2());
    }

    private void enableFacing(Vector2 playerPos, Vector2 enemyPos) {
        for ( Entity entity : this.entities ) {
            MovableComponent mC = this.movableMapper.get(entity);

            mC.steeringAgent.enableSteeringBehavior(SteeringAgent.Steering.FACE);

            if ( this.inputMapper.has(entity) ) {
                mC.steeringAgent.setTarget(enemyPos);
            } else {
                mC.steeringAgent.setTarget(playerPos);
            }
        }
    }

    private void disableFacing() {
        this.movableMapper.get(this.entities.get(0)).steeringAgent.disableSteeringBehavior(SteeringAgent.Steering.FACE);
    }

    private Position findSuitablePosition(Position position) {
        GameGrid gameGrid = LevelManager.getInstance().getLevel().getGameGrid();
        Position newPos = position;

        if ( newPos != null ) {
            while ( !gameGrid.isWalkable(newPos) ) {
                Position[] array = newPos.getAdjacents();

                for ( Position pos : array ) {
                    newPos = gameGrid.isWalkable(pos) ? pos : array[4];
                }
            }
        }

        return newPos;
    }

    private void disableSystems() {
        Engine engine = this.combatSystem.getEngine();
        engine.getSystem(MovementAISystem.class).setProcessing(false); //damit sich die gegner nicht bewegen
        engine.getSystem(GameInputSystem.class).combatMode = true;//damit der spieler keine items anklicken oder sich bewegen kann
        engine.getSystem(MovementSystem.class)
              .stopAllOtherEntities(this.entities); //damit alle anderen entities stehenbleiben, die kampfteilnehmer aber noch ihre position einnehmen können
        engine.getSystem(ViewportSystem.class).setProcessing(false);
    }

    @Override
    public void onExit() {
        this.disableFacing();
        this.enableSystems(); //schaltet die systeme wieder ein

        this.entityStateMapper.get(this.entities.get(0)).fighting = false;

        this.entities.clear(); //leert die kampfentities
        this.actions.clear();

        //entfernt alle combatui sachen
        this.combatSystem.fire(EventSystem.EventType.COMBAT_ENDED);
    }

    private void enableSystems() {
        Engine engine = this.combatSystem.getEngine();
        engine.getSystem(MovementAISystem.class).setProcessing(true);
        engine.getSystem(GameInputSystem.class).combatMode = false;
        engine.getSystem(GameInputSystem.class).resetClicked(); //damit während dem kampf außerhalb geklickte sachen resettet werden
        engine.getSystem(MovementSystem.class).startAllEntities();
        engine.getSystem(ViewportSystem.class).setProcessing(true);
    }

    private void checkForDeaths() {

        //muss iterator sein weil for each mit entfernen nicht funktioniert
        Iterator<Entity> entityIter = this.entities.iterator();

        //geht alle teilnehmer durch
        while ( entityIter.hasNext() ) {
            Entity entity = entityIter.next();

            //und entfernt sie wenn sie tot sind aus der liste und der engine
            if ( this.combatMapper.get(entity).curHealth <= 0 ) {
                entityIter.remove();

                //alle actions der entity entfernen, können ja nichts machen wenn sie tot sind
                Iterator<IAction> actionIter = this.actions.iterator();

                while ( actionIter.hasNext() ) {
                    if ( actionIter.next().getOwner().equals(entity) ) {
                        actionIter.remove();
                    }
                }

                this.combatExp += this.combatMapper.get(entity).level * 5; //TODO

                EventSystem.getInstance().commitEvent(new Event(EventSystem.EventType.KILL_EVENT, entity));

                this.combatSystem.getEngine().removeEntity(entity);
            }
        }
    }

    private void checkForCombatOver() {
        //zählt die anzahl an spielern und nicht spielern
        int numberOfPlayers = 0;
        int numberOfAi = 0;

        for ( Entity e : this.entities ) {
            if ( this.inputMapper.has(e) ) {
                numberOfPlayers++;
            } else {
                //TODO ai partner noch nicht möglich
                numberOfAi++;
            }
        }

        //wenn der spieler keine mehr hat gameover wenn der "gegner" keine mehr hat gewonnen
        if ( numberOfPlayers == 0 ) {
            this.gameOver();
        } else if ( numberOfAi == 0 ) {
            this.combatWon();
        }
    }

    private void gameOver() {
        this.combatSystem.fire(EventSystem.EventType.GAME_OVER);

        this.combatSystem.waitForCombat();
    }

    private void combatWon() {
        Gdx.app.log("COMBAT", "Kampf gewonnen! Du erhälst " + this.combatExp + " Erfahrung!");

        //der einzige der am ende übrig sein sollte ist der spieler
        this.combatMapper.get(this.entities.get(0)).experience += this.combatExp;

        //wechselt wieder in den anderen state
        this.combatSystem.waitForCombat();
    }
}
