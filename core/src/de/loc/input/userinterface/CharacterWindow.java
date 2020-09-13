package de.loc.input.userinterface;

import com.badlogic.ashley.core.Entity;

import java.util.Map;

import de.loc.event.Event;
import de.loc.event.EventListener;
import de.loc.item.EquipmentComponent;
import de.loc.item.ItemComponent;

public class CharacterWindow extends MenuWindow {

    EquipmentComponent equipmentComponent;

    public CharacterWindow(UserInterface ui, EquipmentComponent equipmentComponent) {
        super(ui, Type.RIGHT);

        this.equipmentComponent = equipmentComponent;

        equipmentComponent.addListener(new EventListener() {
            @Override
            public void update(Event e) {
                CharacterWindow.this.updateEquipment();
            }
        });
    }

    private void updateEquipment() {
        this.mainTable.clearChildren();

        for ( Map.Entry pair : this.equipmentComponent.equipment.entrySet() ) {

            if ( pair.getValue() == null ) {
                //TODO unequip
                this.addLabelItem(pair.getKey() + ":\n Nichts", "unequip");
            } else {
                this.addLabelItem(pair.getKey() + ":\n " + ((Entity) pair.getValue()).getComponent(ItemComponent.class).name, "unequip");
            }
        }
    }
}
