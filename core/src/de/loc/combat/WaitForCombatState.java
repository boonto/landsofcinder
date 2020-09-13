package de.loc.combat;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;

import java.util.ArrayList;
import java.util.List;

import de.loc.core.PositionComponent;
import de.loc.input.ClickableComponent;
import de.loc.tools.Constants;
import de.loc.tools.IState;
import de.loc.tools.Position;

public class WaitForCombatState implements IState {

    private final CombatSystem combatSystem;
    private ImmutableArray<Entity> combatEntities;

    private final ComponentMapper<ClickableComponent> clickableMapper;
    private final ComponentMapper<PositionComponent> positionMapper;

    public WaitForCombatState(CombatSystem combatSystem) {
        this.combatSystem = combatSystem;

        this.clickableMapper = ComponentMapper.getFor(ClickableComponent.class);
        this.positionMapper = ComponentMapper.getFor(PositionComponent.class);
    }

    @Override
    public void update(float deltaTime) {
        //wartet darauf das eine combatentity geklickt wurde und wechselt dann in den combatstate
        for ( Entity e : this.combatEntities ) {
            if ( this.clickableMapper.get(e).clicked ) {
                this.combatSystem.combat(this.getNearbyEnemies(e));
            }
        }
    }

    private Object[] getNearbyEnemies(Entity e) {
        List<Entity> enemies = new ArrayList<>();
        Position clickedEnemyPos = this.positionMapper.get(e).position;

        for ( int i = 0; i < this.combatEntities.size(); ++i ) {
            Entity enemy = this.combatEntities.get(i);
            Position enemyPos = this.positionMapper.get(enemy).position;

            if ( enemies.size() < Constants.MAX_ENEMIES ) {
                if ( enemyPos.inVicinity(clickedEnemyPos, Constants.AGGRO_RANGE) ) {
                    enemies.add(enemy);
                }
            }
        }

        return enemies.toArray();
    }

    @Override
    public void onEnter(Object... objects) {
        this.combatEntities = (ImmutableArray<Entity>) objects[0];
    }

    @Override
    public void onExit() {

    }
}
