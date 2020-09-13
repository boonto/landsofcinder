package de.loc.combat;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;

import de.loc.core.PositionComponent;
import de.loc.core.TypeComponent;
import de.loc.event.Event;
import de.loc.event.EventSystem;
import de.loc.item.Stat;
import de.loc.item.StatManager;
import de.loc.movement.MovableComponent;
import de.loc.tools.Constants;
import de.loc.tools.IAction;
import de.loc.tools.Pair;

public class Skill implements IAction {

    private final Type skill;
    private final Entity attacker;
    private final List<Entity> defenders;
    private boolean ready;

    private final ComponentMapper<CombatComponent> combatMapper;
    private final ComponentMapper<MovableComponent> movableMapper;
    private final ComponentMapper<PositionComponent> positionMapper;

    private Pair<String, ArrayList<Stat>> skillData;

    public Skill(Type skill, Entity attacker, List<Entity> defenders) {
        this.skill = skill;
        this.attacker = attacker;
        this.defenders = defenders;

        this.combatMapper = ComponentMapper.getFor(CombatComponent.class);
        this.movableMapper = ComponentMapper.getFor(MovableComponent.class);
        this.positionMapper = ComponentMapper.getFor(PositionComponent.class);
    }

    @Override
    public void update(float deltaTime) {
        this.ready = true;
    }

    @Override
    public void resolve() {
        this.ready = false;

        // attacker schaut den defender an
        this.movableMapper.get(this.attacker).steeringAgent.setTarget(this.positionMapper.get(this.defenders.get(0)).positionF);

        CombatComponent attackerCC = this.combatMapper.get(this.attacker);
        ArrayList<CombatComponent> defenderCCs = new ArrayList<CombatComponent>(Constants.MAX_ENEMIES);
        for ( Entity defender : this.defenders ) {
            defenderCCs.add(this.combatMapper.get(defender));
        }

        this.skillData = SkillParser.parseSkill(this.skill.toString());

        ArrayList<Stat> statList = this.skillData.getRight();
        int damage = StatManager.applyStatsToCombatComponents(statList, attackerCC, defenderCCs);

        for ( Entity defender : this.defenders ) {
            EventSystem.getInstance().commitEvent(new Event(EventSystem.EventType.ATTACK_EVENT, this.attacker, defender, this.skill));

            Gdx.app.log("COMBAT",
                        this.attacker.getComponent(TypeComponent.class).name
                        + " greift "
                        + defender.getComponent(TypeComponent.class).name
                        + " mit "
                        + this.skillData.getLeft()
                        + " an!");
            Gdx.app.log("COMBAT", "Es verursacht " + damage + " Schaden!");
        }
    }

    @Override
    public boolean isReady() {
        return this.ready;
    }

    @Override
    public Entity getOwner() {
        return this.attacker;
    }

    @Override
    public ArrayList<Pair<Skill.Type, ArrayList<Entity>>> getDecisions() {
        return null;
    }

    public enum Type {
        ATTACK("Attack"),
        UPPERCUT("Uppercut"),
        DAMPFSTRAHL("Dampfstrahl"),
        EQUIP("Equip"),
        CONSUME("Consume"),
        BISS("Biss");

        private final String text;

        Type(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return this.text;
        }

        public static Type fromString(String text) {
            for ( Type type : values() ) {
                if ( type.toString().equals(text) ) {
                    return type;
                }
            }
            return null;
        }
    }
}
