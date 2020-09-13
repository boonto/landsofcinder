package de.loc.combat;

import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.Set;

import de.loc.input.InputComponent;
import de.loc.tools.IAction;
import de.loc.tools.Pair;

public class AIDecide implements IAction {

    private final Entity owner;
    private final ArrayList<Pair<Skill.Type, ArrayList<Entity>>> decisions;
    private final ArrayList<Entity> entities;
    private boolean ready;

    public AIDecide(Entity owner, ArrayList<Entity> entities) {
        this.decisions = new ArrayList<>(5);
        this.owner = owner;
        this.entities = entities;
    }

    @Override
    public void update(float deltaTime) {
        this.ready = true;
    }

    @Override
    public void resolve() {
        this.ready = false;
        this.decisions.clear();

        ArrayList<Entity> temp = new ArrayList<>();

        for ( Entity e : this.entities ) {
            //TODO
            if ( e.getComponent(InputComponent.class) != null ) {
                temp.add(e);
            }
        }

        ArrayList<Entity> targets = new ArrayList<>(2);
        //TODO gscheit
        targets.add(temp.get(0));

        Set<Skill.Type> availableSkills = this.owner.getComponent(CombatComponent.class).skillList.keySet();
        for ( Skill.Type skill : availableSkills ) {
            this.decisions.add(new Pair<>(skill, targets));
        }
    }

    @Override
    public boolean isReady() {
        return this.ready;
    }

    @Override
    public ArrayList<Pair<Skill.Type, ArrayList<Entity>>> getDecisions() {
        return this.decisions;
    }

    @Override
    public Entity getOwner() {
        return this.owner;
    }
}
