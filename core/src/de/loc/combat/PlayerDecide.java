package de.loc.combat;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;

import de.loc.event.Event;
import de.loc.event.EventListener;
import de.loc.event.EventSystem;
import de.loc.tools.IAction;
import de.loc.tools.Pair;

public class PlayerDecide implements IAction, EventListener {

    private final Entity owner;
    private ArrayList<Pair<Skill.Type, ArrayList<Entity>>> decisions;

    private final ComponentMapper<CombatComponent> combatMapper;

    private boolean ready;

    public PlayerDecide(Entity owner) {
        this.owner = owner;
        this.decisions = new ArrayList<>(5);
        this.combatMapper = ComponentMapper.getFor(CombatComponent.class);

        EventSystem.getInstance().addListener(this, EventSystem.EventType.ATTACK_CLICKED);
    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void resolve() {
        this.ready = false;
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

    @Override
    public void update(Event e) {
        this.ready = true;

        this.decisions = (ArrayList<Pair<Skill.Type, ArrayList<Entity>>>) e.args[0];
    }
}
