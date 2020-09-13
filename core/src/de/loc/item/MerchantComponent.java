package de.loc.item;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import java.util.HashMap;

public class MerchantComponent implements Component {
    public InventoryComponent merchantInventory;
    public HashMap<String, Integer> priceList;

    public MerchantComponent() {
        this.merchantInventory = new InventoryComponent();
        this.priceList = new HashMap<>();
    }

    public void addItem(Entity item, int amount, int price) {
        this.merchantInventory.addItem(item, amount);
        this.priceList.put(item.getComponent(ItemComponent.class).name, price);
    }
}
