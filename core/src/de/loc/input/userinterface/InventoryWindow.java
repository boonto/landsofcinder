package de.loc.input.userinterface;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.loc.event.Event;
import de.loc.event.EventSystem;
import de.loc.item.InventoryComponent;
import de.loc.item.ItemComponent;
import de.loc.item.StatComponent;
import de.loc.tools.Constants;

public class InventoryWindow extends MenuWindow {

    private final ComponentMapper<ItemComponent> itemMapper;

    public InventoryWindow(UserInterface ui) {
        super(ui, Type.MIDDLE);
        this.itemMapper = ComponentMapper.getFor(ItemComponent.class);
    }

    // Wenn der Spieler eine Truhe oder etwas anderes das man looten kann angeklickt hat
    public void updateInventory(final InventoryComponent inventory, final InventoryComponent playerInventory) {
        this.mainTable.clearChildren();

        int index = 0;
        for ( Entity e : inventory.contents.values() ) {
            ItemComponent ic = this.itemMapper.get(e);
            String itemName = ic.name;
            String itemDescription = ic.description;
            String iconPath = ic.iconPath;

            String itemStat;
            if ( e.getComponent(StatComponent.class) != null ) {
                String statType = e.getComponent(StatComponent.class).stats.get(0).type.toString().toLowerCase();
                statType = Character.toString(statType.charAt(0)).toUpperCase() + statType.substring(1);
                String statValue = String.valueOf(e.getComponent(StatComponent.class).stats.get(0).value);
                itemStat = statType + " +" + statValue;
            } else {
                itemStat = "";
            }

            final Actor actor = this.createInventoryEntry(itemName, itemDescription, Constants.ICON_PATH + iconPath, itemStat, ic.getAmount(), itemName);
            actor.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println("INVENTORY_LIST: Item mit Id " + actor.getName() + " geklickt!");
                    //int i = Integer.valueOf(actor.getName());

                    playerInventory.addItem(inventory.contents.get(actor.getName()), 1);
                    // Todo: das FETCH_EVENT sollte eigentlich eher irgendwo zentral ausgeführt werden, und nicht
                    // mal hier mal da (ItemSystem, InventoryWindow) ... momentan weiß ich aber noch nicht wo das sein sollte
                    // also erstmal doch dezentral!
                    EventSystem.getInstance().commitEvent(new Event(EventSystem.EventType.FETCH_EVENT, actor.getName()));
                    inventory.removeItem(inventory.contents.get(actor.getName()), 1);
                    InventoryWindow.this.updateInventory(inventory, playerInventory);
                }
            });

            index++;
        }
    }

    // Zur Darstellung des Inventars des Spielers (andere Interaktion bei Klick)
    public void updateInventory(InventoryComponent playerInventory) {
        this.mainTable.clearChildren();

        for ( Entity e : playerInventory.contents.values() ) {
            ItemComponent ic = this.itemMapper.get(e);
            String itemName = ic.name;
            String itemDescription = ic.description;
            String iconPath = ic.iconPath;

            String itemStat;
            if ( e.getComponent(StatComponent.class) != null ) {
                String statType = e.getComponent(StatComponent.class).stats.get(0).type.toString().toLowerCase();
                statType = Character.toString(statType.charAt(0)).toUpperCase() + statType.substring(1);
                String statValue = String.valueOf(e.getComponent(StatComponent.class).stats.get(0).value);
                itemStat = statType + " +" + statValue;
            } else {
                itemStat = "";
            }
            final Actor actor = this.createInventoryEntry(itemName, itemDescription, Constants.ICON_PATH + iconPath, itemStat, ic.getAmount(), itemName);
            actor.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    InventoryWindow.this.ui.getInputHandler().handle(actor.getName());
                }
            });
            //addInventoryEntry(itemName, itemDescription, Constants.ICON_PATH + iconPath, itemStat, ic.getAmount(), "inventory_" + Long.toString(e.getId()));
        }
    }

}
