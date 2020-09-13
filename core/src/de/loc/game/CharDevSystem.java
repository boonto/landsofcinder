package de.loc.game;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import de.loc.combat.CombatComponent;
import de.loc.core.LandsOfCinderSystem;
import de.loc.core.LevelManager;
import de.loc.event.Event;
import de.loc.event.EventListener;
import de.loc.event.EventSystem;

public class CharDevSystem extends LandsOfCinderSystem implements EventListener {

    private static final Map<Integer, Integer> REQUIREMENTS;

    static {
        Map<Integer, Integer> map = new TreeMap<>();
        map.put(1, 10);
        map.put(2, 50);
        map.put(3, 150);
        map.put(4, 500);
        REQUIREMENTS = Collections.unmodifiableMap(map);
    }

    private final ComponentMapper<CombatComponent> combatMapper;

    private Entity player;

    public CharDevSystem() {
        this.combatMapper = ComponentMapper.getFor(CombatComponent.class);

        EventSystem.getInstance().addListener(this, EventSystem.EventType.LEVELUP_ANSWER_EVENT);
    }

    @Override
    public void addedToEngine(Engine engine) {
        this.player = LevelManager.getInstance().getPlayerEntity();
    }

    @Override
    public void update(float deltaTime) {
        CombatComponent playerCC = this.combatMapper.get(this.player);

        if ( REQUIREMENTS.get(playerCC.level + 1) <= playerCC.experience ) {
            EventSystem.getInstance().commitEvent(new Event(EventSystem.EventType.LEVELUP_EVENT));
        }
    }

    private void levelUp() {
        CombatComponent cc = this.combatMapper.get(this.player);

        cc.level++;
        cc.vitality++;
    }

    @Override
    public void update(Event e) {
        this.levelUp();
    }

    @Override
    public void reset() {
        //do nothing
    }
}
