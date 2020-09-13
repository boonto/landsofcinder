package de.loc.item;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.loc.event.Event;
import de.loc.event.EventListener;
import de.loc.event.EventSystem;
import de.loc.event.Observable;

public class EquipmentComponent implements Component, Observable {
    //TODO vielleicht schöner?
    private final List<EventListener> listeners;

    public HashMap<String, Entity> equipment;

    public EquipmentComponent() {
        this.listeners = new ArrayList<>();

        this.equipment = new HashMap<>();

        this.equipment.put("weaponLeft", null);
        this.equipment.put("weaponRight", null);
        this.equipment.put("armor", null);
    }

    //rüstet das item aus und liefert das zuletzt getragene zurück, kann null returnen
    public Entity equip(Entity entity) {
        Entity tempEntity = null;

        switch ( entity.getComponent(EquippableComponent.class).type ) {
            case WEAPON_LEFT:
                tempEntity = this.equipment.get("weaponLeft");
                this.equipment.put("weaponLeft", entity);
                break;
            case WEAPON_RIGHT:
                tempEntity = this.equipment.get("weaponRight");
                this.equipment.put("weaponRight", entity);
                break;
            case ARMOR:
                tempEntity = this.equipment.get("armor");
                this.equipment.put("armor", entity);
                break;
            default:
                break;
        }
        this.fire(null);

        return tempEntity;
    }

    @Override
    public void addListener(EventListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(EventListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void fire(EventSystem.EventType eventType, Object... args) {
        for ( EventListener listener : this.listeners ) {
            listener.update(new Event(eventType));
        }
    }
}
