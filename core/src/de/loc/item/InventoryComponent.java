package de.loc.item;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import java.util.HashMap;

public class InventoryComponent implements Component {

    public HashMap<String, Entity> contents;

    public InventoryComponent(Entity... items) {
        this.contents = new HashMap<>();
        for ( Entity item : items ) {
            this.contents.put(item.getComponent(ItemComponent.class).name, item);
        }
    }

    public void addItem(Entity item, int amount) {
        ItemComponent ic = item.getComponent(ItemComponent.class);
        // if inventory contains the item, increase it's amount
        if ( this.contents.containsKey(ic.name) ) {
            Entity containingItem = this.contents.get(ic.name);
            containingItem.getComponent(ItemComponent.class).increaseAmount(amount);
        } else {
            // else: create a new entity, set the amount and add item to inventory
            Entity newItem = new Entity();
            //TODO: Copy constructor?! There may be a better solution!
            for ( Component c : item.getComponents() ) {
                newItem.add(c);
            }
            ItemComponent newItemComponent = new ItemComponent(ic.name, ic.description, ic.iconPath);
            newItemComponent.setAmount(amount);
            newItem.add(newItemComponent);

            this.contents.put(newItemComponent.name, newItem);
        }
    }

    public void removeItem(Entity item, int amount) {
        ItemComponent ic = item.getComponent(ItemComponent.class);
        // if inventory contains the item, decrease it's amount
        if ( this.contents.containsKey(ic.name) ) {
            Entity containingItem = this.contents.get(ic.name);
            ItemComponent containgItemComponent = containingItem.getComponent(ItemComponent.class);
            containgItemComponent.decreaseAmount(amount);
            // remove the Item completely, if amount is zero
            if ( containgItemComponent.getAmount() <= 0 ) {
                this.contents.remove(containgItemComponent.name);
            }
        }

    }
}
