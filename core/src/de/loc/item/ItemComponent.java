package de.loc.item;

import com.badlogic.ashley.core.Component;

public class ItemComponent implements Component {

    public final String name;
    public final String description;
    public final String iconPath;

    private int amount;

    public ItemComponent(String name, String description, String iconPath) {
        this.name = name;
        this.description = description;
        this.iconPath = iconPath;
        this.amount = 1;
    }

    public void increaseAmount(int count) {
        this.amount += count;
    }

    public void decreaseAmount(int count) {
        this.amount -= count;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int count) {
        this.amount = count;
    }

}
